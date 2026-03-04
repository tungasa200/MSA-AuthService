package com.yjmedia.yvisbig.baseauth.module.auth;

import com.yjmedia.yvisbig.baseauth.voProtocol.UserLoginResVO;
import com.yjmedia.yvisbig.bizcom.annotation.AcessScope;
import com.yjmedia.yvisbig.bizcom.enums.AccessScopeType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * SNS OAuth2 인증 API 컨트롤러
 * OAuth2 인가 리다이렉트 및 콜백 처리
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth-svr")
@Tag(name = "SNS Auth", description = "SNS OAuth2 인증 API (Kakao/Naver/Google/Facebook)")
public class SnsAuthApi {

    private static final String REFRESH_TOKEN_COOKIE = "msa_refresh_token";
    private static final String REFRESH_TOKEN_COOKIE_PATH = "/";

    private final SnsLoginService snsLoginService;

    @Value("${jwt.refresh-token-validity-in-seconds:2592000}")
    private long refreshTokenValidityInSeconds;

    @Value("${app.cookie.domain:}")
    private String cookieDomain;

    @Value("${app.cookie.secure:true}")
    private boolean cookieSecure;

    /**
     * SNS OAuth2 인가 요청
     * 프로바이더의 인가 페이지로 리다이렉트
     *
     * @param provider    프로바이더명 (kakao, naver, google, facebook)
     * @param redirectUri 인증 완료 후 리다이렉트할 서비스 URL
     */
    @Operation(summary = "SNS 로그인 시작", description = "SNS 프로바이더 인가 페이지로 리다이렉트")
    @GetMapping("/auth/sns/authorize/{provider}")
    @AcessScope(scope = AccessScopeType.PUBLIC)
    public void authorize(
            @Parameter(description = "SNS 프로바이더 (kakao, naver, google, facebook)")
            @PathVariable String provider,
            @Parameter(description = "인증 완료 후 리다이렉트할 URL")
            @RequestParam(required = false) String redirectUri,
            HttpServletResponse response) throws IOException {

        log.info("SNS authorize request: provider={}, redirectUri={}", provider, redirectUri);

        if (!StringUtils.hasText(redirectUri)) {
            redirectUri = snsLoginService.buildCallbackUrl(provider);
        }

        String authorizationUrl = snsLoginService.buildAuthorizationUrl(provider, redirectUri);
        response.sendRedirect(authorizationUrl);
    }

    /**
     * SNS OAuth2 콜백 처리
     * 프로바이더로부터 인가 코드를 받아 토큰 교환 및 사용자 처리 후 서비스로 리다이렉트
     *
     * @param provider 프로바이더명
     * @param code     인가 코드
     * @param state    CSRF 방지 state 파라미터
     * @param error    에러 코드 (인가 거부 등)
     */
    @Operation(summary = "SNS 로그인 콜백", description = "SNS 프로바이더 콜백 처리 후 JWT 발급 및 리다이렉트")
    @GetMapping("/auth/sns/callback/{provider}")
    @AcessScope(scope = AccessScopeType.PUBLIC)
    public void callback(
            @PathVariable String provider,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String error,
            @RequestParam(required = false, name = "error_description") String errorDescription,
            HttpServletResponse response) throws IOException {

        log.info("SNS callback received: provider={}, hasCode={}, error={}",
                provider, code != null, error);

        // 사용자가 인가를 거부한 경우
        if (StringUtils.hasText(error)) {
            log.warn("SNS authorization denied: provider={}, error={}, description={}",
                    provider, error, errorDescription);
            String defaultRedirect = snsLoginService.getDefaultRedirectUri();
            response.sendRedirect(defaultRedirect + "?error=" + encode(error));
            return;
        }

        try {
            SnsLoginService.SnsCallbackResult result =
                    snsLoginService.handleCallback(provider, code, state);

            UserLoginResVO loginRes = result.getLoginRes();
            String redirectUri = result.getRedirectUri();

            // Refresh Token을 httpOnly 쿠키로 설정 (URL 파라미터에서 제거)
            addRefreshTokenCookie(response, loginRes.getRefreshToken());

            // Access Token 등 나머지 정보만 URL 파라미터로 전달
            StringBuilder redirectUrl = new StringBuilder(redirectUri);
            redirectUrl.append(redirectUri.contains("?") ? "&" : "?");
            redirectUrl.append("accessToken=").append(encode(loginRes.getAccessToken()));
            redirectUrl.append("&tokenType=").append(encode(loginRes.getTokenType()));
            redirectUrl.append("&expiresIn=").append(loginRes.getExpiresIn());
            redirectUrl.append("&userId=").append(encode(loginRes.getUserId()));
            if (loginRes.getUserNm() != null) {
                redirectUrl.append("&userNm=").append(encode(loginRes.getUserNm()));
            }

            response.sendRedirect(redirectUrl.toString());

        } catch (Exception e) {
            log.error("SNS callback processing failed: provider={}", provider, e);
            String defaultRedirect = snsLoginService.getDefaultRedirectUri();
            response.sendRedirect(defaultRedirect + "?error=" + encode("sns_auth_failed")
                    + "&message=" + encode(e.getMessage()));
        }
    }

    private void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from(REFRESH_TOKEN_COOKIE, refreshToken)
                .httpOnly(true)
                .secure(cookieSecure)
                .path(REFRESH_TOKEN_COOKIE_PATH)
                .maxAge(refreshTokenValidityInSeconds)
                .sameSite("Lax");
        if (cookieDomain != null && !cookieDomain.isEmpty()) {
            builder.domain(cookieDomain);
        }
        response.addHeader(HttpHeaders.SET_COOKIE, builder.build().toString());
    }

    private String encode(String value) {
        if (value == null) return "";
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
