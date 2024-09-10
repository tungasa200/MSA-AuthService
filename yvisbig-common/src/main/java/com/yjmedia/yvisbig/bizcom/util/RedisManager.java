package com.yjmedia.yvisbig.bizcom.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j

public class RedisManager {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.redis.sessiontime}")
    private int redisSessionTime;

    @Autowired
    ObjectMapper objectMapper;

    public boolean setValue(String key, Object value){
        ValueOperations<String, Object> vop = redisTemplate.opsForValue();
        //set
        vop.set(key, value);

        return redisTemplate.expire(key, redisSessionTime, TimeUnit.SECONDS);
    }

    public void setValuePersist(String key, Object value){
        ValueOperations<String, Object> vop = redisTemplate.opsForValue();
        //set
        vop.set(key, value);
        redisTemplate.persist(key);
        return;
    }

    public void setValueWithEachTime(String key, Object value, int sessionTimeSecond){
        ValueOperations<String, Object> vop = redisTemplate.opsForValue();
        //set
        vop.set(key, value);
        redisTemplate.expire(key, sessionTimeSecond, TimeUnit.SECONDS);
        return;
    }

    public void appendValue(String key, String value){
        ValueOperations<String, Object> vop = redisTemplate.opsForValue();
        //set
        vop.append(key, value);
        redisTemplate.expire(key, redisSessionTime, TimeUnit.SECONDS);
        return;
    }

    public void remove(String key){
        redisTemplate.delete(key);
        return;
    }

    public boolean isExist(String key){
        ValueOperations<String, Object> vop = redisTemplate.opsForValue();
        return vop.get(key) != null ? true: false;
    }

    public Object getValue(String key){
        ValueOperations<String, Object> vop = redisTemplate.opsForValue();
        redisTemplate.expire(key, redisSessionTime, TimeUnit.SECONDS);
        return vop.get(key);
    }

    public void publishMessage(String channel, Object message) throws Exception {
        redisTemplate.convertAndSend(channel, objectMapper.writeValueAsString(message));
    }

}
