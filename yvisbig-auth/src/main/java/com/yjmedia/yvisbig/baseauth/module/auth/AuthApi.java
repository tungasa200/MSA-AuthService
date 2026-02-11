package com.yjmedia.yvisbig.baseauth.module.auth;

import com.yjmedia.yvisbig.baseauth.voProtocol.UserLoginReqVO;
import com.yjmedia.yvisbig.baseauth.voProtocol.UserLoginResVO;
import com.yjmedia.yvisbig.baseauth.voProtocol.UserRefreshReqVO;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 인증 API 컨트롤러
 * 로그인, 로그아웃, 토큰 갱신
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth-svr")
@Tag(name = "Auth", description = "인증 API (로그인/로그아웃/토큰갱신)")
public class AuthApi {

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
    @PostMapping("/auth/login")
    @AcessScope(scope = AccessScopeType.PUBLIC)
    public ResponseEntity<UserLoginResVO> login(@Valid @RequestBody UserLoginReqVO reqVO,
                                                 HttpServletRequest request) {
        log.info("Login request: mediaId={}, userId={}", reqVO.getMediaId(), reqVO.getUserId());

        // 클라이언트 IP 설정 (프록시 환경 고려)
        if (reqVO.getLastLoginIp() == null || reqVO.getLastLoginIp().isEmpty()) {
            reqVO.setLastLoginIp(getClientIp(request));
        }

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
    @PostMapping("/auth/refresh")
    @AcessScope(scope = AccessScopeType.PUBLIC)
    public ResponseEntity<UserLoginResVO> refreshToken(
            @Valid @RequestBody UserRefreshReqVO reqVO) {

        String mediaId = reqVO.getMediaId();
        String userId = reqVO.getUserId();

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
    @PostMapping("/auth/logout")
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

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // X-Forwarded-For에 여러 IP가 있는 경우 첫 번째 사용
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
