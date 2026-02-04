package com.yjmedia.yvisbig.baseauth.module.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yjmedia.yvisbig.bizcom.util.RedisManager;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Refresh Token 관리 서비스
 * Redis에 Refresh Token을 저장하고 관리
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${jwt.refresh-token-validity-in-seconds:2592000}")
    private long refreshTokenValidityInSeconds;

    @Value("${spring.redis.keyprefix:LOCAL}")
    private String keyPrefix;

    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";

    /**
     * Refresh Token 정보 DTO
     */
    @Data
    @Builder
    public static class RefreshTokenInfo {
        private String token;
        private String userId;
        private String mediaId;
        private String userNm;
        private String issuedAt;
        private String expiresAt;
    }

    /**
     * Refresh Token 생성 및 Redis 저장
     * @param mediaId 언론사 ID
     * @param userId 사용자 ID
     * @param userNm 사용자명
     * @return 생성된 Refresh Token
     */
    public String createAndSaveRefreshToken(String mediaId, String userId, String userNm) {
        String refreshToken = UUID.randomUUID().toString();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusSeconds(refreshTokenValidityInSeconds);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        RefreshTokenInfo tokenInfo = RefreshTokenInfo.builder()
                .token(refreshToken)
                .userId(userId)
                .mediaId(mediaId)
                .userNm(userNm)
                .issuedAt(now.format(formatter))
                .expiresAt(expiresAt.format(formatter))
                .build();

        String key = buildKey(mediaId, userId);

        try {
            String value = objectMapper.writeValueAsString(tokenInfo);
            redisTemplate.opsForValue().set(key, value, refreshTokenValidityInSeconds, TimeUnit.SECONDS);
            log.info("Refresh token saved: key={}", key);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize refresh token info", e);
            throw new RuntimeException("Failed to save refresh token", e);
        }

        return refreshToken;
    }

    /**
     * Refresh Token으로 토큰 정보 조회
     * @param refreshToken Refresh Token 값
     * @param mediaId 언론사 ID
     * @param userId 사용자 ID
     * @return 토큰 정보 (없으면 null)
     */
    public RefreshTokenInfo findByToken(String refreshToken, String mediaId, String userId) {
        String key = buildKey(mediaId, userId);
        Object value = redisTemplate.opsForValue().get(key);

        if (value == null) {
            log.warn("Refresh token not found: key={}", key);
            return null;
        }

        try {
            RefreshTokenInfo tokenInfo = objectMapper.readValue(value.toString(), RefreshTokenInfo.class);

            // 토큰 값 일치 여부 확인
            if (!refreshToken.equals(tokenInfo.getToken())) {
                log.warn("Refresh token mismatch: expected={}, actual={}", refreshToken, tokenInfo.getToken());
                return null;
            }

            return tokenInfo;
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize refresh token info", e);
            return null;
        }
    }

    /**
     * 사용자별 Refresh Token 정보 조회
     * @param mediaId 언론사 ID
     * @param userId 사용자 ID
     * @return 토큰 정보 (없으면 null)
     */
    public RefreshTokenInfo findByUser(String mediaId, String userId) {
        String key = buildKey(mediaId, userId);
        Object value = redisTemplate.opsForValue().get(key);

        if (value == null) {
            return null;
        }

        try {
            return objectMapper.readValue(value.toString(), RefreshTokenInfo.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize refresh token info", e);
            return null;
        }
    }

    /**
     * Refresh Token 삭제 (로그아웃)
     * @param mediaId 언론사 ID
     * @param userId 사용자 ID
     */
    public void deleteRefreshToken(String mediaId, String userId) {
        String key = buildKey(mediaId, userId);
        Boolean deleted = redisTemplate.delete(key);
        log.info("Refresh token deleted: key={}, result={}", key, deleted);
    }

    /**
     * Refresh Token 갱신 (Rotation)
     * @param mediaId 언론사 ID
     * @param userId 사용자 ID
     * @param userNm 사용자명
     * @return 새로운 Refresh Token
     */
    public String rotateRefreshToken(String mediaId, String userId, String userNm) {
        deleteRefreshToken(mediaId, userId);
        return createAndSaveRefreshToken(mediaId, userId, userNm);
    }

    /**
     * Refresh Token 유효성 검증
     * @param refreshToken Refresh Token
     * @param mediaId 언론사 ID
     * @param userId 사용자 ID
     * @return 유효 여부
     */
    public boolean validateRefreshToken(String refreshToken, String mediaId, String userId) {
        RefreshTokenInfo tokenInfo = findByToken(refreshToken, mediaId, userId);
        if (tokenInfo == null) {
            return false;
        }

        // 만료 시간 확인
        LocalDateTime expiresAt = LocalDateTime.parse(tokenInfo.getExpiresAt(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        return LocalDateTime.now().isBefore(expiresAt);
    }

    /**
     * Redis 키 생성
     */
    private String buildKey(String mediaId, String userId) {
        return keyPrefix + ":" + REFRESH_TOKEN_PREFIX + mediaId + ":" + userId;
    }
}