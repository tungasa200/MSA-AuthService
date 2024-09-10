package com.yjmedia.yvisbig.bizcom.enums;

public enum RedisKeyType {
    REDISKEY_REFRESHTOKEN("REDISKEY_REFRESHTOKEN"),
    REDISKEY_USERINFO("REDISKEY_USERINFO");

    private String keyName;

    private RedisKeyType(String keyName){
        this.keyName = keyName;
    }

    public String getKeyName(){
        return keyName;
    }
}
