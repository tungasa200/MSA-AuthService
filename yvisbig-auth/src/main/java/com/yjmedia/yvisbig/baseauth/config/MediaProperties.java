package com.yjmedia.yvisbig.baseauth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 언론사 정보 설정
 * application.yml의 media.configs 설정을 읽어옴
 */
@Data
@Component
@ConfigurationProperties(prefix = "media")
public class MediaProperties {

    private List<MediaConfig> configs = new ArrayList<>();

    @Data
    public static class MediaConfig {
        private String mediaId;
        private String mediaKey;
        private String mediaSecret;
    }

    /**
     * mediaId로 언론사 설정 조회
     */
    public Optional<MediaConfig> findByMediaId(String mediaId) {
        return configs.stream()
                .filter(c -> c.getMediaId().equals(mediaId))
                .findFirst();
    }

    /**
     * mediaKey로 언론사 설정 조회
     */
    public Optional<MediaConfig> findByMediaKey(String mediaKey) {
        return configs.stream()
                .filter(c -> c.getMediaKey().equals(mediaKey))
                .findFirst();
    }
}