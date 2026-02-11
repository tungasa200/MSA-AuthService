package com.yjmedia.yvisbig.baseauth.config;

import com.yjmedia.yvisbig.bizcom.config.HttpHeaderDefaultType;
import com.yjmedia.yvisbig.bizcom.interceptor.ActionLoggerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.Charset;

@Configuration

public class Webconfig implements WebMvcConfigurer{

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Authorization");
   }

    @Bean
    public HttpHeaderDefaultType httpHeaderDefaultType(){
        HttpHeaderDefaultType httpHeaderDefaultType = new HttpHeaderDefaultType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return httpHeaderDefaultType;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // LoggingInterceptor를 등록
        registry.addInterceptor(new ActionLoggerInterceptor());
    }

}
