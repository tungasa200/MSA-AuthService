package com.yjmedia.yvisbig.baseauth.module.auth;

import com.yjmedia.yvisbig.baseauth.voProtocol.*;
import com.yjmedia.yvisbig.bizcom.config.HttpHeaderDefaultType;
import com.yjmedia.yvisbig.bizcom.util.CryptoUtil;
import com.yjmedia.yvisbig.bizcom.util.RedisManager;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.yjmedia.yvisbig.bizcom.annotation.AcessScope;
import com.yjmedia.yvisbig.bizcom.enums.AccessScopeType;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth-svr")
public class AuthCoreApi {

    private final RedisManager redisManager;
    private final HttpHeaderDefaultType httpHeaderDefaultType;
    private final AuthCoreService authCoreService;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.system-secret}")
    private String systemSecret;

    @Operation(summary = "Sample:Simple API 테스트", description = "기본 체크")
    @GetMapping("/simplecheck")
    @AcessScope(scope = AccessScopeType.PUBLIC)
    public String getSimpleCheck() {

        return "hi - check ok";
    }

    @Operation(summary = "토큰얻기", description = "HMAC 이용")
    @PostMapping("/getToken")
    @AcessScope(scope = AccessScopeType.PUBLIC)
    public ResponseEntity<SvrGetTokenResVO> getToken(@RequestBody SvrGetTokenReqVO svrGetTokenReqVO) {

        SvrGetTokenResVO svrGetTokenResVO = authCoreService.getToken(svrGetTokenReqVO);
        return new ResponseEntity<>(svrGetTokenResVO, httpHeaderDefaultType.getHeader() , HttpStatus.OK);
    }

    @Operation(summary = "토큰 다시 얻기", description = "기본 체크")
    @PostMapping("/refreshToken")
    @AcessScope(scope = AccessScopeType.PUBLIC)
    public ResponseEntity<SvrRefreshTokenResVO> refreshToken(@RequestBody SvrRefreshTokenReqVO  svrRefreshTokenReqVO) {

        SvrRefreshTokenResVO svrRefreshTokenResVO = authCoreService.refreshToken(svrRefreshTokenReqVO);
        return new ResponseEntity<>(svrRefreshTokenResVO, httpHeaderDefaultType.getHeader() , HttpStatus.OK);
    }


    @Operation(summary = "비밀키암호화", description = "언론사별 비밀키를 암호화하여 사용함")
    @GetMapping("/getEncodeMediaSecret")
    @AcessScope(scope = AccessScopeType.PUBLIC)
    public String getEncodeMediaSecret(String strSecretKey) {

        String resultKey =  "";
        try{
            resultKey = CryptoUtil.encryptAES256(strSecretKey, systemSecret);
        }catch (Exception ex){

        }
        return resultKey;
    }


}
