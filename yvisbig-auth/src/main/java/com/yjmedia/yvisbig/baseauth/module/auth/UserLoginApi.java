package com.yjmedia.yvisbig.baseauth.module.auth;

import com.yjmedia.yvisbig.baseauth.voProtocol.UserLoginReqVO;
import com.yjmedia.yvisbig.baseauth.voProtocol.UserLoginResVO;
import com.yjmedia.yvisbig.baseauth.voProtocol.UserRefreshReqVO;
import com.yjmedia.yvisbig.baseauth.voProtocol.UserRegisterReqVO;
import com.yjmedia.yvisbig.baseauth.voProtocol.UserRegisterResVO;
import com.yjmedia.yvisbig.bizcom.annotation.AcessScope;
import com.yjmedia.yvisbig.bizcom.config.HttpHeaderDefaultType;
import com.yjmedia.yvisbig.bizcom.enums.AccessScopeType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 사용자 로그인 API 컨트롤러
 * ID/PW 기반 사용자 인증 및 JWT 토큰 관리
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth-svr")
@Tag(name = "User Login", description = "사용자 로그인/로그아웃/토큰갱신 API")
public class UserLoginApi {

    private final UserLoginService userLoginService;
    private final HttpHeaderDefaultType httpHeaderDefaultType;

    /**
     * 사용자 로그인
     * ID/PW를 검증하고 JWT Access Token과 Refresh Token을 발급
     *
     * @param reqVO 로그인 요청 (mediaId, userId, password)
     * @return 토큰 정보 (accessToken, refreshToken, expiresIn 등)
     */
    @Operation(summary = "사용자 로그인", description = "ID/PW 기반 로그인 후 JWT 토큰 발급")
    @PostMapping("/user/login")
    @AcessScope(scope = AccessScopeType.PUBLIC)
    public ResponseEntity<UserLoginResVO> login(@Valid @RequestBody UserLoginReqVO reqVO) {
        log.info("Login request: mediaId={}, userId={}", reqVO.getMediaId(), reqVO.getUserId());

        UserLoginResVO resVO = userLoginService.login(reqVO);

        return new ResponseEntity<>(resVO, httpHeaderDefaultType.getHeader(), HttpStatus.OK);
    }

    /**
     * 토큰 갱신
     * Refresh Token으로 새 Access Token 발급
     *
     * @param reqVO 갱신 요청 (refreshToken)
     * @param mediaId 언론사 ID (헤더 또는 파라미터)
     * @param userId 사용자 ID (헤더 또는 파라미터)
     * @return 새 토큰 정보
     */
    @Operation(summary = "토큰 갱신", description = "Refresh Token으로 새 Access Token 발급")
    @PostMapping("/user/refresh")
    @AcessScope(scope = AccessScopeType.PUBLIC)
    public ResponseEntity<UserLoginResVO> refreshToken(
            @Valid @RequestBody UserRefreshReqVO reqVO,
            @RequestHeader(value = "X-Media-Id", required = false) String mediaIdHeader,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader,
            @RequestParam(value = "mediaId", required = false) String mediaIdParam,
            @RequestParam(value = "userId", required = false) String userIdParam) {

        // 헤더 우선, 없으면 파라미터 사용
        String mediaId = mediaIdHeader != null ? mediaIdHeader : mediaIdParam;
        String userId = userIdHeader != null ? userIdHeader : userIdParam;

        log.info("Token refresh request: mediaId={}, userId={}", mediaId, userId);

        UserLoginResVO resVO = userLoginService.refreshToken(reqVO, mediaId, userId);

        return new ResponseEntity<>(resVO, httpHeaderDefaultType.getHeader(), HttpStatus.OK);
    }

    /**
     * 로그아웃
     * Redis에서 Refresh Token 삭제
     *
     * @param mediaId 언론사 ID
     * @param userId 사용자 ID
     * @return 성공 응답
     */
    @Operation(summary = "로그아웃", description = "Refresh Token 삭제 (Redis)")
    @PostMapping("/user/logout")
    @AcessScope(scope = AccessScopeType.PUBLIC)
    public ResponseEntity<Map<String, Object>> logout(
            @RequestHeader(value = "X-Media-Id", required = false) String mediaIdHeader,
            @RequestHeader(value = "X-User-Id", required = false) String userIdHeader,
            @RequestParam(value = "mediaId", required = false) String mediaIdParam,
            @RequestParam(value = "userId", required = false) String userIdParam) {

        // 헤더 우선, 없으면 파라미터 사용
        String mediaId = mediaIdHeader != null ? mediaIdHeader : mediaIdParam;
        String userId = userIdHeader != null ? userIdHeader : userIdParam;

        log.info("Logout request: mediaId={}, userId={}", mediaId, userId);

        userLoginService.logout(mediaId, userId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "로그아웃 되었습니다.");

        return new ResponseEntity<>(response, httpHeaderDefaultType.getHeader(), HttpStatus.OK);
    }

    /**
     * 회원가입
     * 새 사용자 등록
     *
     * @param reqVO 회원가입 요청 정보
     * @return 회원가입 결과
     */
    @Operation(summary = "회원가입", description = "새 사용자 등록")
    @PostMapping("/user/register")
    @AcessScope(scope = AccessScopeType.PUBLIC)
    public ResponseEntity<UserRegisterResVO> register(@Valid @RequestBody UserRegisterReqVO reqVO) {
        log.info("Register request: mediaId={}, userLogin={}", reqVO.getMediaId(), reqVO.getUserLogin());

        UserRegisterResVO resVO = userLoginService.register(reqVO);

        return new ResponseEntity<>(resVO, httpHeaderDefaultType.getHeader(), HttpStatus.CREATED);
    }

    /**
     * 사용자 ID 중복 체크
     *
     * @param userLogin 사용자 로그인 ID
     * @return 사용 가능 여부
     */
    @Operation(summary = "ID 중복 체크", description = "사용자 ID 사용 가능 여부 확인")
    @GetMapping("/user/check-id")
    @AcessScope(scope = AccessScopeType.PUBLIC)
    public ResponseEntity<Map<String, Object>> checkUserId(@RequestParam("userLogin") String userLogin) {
        log.info("Check user ID: userLogin={}", userLogin);

        boolean available = userLoginService.checkUserLoginAvailable(userLogin);

        Map<String, Object> response = new HashMap<>();
        response.put("available", available);
        response.put("userLogin", userLogin);
        response.put("message", available ? "사용 가능한 아이디입니다." : "이미 사용 중인 아이디입니다.");

        return new ResponseEntity<>(response, httpHeaderDefaultType.getHeader(), HttpStatus.OK);
    }

    /**
     * 이메일 중복 체크
     *
     * @param userEmail 이메일
     * @return 사용 가능 여부
     */
    @Operation(summary = "이메일 중복 체크", description = "이메일 사용 가능 여부 확인")
    @GetMapping("/user/check-email")
    @AcessScope(scope = AccessScopeType.PUBLIC)
    public ResponseEntity<Map<String, Object>> checkUserEmail(@RequestParam("userEmail") String userEmail) {
        log.info("Check user email: userEmail={}", userEmail);

        boolean available = userLoginService.checkUserEmailAvailable(userEmail);

        Map<String, Object> response = new HashMap<>();
        response.put("available", available);
        response.put("userEmail", userEmail);
        response.put("message", available ? "사용 가능한 이메일입니다." : "이미 사용 중인 이메일입니다.");

        return new ResponseEntity<>(response, httpHeaderDefaultType.getHeader(), HttpStatus.OK);
    }
}