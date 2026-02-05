package com.yjmedia.yvisbig.baseauth.module.auth;

import com.yjmedia.yvisbig.baseauth.voProtocol.UserLoginReqVO;
import com.yjmedia.yvisbig.baseauth.voProtocol.UserLoginResVO;
import com.yjmedia.yvisbig.baseauth.voProtocol.UserRefreshReqVO;
import com.yjmedia.yvisbig.baseauth.voProtocol.UserRegisterReqVO;
import com.yjmedia.yvisbig.baseauth.voProtocol.UserRegisterResVO;
import com.yjmedia.yvisbig.bizcom.dto.ServiceUserDTO;
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
        String userLogin = reqVO.getUserLogin();
        String userEmail = reqVO.getUserEmail();

        log.info("User registration attempt: mediaId={}, userLogin={}", mediaId, userLogin);

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

        // 6. 인증서버 사용자 정보 동기화 (svr_user)
        syncUserInfo(mediaId, userLogin, reqVO.getUserName());

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