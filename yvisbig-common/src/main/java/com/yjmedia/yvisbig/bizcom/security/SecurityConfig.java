package com.yjmedia.yvisbig.bizcom.security;

import com.yjmedia.yvisbig.bizcom.constants.GlobalConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // Spring Security 활성화
@EnableMethodSecurity // @PreAuthorize 어노테이션 메소드 단위로 추가하기 위해 적용 (default : true)
public class SecurityConfig {

  private final TokenProvider tokenProvider;
  private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
  private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

  // TokenProvider,JwtAuthenticationEntryPoint,JwtAccessDeniedHandler 의존성 주입
  public SecurityConfig(
      TokenProvider tokenProvider,
      JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
      JwtAccessDeniedHandler jwtAccessDeniedHandler
  ) {
    this.tokenProvider = tokenProvider;
    this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
  }

  // 비밀번호 암호화
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /*

      @Bean
      public WebSecurityCustomizer webSecurityCustomizer() {
          return (web) -> web.ignoring()
                  // Spring Security should completely ignore URLs starting with /resources/
                  .requestMatchers("/resources/**");
      }

  */
  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
    // ACL(Access Control List, 접근 제어 목록)의 예외 URL 설정
    return (web)
        -> web
        .ignoring()
        .requestMatchers(); // 정적 리소스들
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    if (GlobalConstants.YVISBIG_MSA_SERVER_ACTIVE_PROFILE.contains("local")) {
      http
          // 토큰을 사용하기 때문에 csrf 설정 disable
          .csrf().disable()
          .cors().disable()

          // 예외 처리 시 직접 만들었던 클래스 추가
          .exceptionHandling()
          .authenticationEntryPoint(jwtAuthenticationEntryPoint)
          .accessDeniedHandler(jwtAccessDeniedHandler)

          // 세션 사용하지 않기 때문에 세션 설정 STATELESS
          .and()
          .sessionManagement()
          .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
          .and()
          .authorizeHttpRequests()
          .anyRequest().permitAll()
          // JwtFilter를 addFilterBefore로 등록했던 jwtSecurityConfig 클래스 적용
          .and()
          .apply(new JwtSecurityConfig(tokenProvider));
    } else {
      http
          // 토큰을 사용하기 때문에 csrf 설정 disable
          .csrf().disable()

          // 예외 처리 시 직접 만들었던 클래스 추가
          .exceptionHandling()
          .authenticationEntryPoint(jwtAuthenticationEntryPoint)
          .accessDeniedHandler(jwtAccessDeniedHandler)

          // 세션 사용하지 않기 때문에 세션 설정 STATELESS
          .and()
          .sessionManagement()
          .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

          // 토큰이 없는 상태에서 요청이 들어오는 API들은 permitAll
          .and()
          .authorizeHttpRequests()
          .mvcMatchers("/v1/auth-svr/adminLogin").permitAll()
          .mvcMatchers("/v1/auth-svr/dmLogin").permitAll()
          .mvcMatchers("/v1/auth-svr/cmLogin").permitAll()
          .mvcMatchers("/v1/auth-svr/login").permitAll()
          .mvcMatchers("/v1/auth-svr/health").permitAll()
          .mvcMatchers("/v1/auth-svr/refreshToken").permitAll()
          .mvcMatchers("/v1/biztotal/webview/portone/getAuthInfo").permitAll()
          .mvcMatchers("/v1/biztotal/webview/mobilians/getMobilSelfAuthInfo").permitAll()
          .mvcMatchers("/v1/biztotal/cm/listTerm").permitAll()
          .mvcMatchers("/v1/biztotal/dm/listTerm").permitAll()
          .mvcMatchers("/v1/biztotal/cm/getTerm").permitAll()
          .mvcMatchers("/v1/biztotal/dm/getTerm").permitAll()
          .mvcMatchers("/v1/biztotal/cm/mbr/newMember").permitAll()
          .mvcMatchers("/v1/biztotal/dm/mbr/newMember").permitAll()
          .mvcMatchers("/v1/biztotal/health").permitAll()
              .mvcMatchers("/v1/biztotal/cb/regVirtualPaymentRestNotice").permitAll()
          .mvcMatchers("/v1/biztotal/adm/fcm/**").authenticated()
          //.antMatchers("/authcheck").authenticated()
          .mvcMatchers("/swagger-ui").permitAll()
          .mvcMatchers("/swagger-ui/**").permitAll()
          .mvcMatchers("/api-docs/**").permitAll()
          .antMatchers("/authcheck").hasRole("USER")
          .mvcMatchers("/v1/biztotal/cm/**").hasRole("CMMB")
          .mvcMatchers("/v1/biztotal/dm/**").hasRole("DMMB")
          .mvcMatchers("/v1/biztotal/adm/**").hasRole("ADMN")


          //.anyRequest().permitAll()
          .anyRequest().authenticated()
          // JwtFilter를 addFilterBefore로 등록했던 jwtSecurityConfig 클래스 적용
          .and()
          .apply(new JwtSecurityConfig(tokenProvider));
    }

    return http.build();
  }


}