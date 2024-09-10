package com.yjmedia.yvisbig.bizcom.constants;

import com.yjmedia.yvisbig.bizcom.enums.RedisKeyType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RedisKeyConfig {

    public static String keyPrefix;

    @Value("${spring.redis.keyprefix:LOCAL}")
    public void setKeyPrefix(String value) {
        this.keyPrefix = value;
    }

    public String getUserRedisKey(RedisKeyType redisKey, long userSeq){
        StringBuilder sb = new StringBuilder();
        sb.append(keyPrefix);
        sb.append("_");
        switch (redisKey){
            case REDISKEY_REFRESHTOKEN :
                sb.append(userSeq);
                sb.append("_");
                sb.append(redisKey.getKeyName());
                break;
            default:
                break;
        }
        return sb.toString();
    }

}
