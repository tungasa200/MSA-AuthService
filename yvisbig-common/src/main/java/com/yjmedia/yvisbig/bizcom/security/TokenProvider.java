package com.yjmedia.yvisbig.bizcom.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

// 토큰의 생성, 토큰의 유효성 검증 등을 담당

@Component
public class TokenProvider implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(TokenProvider.class);

    private static final String AUTHORITIES_KEY = "auth";

    private final String secret;
    private final long tokenValidityInMilliseconds;

    private final long refreshTokenValidityInMilliseconds;

    private Key key;

    // 의존성 주입
    public TokenProvider(
            @Value("${jwt.system-secret}") String secret,
            @Value("${jwt.token-validity-in-seconds}") long tokenValidityInSeconds,
            @Value("${jwt.refresh-token-validity-in-seconds}") long refreshTokenValidityInSeconds
    ){
        this.secret = secret;
        this.tokenValidityInMilliseconds = tokenValidityInSeconds* 1000;
        this.refreshTokenValidityInMilliseconds = refreshTokenValidityInSeconds * 1000 ;
    }

    // Bean이 생성이 되고 주입을 받은 후에 secret값을 Base64로 Decode 해서 key 변수에 할당
    @Override
    public void afterPropertiesSet() {

        System.out.println("afterPropertiesSet");

        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }


    public String createTokenWithString(String mediaId,
                                        String userId){

        System.out.println("createTokenWithString");

        // 토큰 만료 시간 설정
        long now = (new Date()).getTime();

        // 사용자 정의 클레임 생성
        Map<String, Object> claims = new HashMap<>();
        claims.put("mediaId", mediaId);
        claims.put("userId", userId);


        String accessToken =  Jwts.builder()
                .setSubject("auth")
                .setClaims(claims)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(new Date(now + this.tokenValidityInMilliseconds))
                .compact();

        return accessToken;
    }


    // 토큰에 담겨있는 정보를 이용해 Authentication 객체 리턴
    public Authentication getAuthentication(String token){

        System.out.println("getAuthentication");

        // 토큰을 이용하여 claim 생성
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        // claim을 이용하여 authorities 생성
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // claim과 authorities 이용하여 User 객체 생성
        User principal = new User(claims.getSubject(), "", authorities);

        // 최종적으로 Authentication 객체 리턴
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    // 토큰의 유효성 검증 수행
    public boolean validateToken(String token){

        System.out.println("validateToken");

        // 토큰 파싱 후 발생하는 예외 캐치하여 문제 있으면 false, 정상이면 true 리턴
        try{
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        }
        catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) { logger.info("잘못된 JWT 토큰 서명"); }
        catch (ExpiredJwtException e) {
            logger.info("만료된 JWT 토큰 {}", token);
            throw e;
        }
        catch (UnsupportedJwtException e) { logger.info("지원되지 않는 JWT 토큰"); }
        catch (IllegalArgumentException e) { logger.info("잘못된 JWT 토큰"); }
        return false;
    }

    public boolean validateRefreshToken(String token){

        System.out.println("validateToken");

        // 토큰 파싱 후 발생하는 예외 캐치하여 문제 있으면 false, 정상이면 true 리턴
        try{
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        }
        catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) { logger.info("잘못된 JWT 토큰 서명"); }
        catch (ExpiredJwtException e) { logger.info("만료된 JWT 토큰");}
        catch (UnsupportedJwtException e) { logger.info("지원되지 않는 JWT 토큰"); }
        catch (IllegalArgumentException e) { logger.info("잘못된 JWT 토큰"); }
        return false;
    }

}