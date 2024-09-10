package com.yjmedia.yvisbig.bizcom.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class ActionLoggerInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        log.info("Request URL: {}", request.getRequestURL());
        return true; // true를 반환하면 다음 단계로 진행, false를 반환하면 요청을 중단
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {
        // 뷰 렌더링이 완료된 후에 실행되는 로직
        log.info("Request END: {}", request.getRequestURL());
    }
}
