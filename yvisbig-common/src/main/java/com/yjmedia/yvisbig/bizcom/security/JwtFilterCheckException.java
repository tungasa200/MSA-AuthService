package com.yjmedia.yvisbig.bizcom.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yjmedia.yvisbig.bizcom.config.HttpHeaderDefaultType;
import com.yjmedia.yvisbig.bizcom.constants.GlobalConstants;
import com.yjmedia.yvisbig.bizcom.exception.ErrorType;
import com.yjmedia.yvisbig.bizcom.exception.ServerErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// JWT를 위한 커스텀 필터
@Slf4j
public class JwtFilterCheckException extends OncePerRequestFilter {

    private ObjectMapper objectMapper = new ObjectMapper();



    //private static final Logger logger = LoggerFactory.getLogger(JwtFilterCheckException.class);

    public static final String AUTHORIZATION_HEADER = GlobalConstants.JWT_TOKEN_HEADER;

    private final TokenProvider tokenProvider;

    public JwtFilterCheckException(TokenProvider tokenProvider){
        this.tokenProvider = tokenProvider;
    }

    // 실제 필터링 로직 작성
    // doFilter : 토큰의 인증 정보를 SecurityContext에 저장
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {


        // resolveToken을 통해 토큰을 받아옴
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String jwt = resolveToken(httpServletRequest);
        String requestURI = httpServletRequest.getRequestURI();

        // 토큰 유효성 검증 후 정상이면 SecurityContext에 저장
        try{
            /*
                    .mvcMatchers("/v1/auth-svr/adminLogin").permitAll()
                    .mvcMatchers("/v1/auth-svr/dmLogin").permitAll()
                    .mvcMatchers("/v1/auth-svr/cmLogin").permitAll()
                    .mvcMatchers("/v1/auth-svr/login").permitAll()
                    .mvcMatchers("/v1/biztotal/cm/listTerm").permitAll()
                    .mvcMatchers("/v1/biztotal/dm/listTerm").permitAll()
                    .mvcMatchers("/v1/biztotal/cm/getTerm").permitAll()
                    .mvcMatchers("/v1/biztotal/dm/getTerm").permitAll()
                    .mvcMatchers("/v1/biztotal/cm/mbr/newMember").permitAll()
                    .mvcMatchers("/v1/biztotal/dm/mbr/newMember").permitAll()
                                    .mvcMatchers("/swagger-ui").permitAll()
                .mvcMatchers("/swagger-ui/**").permitAll()

              */
            if(!requestURI.contains("Login") &&
                    !requestURI.contains("auth-svr/refreshToken") &&
                    !requestURI.contains("auth/login") &&
                    !requestURI.contains("auth/refresh") &&
                    !requestURI.contains("auth/logout") &&
                    !requestURI.contains("user/register") &&
                    !requestURI.contains("user/check-") &&
                    !requestURI.contains("/v1/biztotal/cm/listTerm") &&
                    !requestURI.contains("/v1/biztotal/cm/getTerm") &&
                    !requestURI.contains("/v1/biztotal/dm/listTerm") &&
                    !requestURI.contains("/v1/biztotal/dm/getTerm") &&
                    !requestURI.contains("/v1/biztotal/webview/mobilians/getMobilSelfAuthInfo") &&
                    !requestURI.contains("/v1/biztotal/cb/regVirtualPaymentRestNotice") &&
                    !requestURI.contains("health") &&
                    !requestURI.contains("swagger-ui") &&
                        !requestURI.contains("mbr/newMember") ){
                if(StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)){
                    Authentication authentication = tokenProvider.getAuthentication(jwt);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}",authentication.getName(),requestURI);
                }
                else log.error("유효한 JWT 토큰이 없습니다, uri: {}",requestURI);
            }

            filterChain.doFilter(httpServletRequest,response);
        }catch (ExpiredJwtException e){
            //토큰의 유효기간 만료
            setErrorResponse(request, response, ErrorType.JWT_TOKEN_TIME_OUT);
        }//기타 오류는 핸들러에서 처리
        /*
        catch (JwtException | IllegalArgumentException e){
            //유효하지 않은 토큰
            setErrorResponse(response, ErrorCode.INVALID_TOKEN);
        }
         */

    }

    // Request Header에서 토큰 정보를 꺼내오기
    private String resolveToken(HttpServletRequest request){
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")){

            System.out.println("token : " + bearerToken);
            return bearerToken.substring(7);
        }
        return null;
    }
    private void setErrorResponse( HttpServletRequest request, HttpServletResponse response,ErrorType errorType){
        try{
            if(ErrorType.JWT_TOKEN_TIME_OUT == errorType){
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
            }
            else {
                response.setStatus(HttpStatus.OK.value());
            }

            response.setContentType(HttpHeaderDefaultType.mediaTypeString);
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(ServerErrorResponse.builder()
                    .bizErrCode(errorType.getBizErrorCode())
                    .message(errorType.getMessage())
                    .detailMessage(errorType.getDetailMessage())
                    .path(request.getRequestURI())
                    .messageKey(errorType.getMessageKey()).build()));
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}