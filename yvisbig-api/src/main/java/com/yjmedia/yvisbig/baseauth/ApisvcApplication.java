package com.yjmedia.yvisbig.baseauth;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({
        "com.yjmedia.yvisbig.baseauth",
        "com.yjmedia.yvisbig.bizcom",
})
//@EnableScheduling // 추가
public class ApisvcApplication {
    public static void main(String[] args)
    {
        SpringApplication.run(ApisvcApplication.class, args);
    }

}