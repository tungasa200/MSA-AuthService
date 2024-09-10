package com.yjmedia.yvisbig.baseauth.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;


import static java.util.stream.Collectors.toList;

@Configuration
@OpenAPIDefinition
public class SwaggerConfig {
/*
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any()) // 현재 RequestMapping으로 할당된 모든 URL 리스트를 추출
                .paths(PathSelectors.ant("/test/**")) // 그중 /api/** 인 URL들만 필터링
                .build();
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.OAS_30)
                .useDefaultResponseMessages(true) // Swagger 에서 제공해주는 기본 응답 코드를 표시할 것이면 true
                .apiInfo(apiInfo())
                .select()
               // .apis(RequestHandlerSelectors.basePackage("com.msa.internalauthservice")) // Controller가 들어있는 패키지. 이 경로의 하위에 있는 api만 표시됨.
               // .paths(PathSelectors.any()) // 위 패키지 안의 api 중 지정된 path만 보여줌. (any()로 설정 시 모든 api가 보여짐)
                .paths(PathSelectors.ant("/test/**"))
                .build();
    }

    public ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("SpringBoot Rest API Documentation")
                .description("3rd UMC Server: BAEMIN Clone coding ")
                .version("0.1")
                .build();
    }
*/

}
