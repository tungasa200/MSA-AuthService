package com.yjmedia.yvisbig.bizcom.config;

import lombok.Getter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;

@Getter
public class HttpHeaderDefaultType {
    private HttpHeaders header;
    public static String mediaTypeString;

    public HttpHeaderDefaultType(){
        header = new HttpHeaders();
        header.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        mediaTypeString  = header.getContentType().toString();
    }

    public HttpHeaderDefaultType(MediaType mediaType){
        header = new HttpHeaders();
        header.setContentType(mediaType);
        mediaTypeString  = header.getContentType().toString();
    }
}
