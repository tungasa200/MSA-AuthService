package com.yjmedia.yvisbig.baseauth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * SNS OAuth2 프로바이더 설정
 * application.yml의 sns.providers 설정을 읽어옴
 */
@Data
@Component
@ConfigurationProperties(prefix = "sns")
public class SnsProviderProperties {

    private Map<String, ProviderConfig> providers = new HashMap<>();
    private String defaultRedirectUri;

    @Data
    public static class ProviderConfig {
        private String clientId;
        private String clientSecret;
        private String authorizationUri;
        private String tokenUri;
        private String userInfoUri;
        private String scope;
    }

    /**
     * 프로바이더명으로 설정 조회
     * @param provider 프로바이더명 (kakao, naver, google, facebook)
     * @return 프로바이더 설정 (없으면 null)
     */
    public ProviderConfig getProvider(String provider) {
        return providers.get(provider.toLowerCase());
    }

    /**
     * 지원 프로바이더인지 확인
     */
    public boolean isSupported(String provider) {
        return providers.containsKey(provider.toLowerCase());
    }
}
