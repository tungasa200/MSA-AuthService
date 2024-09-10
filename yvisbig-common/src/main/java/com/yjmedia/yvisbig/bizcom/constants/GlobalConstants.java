package com.yjmedia.yvisbig.bizcom.constants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

@Configuration
public class GlobalConstants {
    public static final String DEFAULT_CHARSET="UTF-8";
    public static final String JWT_TOKEN_HEADER="YJSAuthorization";


    public static String YVISBIG_MSA_SERVER_ID;

    public static String YVISBIG_MSA_SERVER_VERSION;

    public static String YVISBIG_MSA_SERVER_ACTIVE_PROFILE;


    @Value("${logging.server.id}")
    public void setYVISBIG_MSA_SERVER_ID(String serverId) {
        this.YVISBIG_MSA_SERVER_ID = serverId;
    }

    @Value("${logging.server.version}")
    public void setYVISBIG_MSA_SERVER_VERSION(String serverVersion) {
        this.YVISBIG_MSA_SERVER_VERSION = serverVersion;
    }

    @Value("${spring.profiles.active}")
    public void setYVISBIG_MSA_SERVER_ACTIVE_PROFILE(String activeProfile) {
        this.YVISBIG_MSA_SERVER_ACTIVE_PROFILE = activeProfile;
    }



}
