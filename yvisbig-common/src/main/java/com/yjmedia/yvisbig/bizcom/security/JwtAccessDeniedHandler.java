package com.yjmedia.yvisbig.bizcom.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yjmedia.yvisbig.bizcom.exception.ErrorType;
import com.yjmedia.yvisbig.bizcom.exception.ServerErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        //response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(ServerErrorResponse.builder()
                .bizErrCode(ErrorType.JWT_NOT_PERMISSION.getBizErrorCode())
                .message(ErrorType.JWT_NOT_PERMISSION.getMessage())
                .detailMessage(ErrorType.JWT_NOT_PERMISSION.getDetailMessage())
                .path(request.getRequestURI())
                .messageKey(ErrorType.JWT_NOT_PERMISSION.getMessageKey()).build()));

    }

}