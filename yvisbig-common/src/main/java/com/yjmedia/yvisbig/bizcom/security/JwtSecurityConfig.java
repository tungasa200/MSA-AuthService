package com.yjmedia.yvisbig.bizcom.security;


import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


// TokenProvider, JwtFilter를 SecurityConfig에 적용할 떄 사용
// SecurityConfigurerAdapter를 extends

public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity>{
    private final TokenProvider tokenProvider;

    // TokenProvider를 주입
    public JwtSecurityConfig(TokenProvider tokenProvider){
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void configure(HttpSecurity http) {

        System.out.println("configure");

        JwtFilterCheckException customJwtFilter = new JwtFilterCheckException(tokenProvider);
        http.addFilterBefore(customJwtFilter, UsernamePasswordAuthenticationFilter.class);
    }
}