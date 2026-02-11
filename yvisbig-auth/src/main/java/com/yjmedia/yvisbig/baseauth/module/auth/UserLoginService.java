package com.yjmedia.yvisbig.baseauth.module.auth;

import com.yjmedia.yvisbig.baseauth.config.MediaProperties;
import com.yjmedia.yvisbig.baseauth.voProtocol.UserLoginReqVO;
import com.yjmedia.yvisbig.baseauth.voProtocol.UserLoginResVO;
import com.yjmedia.yvisbig.baseauth.voProtocol.UserRefreshReqVO;
import com.yjmedia.yvisbig.baseauth.voProtocol.UserRegisterReqVO;
import com.yjmedia.yvisbig.baseauth.voProtocol.UserRegisterResVO;
import com.yjmedia.yvisbig.bizcom.dto.ServiceUserDTO;
import com.yjmedia.yvisbig.bizcom.encoder.KisaSha256PasswordEncoder;
import com.yjmedia.yvisbig.bizcom.exception.ErrorType;
import com.yjmedia.yvisbig.bizcom.exception.ServerBizException;
import com.yjmedia.yvisbig.bizcom.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 로그인 서비스
 * ID/PW 기반 사용자 인증 및 JWT 토큰 발급
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserLoginService {

    private final UserLoginRepository userLoginRepository;
    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final KisaSha256PasswordEncoder kisaSha256PasswordEncoder;
    private final MediaProperties mediaProperties;

    @Value("${jwt.token-validity-in-seconds:3600}")
    private long accessTokenValidityInSeconds;

    /**
     * 사용자 로그인 처리
     * 1. 사용자 조회 (MH_USERS)
     * 2. 비밀번호 검증
     * 3. Access Token 생성
     * 4. Refresh Token 생성 및 Redis 저장
     *
     * @param reqVO 로그인 요청 정보
     * @return 로그인 응답 (토큰 정보 포함)
     */
    @Transactional
    public UserLoginResVO login(UserLoginReqVO reqVO) {
        String mediaId = reqVO.getMediaId();
        String userId = reqVO.getUserId();
        String password = reqVO.getPassword();

        // mediaId가 없으면 기본 mediaId 사용
        if (mediaId == null || mediaId.isEmpty()) {
            mediaId = getDefaultMediaId();
        }

        log.info("User login attempt: mediaId={}, userId={}", mediaId, userId);

        // 1. 사용자 조회 (MH_USERS 테이블)
        ServiceUserDTO user = userLoginRepository.findByUserLoginOrEmail(userId);
        if (user == null) {
            log.warn("User not found: userId={}", userId);
            throw new ServerBizException(ErrorType.JWT_NOT_EXIST_USER);
        }

        // 2. 비밀번호 검증 (KISA_SHA256)
        if (!kisaSha256PasswordEncoder.matches(password, user.getUserPassword())) {
            log.warn("Invalid password: userId={}", userId);
            throw new ServerBizException(ErrorType.JWT_INVALID_PASSWORD);
        }

        // 3. Access Token 생성
        String accessToken = tokenProvider.createTokenWithString(mediaId, user.getUserLogin());

        // 4. Refresh Token 생성 및 Redis 저장
        String refreshToken = refreshTokenService.createAndSaveRefreshToken(
                mediaId, user.getUserLogin(), user.getUserName());

        // 5. 로그인 정보 업데이트 (최근 로그인 시간, 첫 로그인 기록)
        userLoginRepository.updateLoginInfo(user.getUserLogin(), reqVO.getLastLoginIp());
        userLoginRepository.updateFirstLogin(user.getUserLogin());

        log.info("User login success: mediaId={}, userId={}", mediaId, user.getUserLogin());

        return UserLoginResVO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(accessTokenValidityInSeconds)
                .userId(user.getUserLogin())
                .userNm(user.getUserName())
                .mediaId(mediaId)
                .build();
    }

    /**
     * 토큰 갱신 처리
     * 1. Refresh Token 검증
     * 2. 새 Access Token 생성
     * 3. (선택) Refresh Token Rotation
     *
     * @param reqVO 갱신 요청 정보
     * @param mediaId 언론사 ID
     * @param userId 사용자 ID
     * @return 새 토큰 정보
     */
    @Transactional
    public UserLoginResVO refreshToken(UserRefreshReqVO reqVO, String mediaId, String userId) {
        String refreshToken = reqVO.getRefreshToken();
        
        // mediaId가 없으면 기본 mediaId 사용
        if (mediaId == null || mediaId.isEmpty()) {
            mediaId = getDefaultMediaId();
        }

        log.info("Token refresh attempt: mediaId={}, userId={}", mediaId, userId);

        // 1. Refresh Token 검증
        RefreshTokenService.RefreshTokenInfo tokenInfo =
                refreshTokenService.findByToken(refreshToken, mediaId, userId);

        if (tokenInfo == null) {
            log.warn("Invalid refresh token: mediaId={}, userId={}", mediaId, userId);
            throw new ServerBizException(ErrorType.JWT_INVALID_REFRESH_TOKEN);
        }

        // 2. Refresh Token 유효성 검증
        if (!refreshTokenService.validateRefreshToken(refreshToken, mediaId, userId)) {
            log.warn("Refresh token expired: mediaId={}, userId={}", mediaId, userId);
            throw new ServerBizException(ErrorType.JWT_TOKEN_REFRESH_TIMEOUT);
        }

        // 3. 새 Access Token 생성
        String newAccessToken = tokenProvider.createTokenWithString(mediaId, userId);

        // 4. Refresh Token Rotation (선택적 - 보안 강화)
        String newRefreshToken = refreshTokenService.rotateRefreshToken(
                mediaId, userId, tokenInfo.getUserNm());

        log.info("Token refresh success: mediaId={}, userId={}", mediaId, userId);

        return UserLoginResVO.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(accessTokenValidityInSeconds)
                .userId(userId)
                .userNm(tokenInfo.getUserNm())
                .mediaId(mediaId)
                .build();
    }

    /**
     * 로그아웃 처리
     * Redis에서 Refresh Token 삭제
     *
     * @param mediaId 언론사 ID
     * @param userId 사용자 ID
     */
    public void logout(String mediaId, String userId) {
        // mediaId가 없으면 기본 mediaId 사용
        if (mediaId == null || mediaId.isEmpty()) {
            mediaId = getDefaultMediaId();
        }
        log.info("User logout: mediaId={}, userId={}", mediaId, userId);
        refreshTokenService.deleteRefreshToken(mediaId, userId);
    }

    /**
     * 회원가입 처리
     * 1. 사용자 ID 중복 체크
     * 2. 이메일 중복 체크 (선택)
     * 3. 비밀번호 암호화
     * 4. 사용자 등록
     *
     * @param reqVO 회원가입 요청 정보
     * @return 회원가입 응답
     */
    @Transactional
    public UserRegisterResVO register(UserRegisterReqVO reqVO) {
        String mediaId = reqVO.getMediaId();
        String mediaKey = reqVO.getMediaKey();
        String mediaSecret = reqVO.getMediaSecret();
        String userLogin = reqVO.getUserLogin();
        String userEmail = reqVO.getUserEmail();

        // mediaId가 없으면 기본 mediaId 사용
        if (mediaId == null || mediaId.isEmpty()) {
            mediaId = getDefaultMediaId();
        }

        log.info("User registration attempt: mediaId={}, userLogin={}", mediaId, userLogin);

        // 0. 언론사 키 검증 (mediaKey, mediaSecret이 있을 때만)
        if (mediaKey != null && !mediaKey.isEmpty() && mediaSecret != null && !mediaSecret.isEmpty()) {
            validateMediaCredentials(mediaId, mediaKey, mediaSecret);
        }
        validateMediaCredentials(mediaId, mediaKey, mediaSecret);

        // 1. 사용자 ID 중복 체크
        if (userLoginRepository.checkUserLoginExists(userLogin) > 0) {
            log.warn("User login already exists: userLogin={}", userLogin);
            throw new ServerBizException(ErrorType.JWT_USER_ALREADY_EXISTS, "이미 사용 중인 아이디입니다.");
        }

        // 2. 이메일 중복 체크 (이메일이 있는 경우에만)
        if (userEmail != null && !userEmail.isEmpty()) {
            if (userLoginRepository.checkUserEmailExists(userEmail) > 0) {
                log.warn("User email already exists: userEmail={}", userEmail);
                throw new ServerBizException(ErrorType.JWT_USER_ALREADY_EXISTS, "이미 사용 중인 이메일입니다.");
            }
        }

        // 3. 비밀번호 암호화 (KISA SHA256)
        String encodedPassword = kisaSha256PasswordEncoder.encode(reqVO.getPassword());

        // 4. 사용자 정보 생성
        ServiceUserDTO newUser = new ServiceUserDTO();
        newUser.setUserLogin(userLogin);
        newUser.setUserName(reqVO.getUserName());
        newUser.setUserEmail(userEmail);
        newUser.setUserPassword(encodedPassword);
        newUser.setUserOu(reqVO.getUserOu());
        newUser.setUserLanguage(reqVO.getUserLanguage() != null ? reqVO.getUserLanguage() : "ko");

        // 5. 사용자 등록
        int result = userLoginRepository.insertUser(newUser);
        if (result <= 0) {
            log.error("Failed to insert user: userLogin={}", userLogin);
            throw new ServerBizException(ErrorType.SERVER_INTERNAL_EXCEPTION, "회원가입에 실패했습니다.");
        }

        log.info("User registration success: mediaId={}, userLogin={}, userId={}", mediaId, userLogin, newUser.getUserId());

        return UserRegisterResVO.builder()
                .success(true)
                .message("회원가입이 완료되었습니다.")
                .userId(newUser.getUserId())
                .userLogin(userLogin)
                .userName(reqVO.getUserName())
                .mediaId(mediaId)
                .build();
    }

    /**
     * 사용자 ID 중복 체크
     * @param userLogin 사용자 로그인 ID
     * @return true: 사용 가능, false: 이미 존재
     */
    public boolean checkUserLoginAvailable(String userLogin) {
        return userLoginRepository.checkUserLoginExists(userLogin) == 0;
    }

    /**
     * 이메일 중복 체크
     * @param userEmail 이메일
     * @return true: 사용 가능, false: 이미 존재
     */
    public boolean checkUserEmailAvailable(String userEmail) {
        if (userEmail == null || userEmail.isEmpty()) {
            return true;
        }
        return userLoginRepository.checkUserEmailExists(userEmail) == 0;
    }

    /**
     * 기본 mediaId 조회
     * MediaProperties의 첫 번째 설정에서 mediaId를 가져옴
     */
    private String getDefaultMediaId() {
        if (!mediaProperties.getConfigs().isEmpty()) {
            return mediaProperties.getConfigs().get(0).getMediaId();
        }
        return "DEFAULT";
    }

    /**
     * 언론사 키 검증
     * mediaId, mediaKey, mediaSecret이 application.yml 설정과 일치하는지 확인
     *
     * @param mediaId 언론사 ID
     * @param mediaKey 언론사 Key
     * @param mediaSecret 언론사 Secret
     */
    private void validateMediaCredentials(String mediaId, String mediaKey, String mediaSecret) {
        MediaProperties.MediaConfig config = mediaProperties.findByMediaId(mediaId)
                .orElseThrow(() -> {
                    log.warn("Invalid media ID: mediaId={}", mediaId);
                    return new ServerBizException(ErrorType.JWT_INVALID_MEDIA_CREDENTIALS, "유효하지 않은 언론사 ID입니다.");
                });

        if (!config.getMediaKey().equals(mediaKey)) {
            log.warn("Invalid media key: mediaId={}, mediaKey={}", mediaId, mediaKey);
            throw new ServerBizException(ErrorType.JWT_INVALID_MEDIA_CREDENTIALS, "유효하지 않은 언론사 Key입니다.");
        }

        if (!config.getMediaSecret().equals(mediaSecret)) {
            log.warn("Invalid media secret: mediaId={}", mediaId);
            throw new ServerBizException(ErrorType.JWT_INVALID_MEDIA_CREDENTIALS, "유효하지 않은 언론사 Secret입니다.");
        }

        log.debug("Media credentials validated: mediaId={}", mediaId);
    }
}