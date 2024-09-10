package com.yjmedia.yvisbig.bizcom.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LogJsonMapper {
    private ObjectMapper objectMapper = new ObjectMapper();

    public String writeValueAsString(Object object){
        try {
            return objectMapper.writeValueAsString( object);
        }catch (Exception e){
            log.error("LOG WRITER ERROR");
        }
        return "";
    }

}
