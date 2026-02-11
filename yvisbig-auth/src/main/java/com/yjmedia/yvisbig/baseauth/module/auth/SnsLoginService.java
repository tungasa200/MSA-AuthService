package com.yjmedia.yvisbig.baseauth.module.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yjmedia.yvisbig.baseauth.config.MediaProperties;
import com.yjmedia.yvisbig.baseauth.config.SnsProviderProperties;
import com.yjmedia.yvisbig.baseauth.voProtocol.SnsUserDTO;
import com.yjmedia.yvisbig.baseauth.voProtocol.UserLoginResVO;
import com.yjmedia.yvisbig.bizcom.exception.ErrorType;
import com.yjmedia.yvisbig.bizcom.exception.ServerBizException;
import com.yjmedia.yvisbig.bizcom.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * SNS OAuth2 로그인 서비스
 * OAuth2 인가 URL 생성, 콜백 처리, SNS 사용자 생성/조회, JWT 발급
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SnsLoginService {

    private final SnsProviderProperties snsProviderProperties;
    private final SnsUserRepository snsUserRepository;
    private final UserLoginRepository userLoginRepository;
    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final MediaProperties mediaProperties;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${jwt.token-validity-in-seconds:3600}")
    private long accessTokenValidityInSeconds;

    @Value("${spring.redis.keyprefix:LOCAL}")
    private String keyPrefix;

    @Value("${server.port:8085}")
    private int serverPort;

    @Value("${sns.default-redirect-uri:}")
    private String defaultRedirectUri;

    private static final String SNS_STATE_PREFIX = "sns_state:";
    private static final long STATE_EXPIRY_SECONDS = 600; // 10분

    private final RestTemplate restTemplate = new RestTemplate();

    // ──────────────────────────────────────────────
    // 1. 인가 URL 생성
    // ──────────────────────────────────────────────

    /**
     * OAuth2 인가 URL을 생성하고 state를 Redis에 저장
     *
     * @param provider    프로바이더명 (kakao, naver, google, facebook)
     * @param redirectUri 인증 완료 후 리다이렉트할 서비스 URL
     * @return 프로바이더 인가 페이지 URL
     */
    public String buildAuthorizationUrl(String provider, String redirectUri) {
        SnsProviderProperties.ProviderConfig config = getProviderConfig(provider);

        // state 생성 및 Redis 저장
        String state = UUID.randomUUID().toString();
        saveState(state, provider, redirectUri);

        String callbackUrl = buildCallbackUrl(provider);

        StringBuilder url = new StringBuilder(config.getAuthorizationUri());
        url.append("?response_type=code");
        url.append("&client_id=").append(encode(config.getClientId()));
        url.append("&redirect_uri=").append(encode(callbackUrl));
        url.append("&state=").append(encode(state));

        // 프로바이더별 scope 추가
        if (StringUtils.hasText(config.getScope())) {
            url.append("&scope=").append(encode(config.getScope()));
        } else if ("naver".equals(provider)) {
            // Naver는 기본 profile scope
        }

        log.info("SNS authorize URL built: provider={}, state={}", provider, state);
        return url.toString();
    }

    // ──────────────────────────────────────────────
    // 2. 콜백 처리
    // ──────────────────────────────────────────────

    /**
     * OAuth2 콜백 처리: 코드 교환 → 사용자 정보 조회 → 사용자 생성/조회 → JWT 발급
     *
     * @param provider 프로바이더명
     * @param code     인가 코드
     * @param state    state 파라미터
     * @return 로그인 응답 (JWT 토큰 포함) + redirectUri
     */
    @Transactional
    public SnsCallbackResult handleCallback(String provider, String code, String state) {
        // 1. state 검증 및 redirectUri 복원
        StateInfo stateInfo = loadAndDeleteState(state);
        if (stateInfo == null || !provider.equals(stateInfo.provider)) {
            log.warn("Invalid SNS state: provider={}, state={}", provider, state);
            throw new ServerBizException(ErrorType.JWT_SNS_INVALID_STATE);
        }

        SnsProviderProperties.ProviderConfig config = getProviderConfig(provider);
        String callbackUrl = buildCallbackUrl(provider);

        // 2. Authorization code → Access Token 교환
        Map<String, Object> tokenResponse = exchangeCodeForToken(provider, config, code, callbackUrl);
        String accessToken = (String) tokenResponse.get("access_token");
        if (!StringUtils.hasText(accessToken)) {
            log.error("Failed to get access token from {}: {}", provider, tokenResponse);
            throw new ServerBizException(ErrorType.JWT_SNS_AUTH_FAILED);
        }

        // 3. Access Token → 사용자 정보 조회
        Map<String, Object> userInfoResponse = getUserInfo(provider, config, accessToken);

        // 4. 프로바이더별 사용자 정보 파싱
        SnsUserDTO snsUser = parseUserInfo(provider, userInfoResponse, accessToken);

        // 5. 네이버 필수 동의 체크
        if ("naver".equals(provider)) {
            if (!StringUtils.hasText(snsUser.getName()) || !StringUtils.hasText(snsUser.getEmail())) {
                revokeNaverToken(config, accessToken);
                throw new ServerBizException(ErrorType.JWT_SNS_CONSENT_REQUIRED,
                        "로그인을 위해서 필수 제공 항목에 대해 동의가 필요합니다. 이름과 이메일주소 모두 선택해 주세요.");
            }
        }

        // 6. MH_EXT_MEMBER에서 SNS 사용자 조회/생성
        snsUser.setMediaSeq(1); // app.media-seq 기본값
        String userId = processUser(snsUser);

        // 7. JWT 발급
        String mediaId = getDefaultMediaId();
        String jwtAccessToken = tokenProvider.createTokenWithString(mediaId, userId);
        String refreshToken = refreshTokenService.createAndSaveRefreshToken(
                mediaId, userId, snsUser.getName() != null ? snsUser.getName() : userId);

        // 8. 로그인 정보 업데이트 (최근 로그인 시간, 첫 로그인 기록)
        userLoginRepository.updateLoginInfo(userId, null);
        userLoginRepository.updateFirstLogin(userId);

        log.info("SNS login success: provider={}, userId={}", provider, userId);

        UserLoginResVO loginRes = UserLoginResVO.builder()
                .accessToken(jwtAccessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(accessTokenValidityInSeconds)
                .userId(userId)
                .userNm(snsUser.getName())
                .mediaId(mediaId)
                .build();

        return new SnsCallbackResult(loginRes, stateInfo.redirectUri);
    }

    // ──────────────────────────────────────────────
    // 내부 메서드
    // ──────────────────────────────────────────────

    /**
     * Authorization code를 Access Token으로 교환
     */
    private Map<String, Object> exchangeCodeForToken(
            String provider, SnsProviderProperties.ProviderConfig config,
            String code, String callbackUrl) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", config.getClientId());
        params.add("client_secret", config.getClientSecret());
        params.add("code", code);
        params.add("redirect_uri", callbackUrl);

        // Naver는 state도 필요
        if ("naver".equals(provider)) {
            params.add("state", ""); // 이미 검증 완료
        }

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    config.getTokenUri(), request, Map.class);
            log.debug("Token exchange response from {}: {}", provider, response.getBody());
            return response.getBody() != null ? response.getBody() : Collections.emptyMap();
        } catch (Exception e) {
            log.error("Token exchange failed for provider={}: {}", provider, e.getMessage());
            throw new ServerBizException(ErrorType.JWT_SNS_AUTH_FAILED,
                    "SNS 토큰 교환에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * Access Token으로 프로바이더 사용자 정보 조회
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> getUserInfo(
            String provider, SnsProviderProperties.ProviderConfig config, String accessToken) {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        // Kakao는 Content-Type 필요
        if ("kakao".equals(provider)) {
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        }

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    config.getUserInfoUri(), HttpMethod.GET, request, Map.class);
            log.debug("User info response from {}: {}", provider, response.getBody());
            return response.getBody() != null ? response.getBody() : Collections.emptyMap();
        } catch (Exception e) {
            log.error("User info fetch failed for provider={}: {}", provider, e.getMessage());
            throw new ServerBizException(ErrorType.JWT_SNS_AUTH_FAILED,
                    "SNS 사용자 정보 조회에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 프로바이더별 응답을 SnsUserDTO로 파싱
     */
    @SuppressWarnings("unchecked")
    private SnsUserDTO parseUserInfo(String provider, Map<String, Object> userInfo, String accessToken) {
        SnsUserDTO.SnsUserDTOBuilder builder = SnsUserDTO.builder()
                .snsLoginType(provider)
                .snsAccessToken(accessToken);

        switch (provider) {
            case "kakao": {
                builder.memberId(String.valueOf(userInfo.get("id")));
                Map<String, Object> account = (Map<String, Object>) userInfo.get("kakao_account");
                if (account != null) {
                    builder.email((String) account.get("email"));
                    Map<String, Object> profile = (Map<String, Object>) account.get("profile");
                    if (profile != null) {
                        builder.socialProfileImagePath((String) profile.get("profile_image_url"));
                        builder.socialThumbnailImagePath((String) profile.get("thumbnail_image_url"));
                    }
                }
                break;
            }
            case "naver": {
                Map<String, Object> resp = (Map<String, Object>) userInfo.get("response");
                if (resp != null) {
                    builder.memberId((String) resp.get("id"));
                    builder.email((String) resp.get("email"));
                    builder.name((String) resp.get("name"));
                    builder.socialProfileImagePath((String) resp.get("profile_image"));
                    builder.socialGender((String) resp.get("gender"));
                    builder.socialApproxAge((String) resp.get("age"));
                    builder.socialApproxBirthday((String) resp.get("birthday"));
                    builder.socialApproxBirthyear((String) resp.get("birthyear"));
                }
                break;
            }
            case "google": {
                builder.memberId((String) userInfo.get("sub"));
                builder.email((String) userInfo.get("email"));
                builder.name((String) userInfo.get("name"));
                break;
            }
            case "facebook": {
                builder.memberId((String) userInfo.get("id"));
                builder.email((String) userInfo.get("email"));
                builder.name((String) userInfo.get("name"));
                break;
            }
            default:
                throw new ServerBizException(ErrorType.JWT_SNS_PROVIDER_NOT_SUPPORTED);
        }

        return builder.build();
    }

    /**
     * MH_EXT_MEMBER에서 SNS 사용자 조회/생성
     * @return 사용자의 memberId (로그인 ID)
     */
    private String processUser(SnsUserDTO snsUser) {
        List<SnsUserDTO> existingUsers = snsUserRepository.findSnsUser(
                snsUser.getMemberId(), snsUser.getSnsLoginType());

        int blockedCount = 0;
        int activeCount = 0;

        if (existingUsers != null && !existingUsers.isEmpty()) {
            for (SnsUserDTO existing : existingUsers) {
                if ("D".equals(existing.getAccountStatus())) {
                    if (StringUtils.hasText(existing.getSnsAccessToken())) {
                        blockedCount++;
                    }
                } else if ("A".equals(existing.getAccountStatus())) {
                    if (StringUtils.hasText(existing.getSnsAccessToken())) {
                        activeCount++;
                    }
                }
            }
        }

        if (blockedCount > 0) {
            throw new ServerBizException(ErrorType.JWT_SNS_USER_BLOCKED);
        }

        if (activeCount == 0) {
            // 새 SNS 사용자 생성
            snsUserRepository.insertSnsUser(snsUser);
            log.info("New SNS user created: memberId={}, provider={}",
                    snsUser.getMemberId(), snsUser.getSnsLoginType());
        }

        return snsUser.getMemberId();
    }

    /**
     * 네이버 토큰 삭제 (필수 동의 미충족 시)
     */
    private void revokeNaverToken(SnsProviderProperties.ProviderConfig config, String accessToken) {
        try {
            String url = "https://nid.naver.com/oauth2.0/token"
                    + "?grant_type=delete"
                    + "&client_id=" + config.getClientId()
                    + "&client_secret=" + config.getClientSecret()
                    + "&access_token=" + accessToken
                    + "&service_provider=NAVER";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            HttpEntity<Void> request = new HttpEntity<>(headers);

            restTemplate.postForEntity(url, request, String.class);
            log.info("Naver token revoked successfully");
        } catch (Exception e) {
            log.warn("Failed to revoke Naver token: {}", e.getMessage());
        }
    }

    // ──────────────────────────────────────────────
    // State 관리 (Redis)
    // ──────────────────────────────────────────────

    private void saveState(String state, String provider, String redirectUri) {
        StateInfo stateInfo = new StateInfo(provider, redirectUri);
        try {
            String key = keyPrefix + ":" + SNS_STATE_PREFIX + state;
            String value = objectMapper.writeValueAsString(stateInfo);
            redisTemplate.opsForValue().set(key, value, STATE_EXPIRY_SECONDS, TimeUnit.SECONDS);
        } catch (JsonProcessingException e) {
            log.error("Failed to save SNS state", e);
            throw new RuntimeException("Failed to save SNS state", e);
        }
    }

    private StateInfo loadAndDeleteState(String state) {
        String key = keyPrefix + ":" + SNS_STATE_PREFIX + state;
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return null;
        }
        redisTemplate.delete(key);
        try {
            return objectMapper.readValue(value.toString(), StateInfo.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse SNS state", e);
            return null;
        }
    }

    // ──────────────────────────────────────────────
    // 유틸리티
    // ──────────────────────────────────────────────

    private SnsProviderProperties.ProviderConfig getProviderConfig(String provider) {
        if (!snsProviderProperties.isSupported(provider)) {
            throw new ServerBizException(ErrorType.JWT_SNS_PROVIDER_NOT_SUPPORTED);
        }
        return snsProviderProperties.getProvider(provider);
    }

    public String getDefaultRedirectUri() {
        return StringUtils.hasText(defaultRedirectUri) ? defaultRedirectUri : "http://localhost:" + serverPort;
    }

    String buildCallbackUrl(String provider) {
        // 실제 운영시에는 서버의 외부 URL을 사용해야 함
        return "http://localhost:" + serverPort
                + "/v1/auth-svr/auth/sns/callback/" + provider;
    }

    private String getDefaultMediaId() {
        if (!mediaProperties.getConfigs().isEmpty()) {
            return mediaProperties.getConfigs().get(0).getMediaId();
        }
        return "DEFAULT";
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    // ──────────────────────────────────────────────
    // 내부 DTO
    // ──────────────────────────────────────────────

    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class StateInfo {
        private String provider;
        private String redirectUri;
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    public static class SnsCallbackResult {
        private UserLoginResVO loginRes;
        private String redirectUri;
    }
}
