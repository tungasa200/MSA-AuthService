package com.yjmedia.yvisbig.baseauth.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component // 1
@Slf4j
@Aspect
public class ActionLogAop {

    @Before("execution(* com.yjmedia.yvisbig.baseauth.module..*Api.*(..))")
    public void doLoggingBefore(JoinPoint joinPoint) {
        // 메서드 정보 받아오기
        //Method method = getMethod(joinPoint);
        //log.info("======= method name = {} =======", method.getName());

        // 파라미터 받아오기
        Object[] args = joinPoint.getArgs();
        if (args.length <= 0) log.info("no parameter");
        for (Object arg : args) {
            log.info("parameter type = {}, value = {}", arg.getClass().getSimpleName(), arg);
        }
    }

    private Method getMethod(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getMethod();
    }


}
