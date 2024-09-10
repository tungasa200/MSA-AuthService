package com.yjmedia.yvisbig.bizcom.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yjmedia.yvisbig.bizcom.exception.ErrorType;
import com.yjmedia.yvisbig.bizcom.exception.ServerErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        //response.sendError(HttpServletResponse.SC_OK);
        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(ServerErrorResponse.builder()
                .bizErrCode(ErrorType.JWT_NOT_AUTH.getBizErrorCode())
                .message(ErrorType.JWT_NOT_AUTH.getMessage())
                .detailMessage(ErrorType.JWT_NOT_AUTH.getDetailMessage())
                .path(request.getRequestURI())
                .messageKey(ErrorType.JWT_NOT_AUTH.getMessageKey()).build()));

    }
}