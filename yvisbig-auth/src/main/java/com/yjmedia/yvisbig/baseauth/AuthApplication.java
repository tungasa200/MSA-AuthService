package com.yjmedia.yvisbig.baseauth;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan({
        "com.yjmedia.yvisbig.baseauth",
        "com.yjmedia.yvisbig.bizcom",
})
@MapperScan("com.yjmedia.yvisbig.baseauth")
//@EnableScheduling // 추가
public class AuthApplication {
    public static void main(String[] args)
    {
        SpringApplication.run(AuthApplication.class, args);
    }

}