package com.yjmedia.yvisbig.baseauth.module.healthcheck;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "헬스체크", description = "헬스체크 api")
@RestController
@RequestMapping("/v1/auth-svr")
public class HealthCheckApi {

  @Operation(summary = "헬스체크", description = "auth server 헬스체크 api")
  @GetMapping("/health")
  public String health() {
    return "OK";
  }
}
