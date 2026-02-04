package com.yjmedia.yvisbig.baseauth.module.auth;

import com.yjmedia.yvisbig.baseauth.voProtocol.UserLoginReqVO;
import com.yjmedia.yvisbig.baseauth.voProtocol.UserLoginResVO;
import com.yjmedia.yvisbig.baseauth.voProtocol.UserRefreshReqVO;
import com.yjmedia.yvisbig.bizcom.dto.ServiceUserDTO;
import com.yjmedia.yvisbig.bizcom.dto.SvrMediaKeyDTO;
import com.yjmedia.yvisbig.bizcom.dto.SvrUserDTO;
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
    private final AuthRepository authRepository;
    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final KisaSha256PasswordEncoder kisaSha256PasswordEncoder;

    @Value("${jwt.token-validity-in-seconds:3600}")
    private long accessTokenValidityInSeconds;

    /**
     * 사용자 로그인 처리
     * 1. 언론사 확인
     * 2. 사용자 조회
     * 3. 비밀번호 검증
     * 4. Access Token 생성
     * 5. Refresh Token 생성 및 Redis 저장
     * 6. 사용자 정보 동기화 (svr_user)
     *
     * @param reqVO 로그인 요청 정보
     * @return 로그인 응답 (토큰 정보 포함)
     */
    @Transactional
    public UserLoginResVO login(UserLoginReqVO reqVO) {
        String mediaId = reqVO.getMediaId();
        String userId = reqVO.getUserId();
        String password = reqVO.getPassword();

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

        // 5. 인증서버 사용자 정보 동기화 (svr_user)
        syncUserInfo(mediaId, user.getUserLogin(), user.getUserName());

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
        log.info("User logout: mediaId={}, userId={}", mediaId, userId);
        refreshTokenService.deleteRefreshToken(mediaId, userId);
    }

    /**
     * 인증서버 사용자 정보 동기화 (svr_user 테이블)
     */
    private void syncUserInfo(String mediaId, String userId, String userNm) {
        try {
            SvrUserDTO existUser = authRepository.selectSvrUserWithId(mediaId, userId);

            if (existUser == null) {
                SvrUserDTO newUser = new SvrUserDTO();
                newUser.setMediaId(mediaId);
                newUser.setUserId(userId);
                newUser.setUserNm(userNm);
                authRepository.insertSvrUser(newUser);
                log.debug("New user synced to svr_user: mediaId={}, userId={}", mediaId, userId);
            } else {
                existUser.setUserNm(userNm);
                authRepository.updateSvrUser(existUser);
                log.debug("User info updated in svr_user: mediaId={}, userId={}", mediaId, userId);
            }
        } catch (Exception e) {
            log.warn("Failed to sync user info: mediaId={}, userId={}, error={}", mediaId, userId, e.getMessage());
            // 사용자 동기화 실패는 로그인 실패로 처리하지 않음
        }
    }
}