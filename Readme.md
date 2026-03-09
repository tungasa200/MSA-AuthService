# YVISBIG Auth Server

마이크로서비스 아키텍처 기반의 JWT 인증 서버 프로젝트입니다.

---

## 목차

1. [프로젝트 개요](#1-프로젝트-개요)
2. [기술 스택](#2-기술-스택)
3. [프로젝트 구조](#3-프로젝트-구조)
4. [시스템 아키텍처](#4-시스템-아키텍처)
5. [모듈 설명](#5-모듈-설명)
6. [데이터베이스 설계](#6-데이터베이스-설계)
7. [엔티티 및 DTO 정의](#7-엔티티-및-dto-정의)
8. [API 명세](#8-api-명세)
9. [인증 흐름](#9-인증-흐름)
10. [보안 설정](#10-보안-설정)
11. [암호화 메커니즘](#11-암호화-메커니즘)
12. [에러 코드 정의](#12-에러-코드-정의)
13. [Enum 정의](#13-enum-정의)
14. [환경 설정](#14-환경-설정)
15. [실행 방법](#15-실행-방법)
16. [테스트 정보](#16-테스트-정보)

---

## 1. 프로젝트 개요

### 1.1 소개

**YVISBIG Auth Server**는 언론사 및 외부 시스템과 연동하기 위한 JWT 기반 인증 서버입니다. HMAC-SHA256을 이용한 요청 검증과 AES-256 암호화를 통해 보안을 강화하였습니다.

### 1.2 주요 기능

| 기능 | 설명 |
|------|------|
| **JWT 토큰 발급** | HMAC 검증 후 JWT Access Token 발급 |
| **토큰 갱신** | Refresh Token Rotation을 통한 토큰 재발급 |
| **SNS OAuth2 로그인** | 카카오/네이버/구글/페이스북 소셜 로그인 지원 |
| **일반 로그인** | ID/PW 기반 로그인 (KISA SHA-256) |
| **언론사 키 관리** | 언론사별 비밀키 암호화 저장 및 관리 |
| **사용자 관리** | 언론사 사용자 정보 등록 및 업데이트 |
| **역할 기반 접근 제어** | RBAC(Role-Based Access Control) 적용 |

### 1.3 빌드 정보

| 항목 | 버전 |
|------|------|
| Java | 1.8.0_231 |
| Spring Boot | 2.7.4 |
| Spring Cloud | 2021.0.5 |
| Gradle | Multi-module |

---

## 2. 기술 스택

### 2.1 Core Framework

| 기술 | 버전 | 용도 |
|------|------|------|
| Spring Boot | 2.7.4 | 애플리케이션 프레임워크 |
| Spring Security | 5.x | 인증/인가 |
| Spring Cloud Netflix Eureka | 2021.0.5 | 서비스 디스커버리 |
| Spring Data JPA | 2.7.x | ORM |
| Spring Data Redis | 1.4.0 | 캐시/세션 관리 |

### 2.2 Database & Persistence

| 기술 | 버전 | 용도 |
|------|------|------|
| MySQL | - | 메인 데이터베이스 |
| MyBatis | 2.1.0 | SQL 매핑 |
| HikariCP | - | Connection Pool |
| P6Spy | 1.8.1 | SQL 로깅 |

### 2.3 Security & Authentication

| 기술 | 버전 | 용도 |
|------|------|------|
| JJWT | 0.11.5 | JWT 토큰 생성/검증 |
| BCrypt | - | 비밀번호 암호화 |
| AES-256 | - | 데이터 암호화 |
| HMAC-SHA256 | - | 요청 검증 |

### 2.4 Infrastructure

| 기술 | 버전 | 용도 |
|------|------|------|
| Redis (Lettuce) | 5.3.0 | 세션/캐시 저장소 |
| Netflix Eureka | - | 서비스 레지스트리 |

### 2.5 Utilities

| 기술 | 버전 | 용도 |
|------|------|------|
| Lombok | - | 보일러플레이트 코드 제거 |
| Springdoc OpenAPI | 1.6.15 | API 문서화 (Swagger) |
| OkHttp | 4.3.1 | HTTP 클라이언트 |
| Apache POI | 4.1.2 | Excel 처리 |

---

## 3. 프로젝트 구조

### 3.1 모듈 구조

```
yvisbig-auth-j/
├── yvisbig-auth/                    # 인증 마이크로서비스 (포트: 8085)
│   ├── src/main/java/com/yjmedia/yvisbig/baseauth/
│   │   ├── AuthApplication.java     # Spring Boot 메인 클래스
│   │   ├── aop/                     # AOP (로깅)
│   │   ├── config/                  # 설정 클래스
│   │   ├── module/
│   │   │   ├── auth/                # 인증 모듈
│   │   │   ├── batch/               # 배치/스케줄링
│   │   │   └── healthcheck/         # 헬스체크
│   │   └── voProtocol/              # Request/Response VO
│   └── src/main/resources/
│       ├── application.yml          # 환경 설정
│       ├── mapper/auth/             # MyBatis 매퍼 XML
│       └── mybatis/                 # MyBatis 설정
│
├── yvisbig-common/                  # 공통 라이브러리 모듈
│   └── src/main/java/com/yjmedia/yvisbig/bizcom/
│       ├── annotation/              # 커스텀 어노테이션
│       ├── config/                  # 공통 설정
│       ├── constants/               # 상수 정의
│       ├── dto/                     # DTO 클래스
│       ├── enums/                   # Enum 정의 (28개)
│       ├── exception/               # 예외 처리
│       ├── interceptor/             # 인터셉터
│       ├── security/                # 보안 설정
│       ├── util/                    # 유틸리티 클래스
│       └── voHeader/                # 공통 VO 헤더
│
├── yvisbig-api/                     # API 마이크로서비스 (포트: 8087)
│   └── (yvisbig-auth와 유사한 구조)
│
├── build.gradle                     # 루트 빌드 설정
└── Readme.md                        # 프로젝트 문서
```

### 3.2 패키지 상세 구조

#### yvisbig-auth 모듈

```
com.yjmedia.yvisbig.baseauth
├── AuthApplication.java              # @SpringBootApplication
├── aop/
│   └── ActionLogAop.java             # API 로깅 AOP
├── config/
│   ├── DataSourceConfig.java         # DB 연결 설정
│   ├── MediaProperties.java          # 언론사 설정
│   ├── SnsProviderProperties.java    # SNS OAuth2 프로바이더 설정
│   ├── SwaggerConfig.java            # Swagger 설정
│   └── Webconfig.java                # 웹/CORS 설정
├── module/
│   ├── auth/
│   │   ├── AuthApi.java              # 일반 로그인/토큰 갱신 REST Controller
│   │   ├── AuthCoreService.java      # HMAC 방식 인증 비즈니스 로직
│   │   ├── AuthRepository.java       # MyBatis Repository
│   │   ├── CustomUserDetailsService.java
│   │   ├── RefreshTokenService.java  # Refresh Token Redis 관리
│   │   ├── SnsAuthApi.java           # SNS OAuth2 REST Controller
│   │   ├── SnsLoginService.java      # SNS OAuth2 비즈니스 로직
│   │   ├── SnsUserRepository.java    # SNS 사용자 DB 접근
│   │   ├── UserLoginRepository.java  # 일반 로그인 DB 접근
│   │   └── UserLoginService.java     # 일반 로그인 비즈니스 로직
│   ├── batch/
│   │   ├── SchAuthService.java       # 배치 서비스
│   │   ├── SchAuthRepository.java    # 배치 Repository
│   │   └── ScheduleAuth.java         # 스케줄러 (비활성화)
│   └── healthcheck/
│       └── HealthCheckApi.java       # 헬스체크 API
└── voProtocol/
    ├── SnsUserDTO.java               # SNS 사용자 정보 DTO
    ├── SvrGetTokenReqVO.java         # HMAC 토큰 요청 VO
    ├── SvrGetTokenResVO.java         # HMAC 토큰 응답 VO
    ├── SvrRefreshTokenReqVO.java     # HMAC 갱신 요청 VO
    ├── SvrRefreshTokenResVO.java     # HMAC 갱신 응답 VO
    ├── UserLoginReqVO.java           # 일반 로그인 요청 VO
    ├── UserLoginResVO.java           # 일반 로그인 응답 VO
    └── UserRefreshReqVO.java         # 토큰 갱신 요청 VO
```

#### yvisbig-common 모듈

```
com.yjmedia.yvisbig.bizcom
├── annotation/
│   └── AcessScope.java               # 접근 권한 어노테이션
├── config/
│   ├── HttpHeaderDefaultType.java    # HTTP 헤더 기본값
│   └── RedisRepositoryConfig.java    # Redis 설정
├── constants/
│   ├── GlobalConstants.java          # 전역 상수
│   └── RedisKeyConfig.java           # Redis 키 설정
├── dto/
│   ├── UserDTO.java                  # 회원 DTO
│   ├── SvrUserDTO.java               # 서버 사용자 DTO
│   └── SvrMediaKeyDTO.java           # 언론사 키 DTO
├── enums/                            # 28개 Enum 클래스
├── exception/
│   ├── ErrorType.java                # 에러 타입 정의
│   ├── ServerBizException.java       # 비즈니스 예외
│   ├── ServerErrorResponse.java      # 에러 응답 VO
│   └── GlobalExceptionHandler.java   # 전역 예외 처리
├── security/
│   ├── SecurityConfig.java           # Spring Security 설정
│   ├── TokenProvider.java            # JWT 토큰 제공자
│   ├── JwtSecurityConfig.java        # JWT 필터 설정
│   ├── JwtFilterCheckException.java  # JWT 검증 필터
│   ├── JwtAuthenticationEntryPoint.java
│   └── JwtAccessDeniedHandler.java
├── util/
│   ├── CryptoUtil.java               # 암호화 유틸
│   ├── RedisManager.java             # Redis 관리
│   ├── StringKdmpUtil.java           # 문자열 유틸
│   ├── ExHttpCall.java               # HTTP 호출 유틸
│   └── paging/
│       └── Pagination.java           # 페이징 유틸
└── voHeader/
    ├── RequestHeaderVO.java          # 요청 헤더 베이스
    ├── ResponseHeaderVO.java         # 응답 헤더 베이스
    └── SearchResultVO.java           # 검색 결과 VO
```

---

## 4. 시스템 아키텍처

### 4.1 마이크로서비스 아키텍처

```
┌─────────────────────────────────────────────────────────────────────┐
│                           Client (Mobile/Web)                        │
└─────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────┐
│                         API Gateway (Optional)                       │
└─────────────────────────────────────────────────────────────────────┘
                                    │
                    ┌───────────────┴───────────────┐
                    ▼                               ▼
        ┌───────────────────┐           ┌───────────────────┐
        │   Auth Service    │           │   API Service     │
        │    (Port: 8085)   │           │   (Port: 8087)    │
        │                   │           │                   │
        │  • JWT 발급       │           │  • 비즈니스 API   │
        │  • 토큰 검증      │           │  • 데이터 처리    │
        │  • 사용자 인증    │           │  • CRUD 작업      │
        └─────────┬─────────┘           └─────────┬─────────┘
                  │                               │
                  └───────────┬───────────────────┘
                              │
        ┌─────────────────────┼─────────────────────┐
        │                     │                     │
        ▼                     ▼                     ▼
┌───────────────┐   ┌─────────────────┐   ┌─────────────────┐
│    MySQL      │   │     Redis       │   │  Eureka Server  │
│   Database    │   │  Session/Cache  │   │   (Discovery)   │
└───────────────┘   └─────────────────┘   └─────────────────┘
```

### 4.2 인증 아키텍처

```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│   Client     │────▶│  Auth API    │────▶│   MySQL      │
│              │     │              │     │ (Media Key)  │
│  • mediaId   │     │  • HMAC검증  │     └──────────────┘
│  • userId    │     │  • JWT생성   │
│  • hmacHash  │     │  • 사용자저장│     ┌──────────────┐
│              │◀────│              │────▶│    Redis     │
│  JWT Token   │     └──────────────┘     │  (Session)   │
└──────────────┘                          └──────────────┘
```

### 4.3 보안 레이어

```
┌─────────────────────────────────────────────────────────────────┐
│                        Request Flow                              │
├─────────────────────────────────────────────────────────────────┤
│  1. HTTP Request                                                │
│       ▼                                                         │
│  2. JwtFilterCheckException (토큰 검증)                         │
│       ▼                                                         │
│  3. Spring Security Filter Chain                                │
│       ▼                                                         │
│  4. Authorization (Role Check: CMMB/DMMB/ADMN)                  │
│       ▼                                                         │
│  5. Controller → Service → Repository                           │
└─────────────────────────────────────────────────────────────────┘
```

---

## 5. 모듈 설명

### 5.1 yvisbig-auth (인증 서비스)

**역할**: JWT 토큰 발급 및 인증 처리

| 컴포넌트 | 클래스 | 설명 |
|----------|--------|------|
| Controller | `AuthCoreApi` | REST API 엔드포인트 |
| Service | `AuthCoreService` | 인증 비즈니스 로직 |
| Repository | `AuthRepository` | MyBatis 데이터 접근 |
| Batch | `ScheduleAuth` | 스케줄링 작업 (비활성화) |

**빌드 설정** (`build.gradle`):
```gradle
plugins {
    id 'org.springframework.boot' version '2.7.4'
    id 'io.spring.dependency-management' version '1.0.14.RELEASE'
    id 'java'
}

dependencies {
    implementation project(":yvisbig-common")
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.cloud:spring-cloud-starter-netflix-eureka-client'
    implementation 'org.mybatis.spring.boot:mybatis-spring-boot-starter:2.1.0'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    implementation 'io.jsonwebtoken:jjwt-api:0.11.5'
    // ... 기타 의존성
}
```

### 5.2 yvisbig-common (공통 라이브러리)

**역할**: 모든 서비스에서 공유하는 공통 컴포넌트

| 패키지 | 역할 |
|--------|------|
| `security` | JWT 토큰 처리, Spring Security 설정 |
| `exception` | 전역 예외 처리, 에러 타입 정의 |
| `util` | 암호화, Redis, 문자열 처리 유틸 |
| `dto` | 공통 데이터 전송 객체 |
| `enums` | 비즈니스 열거형 정의 |

**빌드 설정** (`build.gradle`):
```gradle
jar {
    enabled = true      // 라이브러리 JAR 생성
}

bootJar {
    enabled = false     // 실행 JAR 생성 안함
}
```

### 5.3 yvisbig-api (API 서비스)

**역할**: 비즈니스 API 제공 (포트: 8087)

---

## 6. 데이터베이스 설계

### 6.1 ERD

```
┌─────────────────────────────────────┐
│          svr_media_key              │
├─────────────────────────────────────┤
│ PK │ media_key        VARCHAR(100)  │
│    │ media_id         VARCHAR(100)  │
│    │ media_secret_key VARCHAR(1000) │ ◀── AES-256 암호화
│    │ create_id        VARCHAR(50)   │
│    │ create_dt        TIMESTAMP     │
│    │ update_id        VARCHAR(50)   │
│    │ update_dt        TIMESTAMP     │
└─────────────────────────────────────┘
                │
                │ 1:N
                ▼
┌─────────────────────────────────────┐
│            svr_user                 │
├─────────────────────────────────────┤
│ PK │ user_sq          INT (AI)      │
│ FK │ media_id         VARCHAR(100)  │
│ UK │ user_id          VARCHAR(100)  │ ◀── (media_id, user_id) UNIQUE
│    │ user_nm          VARCHAR(100)  │
│    │ create_id        VARCHAR(50)   │
│    │ create_dt        TIMESTAMP     │
│    │ update_id        VARCHAR(50)   │
│    │ update_dt        TIMESTAMP     │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│          kdmp_mbr_info              │
├─────────────────────────────────────┤
│ PK │ mbr_sq           BIGINT (AI)   │
│    │ mbr_id           VARCHAR(100)  │
│    │ mbr_nm           VARCHAR(100)  │
│    │ mbr_mobile_phone VARCHAR(20)   │
│    │ mbr_ci           VARCHAR(200)  │
│    │ mbr_st           CHAR(1)       │ ◀── I/M/D/W
│    │ exit_dt          TIMESTAMP     │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│         kdmp_mbr_detail             │
├─────────────────────────────────────┤
│ PK │ mbr_sq           BIGINT        │
│ FK │                                │
│    │ mbr_email        VARCHAR(100)  │
│    │ mbr_drv_licnum   VARCHAR(50)   │
│    │ mbr_drv_insurnum VARCHAR(50)   │
│    │ mbr_basic_address VARCHAR(200) │
│    │ mbr_detail_address VARCHAR(200)│
│    │ mbr_wdt_account  VARCHAR(50)   │
│    │ mbr_vir_account  VARCHAR(50)   │
│    │ ... (기타 상세 정보)           │
└─────────────────────────────────────┘
```

### 6.2 테이블 정의

#### svr_media_key (언론사 키 정보)

| 컬럼명 | 데이터 타입 | 제약조건 | 설명 |
|--------|------------|----------|------|
| media_id | VARCHAR(100) | NOT NULL | 언론사 ID |
| media_key | VARCHAR(100) | PK | 언론사 발급 키 |
| media_secret_key | VARCHAR(1000) | NOT NULL | 비밀키 (AES-256 암호화) |
| create_id | VARCHAR(50) | | 생성자 ID |
| create_dt | TIMESTAMP | | 생성 일시 |
| update_id | VARCHAR(50) | | 수정자 ID |
| update_dt | TIMESTAMP | | 수정 일시 |

#### svr_user (서버 사용자 정보)

| 컬럼명 | 데이터 타입 | 제약조건 | 설명 |
|--------|------------|----------|------|
| user_sq | INT | PK, AI | 사용자 순번 |
| media_id | VARCHAR(100) | NOT NULL | 언론사 ID |
| user_id | VARCHAR(100) | NOT NULL | 사용자 ID |
| user_nm | VARCHAR(100) | | 사용자 이름 |
| create_id | VARCHAR(50) | | 생성자 ID (기본: 'SYS') |
| create_dt | TIMESTAMP | | 생성 일시 |
| update_id | VARCHAR(50) | | 수정자 ID |
| update_dt | TIMESTAMP | | 수정 일시 |

**인덱스**: `UNIQUE (media_id, user_id)`

### 6.3 MyBatis 매퍼

#### auth.xml

```xml
<mapper namespace="com.yjmedia.yvisbig.baseauth.module.auth.AuthRepository">

  <!-- 언론사 키 정보 조회 -->
  <select id="getMediaKeyInfo" parameterType="string" resultType="SvrMediaKeyDTO">
    SELECT media_id, media_key, media_secret_key, create_dt, update_id, update_dt
    FROM svr_media_key
    WHERE media_key = #{mediaKey}
  </select>

  <!-- 서버 사용자 등록 -->
  <insert id="insertSvrUser" parameterType="SvrUserDTO">
    INSERT INTO svr_user (media_id, user_id, user_nm, create_id, create_dt)
    VALUES (#{mediaId}, #{userId}, #{userNm}, 'SYS', CURRENT_TIMESTAMP())
  </insert>

  <!-- 서버 사용자 수정 -->
  <update id="updateSvrUser" parameterType="SvrUserDTO">
    UPDATE svr_user
    SET media_id = #{userId}, user_id = #{userId}, user_nm = #{userNm},
        update_id = 'SYS', update_dt = CURRENT_TIMESTAMP()
    WHERE user_sq = #{userSq}
  </update>

  <!-- 서버 사용자 조회 -->
  <select id="selectSvrUserWithId" parameterType="HashMap" resultType="SvrUserDTO">
    SELECT media_id, user_id, user_nm, create_id, create_dt, update_id, update_dt
    FROM svr_user
    WHERE media_id = #{mediaId} AND user_id = #{userId}
  </select>

</mapper>
```

#### schauth.xml (배치용)

```xml
<mapper namespace="com.yjmedia.yvisbig.baseauth.module.batch.SchAuthRepository">

  <!-- 프로시저 호출 -->
  <select id="callPROC_STS_3_5" statementType="CALLABLE" resultType="String">
    {call PROC_STS_3_5(#{stsTp})}
  </select>

  <!-- 탈퇴 3개월 경과 회원 조회 -->
  <select id="selectDeleteMemberSqs" resultType="java.lang.Long">
    SELECT mbr_sq FROM kdmp_mbr_info
    WHERE mbr_st = 'W'
    AND exit_dt <= DATE_ADD(NOW(), INTERVAL -3 MONTH)
    AND NOT mbr_nm = '---'
  </select>

  <!-- 회원 정보 마스킹 처리 -->
  <update id="updateMbrInfoData" parameterType="java.util.List">
    UPDATE kdmp_mbr_info
    SET mbr_nm = '---', mbr_mobile_phone = '-----------', mbr_ci = mbr_id
    WHERE mbr_sq IN
    <foreach collection="list" item="deleteMemberSq" open="(" separator="," close=")">
      #{deleteMemberSq}
    </foreach>
  </update>

</mapper>
```

---

## 7. 엔티티 및 DTO 정의

### 7.1 공통 DTO (yvisbig-common)

#### SvrMediaKeyDTO

```java
@Data
public class SvrMediaKeyDTO {
    private String mediaId;           // 언론사 ID
    private String mediaKey;          // 언론사 키 (PK)
    private String mediaSecretKey;    // 비밀키 (AES-256 암호화)
    private String createId;          // 생성자 ID
    private LocalDateTime createDt;   // 생성 일시
    private String updateId;          // 수정자 ID
    private LocalDateTime updateDt;   // 수정 일시
}
```

#### SvrUserDTO

```java
@Data
public class SvrUserDTO {
    int userSq;        // 사용자 순번 (PK)
    String mediaId;    // 언론사 ID
    String userId;     // 사용자 ID
    String userNm;     // 사용자 이름
}
```

#### UserDTO

```java
@Data
public class UserDTO {
    long mbrSq;            // 회원 순번
    String mbrId;          // 회원 ID
    String mbrPwd;         // 회원 비밀번호
    String mbrPrivilegeTp; // 회원 권한 유형 (CMMB/DMMB/ADMN)
    String mbrNm;          // 회원 이름
}
```

### 7.2 Request/Response VO

#### RequestHeaderVO (기본 요청 헤더)

```java
@Data
public class RequestHeaderVO {
    private String clientVersion;  // 클라이언트 버전
    private String clientId;       // 디바이스 ID
}
```

#### ResponseHeaderVO (기본 응답 헤더)

```java
@Getter @Setter
public class ResponseHeaderVO {
    private String serverVersion = GlobalConstants.YVISBIG_MSA_SERVER_VERSION;
    private String serverId = GlobalConstants.YVISBIG_MSA_SERVER_ID;
}
```

#### SvrGetTokenReqVO (토큰 발급 요청)

```java
@Data
public class SvrGetTokenReqVO extends RequestHeaderVO {
    String mediaId;    // 언론사 ID
    String userId;     // 사용자 ID
    String userNm;     // 사용자 이름
    String mediaKey;   // 언론사 발급 키
    String callDate;   // 호출 일시 (yyyyMMddHHmmss)
    String hmacHash;   // HMAC-SHA256 해시값
}
```

#### SvrGetTokenResVO (토큰 발급 응답)

```java
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class SvrGetTokenResVO extends ResponseHeaderVO {
    public String jwt;  // JWT Access Token
}
```

#### SvrRefreshTokenReqVO / SvrRefreshTokenResVO

토큰 갱신 요청/응답은 `SvrGetTokenReqVO`/`SvrGetTokenResVO`와 동일한 구조입니다.

### 7.3 에러 응답 VO

#### ServerErrorResponse

```java
@Builder
public class ServerErrorResponse {
    private int bizErrCode;       // 비즈니스 에러 코드
    private String message;       // 에러 메시지
    private String detailMessage; // 상세 메시지
    private String path;          // 요청 경로
    private String messageKey;    // 국제화 키
}
```

---

## 8. API 명세

### 8.1 일반 로그인/토큰 API (`/v1/auth-svr/auth`)

#### POST /auth/login

**설명**: ID/PW 기반 로그인, JWT 발급

| 항목 | 내용 |
|------|------|
| URL | `/v1/auth-svr/auth/login` |
| Method | POST |
| Content-Type | `application/json` |
| 권한 | PUBLIC |

**Request Body**:
```json
{
  "mediaId": "YJMEDIA",
  "userId": "user001",
  "password": "비밀번호",
  "lastLoginIp": "127.0.0.1"
}
```

**Response Body** (성공):
```json
{
  "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "userId": "user001",
  "userNm": "홍길동",
  "mediaId": "YJMEDIA"
}
```

> Refresh Token은 `msa_refresh_token` httpOnly 쿠키로 설정됩니다 (응답 JSON에 미포함).

---

#### POST /auth/refresh

**설명**: Access Token 갱신 (Refresh Token Rotation)

| 항목 | 내용 |
|------|------|
| URL | `/v1/auth-svr/auth/refresh` |
| Method | POST |
| 권한 | PUBLIC |

**Request Body**:
```json
{
  "mediaId": "YJMEDIA",
  "userId": "user001",
  "refreshToken": "(쿠키 없을 때만 body에 포함)"
}
```

> `msa_refresh_token` 쿠키가 있으면 body의 refreshToken보다 쿠키를 우선 사용합니다.

**Response**: `/auth/login`과 동일 구조

---

### 8.2 SNS OAuth2 API (`/v1/auth-svr/auth/sns`)

#### GET /auth/sns/authorize/{provider}

**설명**: SNS 소셜 로그인 인가 페이지로 리다이렉트

| 항목 | 내용 |
|------|------|
| URL | `/v1/auth-svr/auth/sns/authorize/{provider}` |
| Method | GET |
| 권한 | PUBLIC |
| Path Variable | `provider`: `kakao` / `naver` / `google` / `facebook` |
| Query Param | `redirectUri` (선택): 로그인 완료 후 클라이언트 콜백 URL |

**동작**: State 생성 후 Redis 저장 → SNS 인가 페이지로 302 리다이렉트

---

#### GET /auth/sns/callback/{provider}

**설명**: SNS 인가 후 콜백. JWT 발급 후 클라이언트로 리다이렉트

| 항목 | 내용 |
|------|------|
| URL | `/v1/auth-svr/auth/sns/callback/{provider}` |
| Method | GET |
| 권한 | PUBLIC (SNS가 호출) |
| Query Params | `code`, `state` (또는 `error`, `error_description`) |

**성공 시 리다이렉트**:
```
{redirectUri}?accessToken=eyJ...&tokenType=Bearer&expiresIn=3600&userId=xxx&userNm=홍길동
```

> Refresh Token은 `msa_refresh_token` httpOnly 쿠키로 설정됩니다.

**실패 시 리다이렉트**:
```
{defaultRedirectUri}?error=sns_auth_failed&message=...
```

---

### 8.3 HMAC 방식 토큰 API (레거시)

#### POST /getToken

**설명**: HMAC 검증 후 JWT 토큰 발급 (서버간 연동용)

| 항목 | 내용 |
|------|------|
| URL | `/v1/auth-svr/getToken` |
| Method | POST |
| Content-Type | `application/json` |
| 권한 | PUBLIC |

**Request Body**:
```json
{
  "mediaId": "HANKOOK",
  "userId": "user001",
  "userNm": "홍길동",
  "mediaKey": "12312s3213123xqweqwe123",
  "callDate": "20241215120000",
  "hmacHash": "a1b2c3d4e5f6...",
  "clientVersion": "1.0.0",
  "clientId": "device-uuid-1234"
}
```

**Response Body** (성공):
```json
{
  "serverVersion": "V1",
  "serverId": "AUTH",
  "jwt": "eyJhbGciOiJIUzUxMiJ9..."
}
```

**HMAC 해시 생성 방법**:
```
message = mediaId + userId + userNm + callDate
hmacHash = HMAC-SHA256(message, mediaSecretKey)
```

---

#### POST /refreshToken

**설명**: HMAC 방식 토큰 갱신

| 항목 | 내용 |
|------|------|
| URL | `/v1/auth-svr/refreshToken` |
| Method | POST |
| 권한 | PUBLIC |

Request/Response는 `/getToken`과 동일합니다.

---

#### GET /getEncodeMediaSecret

**설명**: 언론사 비밀키 AES-256 암호화

| 항목 | 내용 |
|------|------|
| URL | `/v1/auth-svr/getEncodeMediaSecret` |
| Method | GET |
| 권한 | PUBLIC |
| Parameter | `strSecretKey` (String) |

**Response**: AES-256으로 암호화된 비밀키 문자열

---

#### GET /health

**설명**: 헬스체크

| 항목 | 내용 |
|------|------|
| URL | `/v1/auth-svr/health` |
| Method | GET |
| 권한 | PUBLIC |
| 응답 | `"OK"` |

---

### 8.4 Swagger UI

| 환경 | URL |
|------|-----|
| Local | http://localhost:8085/swagger-ui.html |
| API Docs | http://localhost:8085/api-docs/json |

---

## 9. 인증 흐름

### 9.1 SNS OAuth2 로그인 흐름

```
┌────────┐    ┌──────────────┐    ┌──────────────┐    ┌─────────┐    ┌──────────┐
│ Client │    │  Auth Server │    │  SNS Provider│    │  MySQL  │    │  Redis   │
└────┬───┘    └──────┬───────┘    └──────┬───────┘    └────┬────┘    └────┬─────┘
     │               │                   │                  │              │
     │  1. GET /auth/sns/authorize/kakao?redirectUri=...    │              │
     │───────────────▶│                  │                  │              │
     │               │  2. state 생성 → Redis 저장 (10분)   │              │
     │               │──────────────────────────────────────────────────────▶
     │               │                  │                  │              │
     │  3. 302 → 카카오 인가 페이지     │                  │              │
     │◀───────────────│                  │                  │              │
     │               │                  │                  │              │
     │  4. 사용자 카카오 로그인 & 동의  │                  │              │
     │──────────────────────────────────▶                   │              │
     │               │                  │                  │              │
     │               │  5. GET /auth/sns/callback/kakao?code=xxx&state=yyy │
     │               │◀─────────────────│                  │              │
     │               │                  │                  │              │
     │               │  6. state 검증 (Redis)               │              │
     │               │──────────────────────────────────────────────────────▶
     │               │  7. code → Access Token 교환         │              │
     │               │──────────────────▶                   │              │
     │               │  8. 사용자 정보 조회                 │              │
     │               │──────────────────▶                   │              │
     │               │  9. MH_EXT_MEMBER 조회/생성          │              │
     │               │─────────────────────────────────────▶│              │
     │               │  10. JWT 발급 + Refresh Token 생성   │              │
     │               │──────────────────────────────────────────────────────▶
     │               │                  │                  │              │
     │  11. 302 → {redirectUri}?accessToken=xxx + 쿠키(msa_refresh_token)  │
     │◀───────────────│                  │                  │              │
```

### 9.2 일반 로그인 흐름

```
┌────────┐       ┌────────────┐       ┌────────────┐       ┌─────────┐
│ Client │       │  Auth API  │       │  Database  │       │  Redis  │
└────┬───┘       └─────┬──────┘       └─────┬──────┘       └────┬────┘
     │                 │                    │                   │
     │  1. POST /auth/login                 │                   │
     │  (mediaId, userId, password)         │                   │
     │────────────────▶│                    │                   │
     │                 │  2. MH_EXT_MEMBER 조회                 │
     │                 │───────────────────▶│                   │
     │                 │  3. KISA SHA-256 비밀번호 검증         │
     │                 │                    │                   │
     │                 │  4. JWT 생성 (HS512, 1시간)            │
     │                 │  5. Refresh Token 생성 → Redis 저장    │
     │                 │────────────────────────────────────────▶
     │                 │  6. 로그인 정보 업데이트               │
     │                 │───────────────────▶│                   │
     │                 │                    │                   │
     │  7. { accessToken, tokenType, expiresIn, userId, userNm }│
     │  + Set-Cookie: msa_refresh_token (httpOnly, 30일)        │
     │◀────────────────│                    │                   │
```

### 9.3 HMAC 방식 토큰 발급 시퀀스 (서버간 연동)

```
┌────────┐       ┌────────────┐       ┌────────────┐       ┌─────────┐
│ Client │       │  Auth API  │       │  Database  │       │  Redis  │
└────┬───┘       └─────┬──────┘       └─────┬──────┘       └────┬────┘
     │                 │                    │                   │
     │  1. POST /getToken                   │                   │
     │  (mediaId, userId, hmacHash...)      │                   │
     │────────────────▶│                    │                   │
     │                 │                    │                   │
     │                 │  2. getMediaKeyInfo(mediaKey)          │
     │                 │───────────────────▶│                   │
     │                 │                    │                   │
     │                 │  3. SvrMediaKeyDTO │                   │
     │                 │◀───────────────────│                   │
     │                 │                    │                   │
     │                 │  4. AES-256 복호화 (mediaSecretKey)    │
     │                 │                    │                   │
     │                 │  5. HMAC-SHA256 검증                   │
     │                 │  msg = mediaId + userId + userNm + callDate
     │                 │  verify(hmacHash, HMAC(msg, secretKey))│
     │                 │                    │                   │
     │                 │  6. JWT 토큰 생성 (HS512)              │
     │                 │                    │                   │
     │                 │  7. 사용자 정보 저장/업데이트          │
     │                 │───────────────────▶│                   │
     │                 │                    │                   │
     │                 │  8. 세션 정보 저장 (Optional)          │
     │                 │────────────────────────────────────────▶
     │                 │                    │                   │
     │  9. JWT Token   │                    │                   │
     │◀────────────────│                    │                   │
     │                 │                    │                   │
```

### 9.4 API 요청 인증 흐름

```
┌────────┐       ┌────────────────┐       ┌─────────────┐       ┌────────────┐
│ Client │       │ JwtFilter      │       │ Security    │       │ Controller │
└────┬───┘       └───────┬────────┘       └──────┬──────┘       └─────┬──────┘
     │                   │                       │                    │
     │  1. Request + JWT Token                   │                    │
     │  Authorization: Bearer eyJ...             │                    │
     │──────────────────▶│                       │                    │
     │                   │                       │                    │
     │                   │  2. 토큰 추출 & 검증  │                    │
     │                   │  validateToken(token) │                    │
     │                   │                       │                    │
     │                   │  3. Authentication 객체 생성               │
     │                   │  getAuthentication(token)                  │
     │                   │                       │                    │
     │                   │  4. SecurityContext 설정                   │
     │                   │───────────────────────▶                    │
     │                   │                       │                    │
     │                   │                       │  5. 권한 확인      │
     │                   │                       │  hasRole("CMMB")   │
     │                   │                       │────────────────────▶
     │                   │                       │                    │
     │                   │                       │                    │  6. 비즈니스 로직
     │                   │                       │                    │
     │  7. Response      │                       │                    │
     │◀──────────────────────────────────────────────────────────────│
     │                   │                       │                    │
```

### 9.5 JWT 토큰 구조

```
Header:
{
  "alg": "HS512",
  "typ": "JWT"
}

Payload:
{
  "sub": "auth",
  "mediaId": "YJMEDIA",
  "userId": "user001",
  "exp": 1702623600
}

Signature:
HMACSHA512(base64UrlEncode(header) + "." + base64UrlEncode(payload), secret)
```

| 클레임 | 설명 | 값 |
|--------|------|-----|
| sub | 토큰 주체 | `"auth"` 고정 |
| mediaId | 언론사 ID | 발급 시 설정 |
| userId | 사용자 ID | 발급 시 설정 |
| exp | 만료 시간 | 발급 시각 + 3600초 (1시간) |

---

## 10. 보안 설정

### 10.1 Spring Security 설정

#### Local 환경 (`SecurityConfig.java`)

```java
@Profile("local")
public SecurityFilterChain filterChain(HttpSecurity http) {
    http
        .csrf().disable()
        .cors().disable()
        .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .authorizeRequests()
            .anyRequest().permitAll();  // 개발 환경: 전체 오픈

    return http.build();
}
```

#### Production 환경

```java
@Profile({"dev", "prod"})
public SecurityFilterChain filterChain(HttpSecurity http) {
    http
        .authorizeRequests()
        // Public API
        .antMatchers(HttpMethod.POST, "/v1/auth-svr/login").permitAll()
        .antMatchers(HttpMethod.POST, "/v1/auth-svr/refreshToken").permitAll()
        .antMatchers(HttpMethod.GET, "/v1/auth-svr/health").permitAll()
        .antMatchers("/swagger-ui/**", "/api-docs/**").permitAll()

        // Role-Based Access
        .antMatchers("/v1/biztotal/cm/**").hasRole("CMMB")   // 고객
        .antMatchers("/v1/biztotal/dm/**").hasRole("DMMB")   // 대리기사
        .antMatchers("/v1/biztotal/adm/**").hasRole("ADMN")  // 관리자

        // 그 외: 인증 필요
        .anyRequest().authenticated();

    return http.build();
}
```

### 10.2 권한 체계

| 역할 | 코드 | 설명 | 접근 가능 경로 |
|------|------|------|----------------|
| 고객 | CMMB | 일반 고객 사용자 | `/v1/biztotal/cm/**` |
| 대리기사 | DMMB | 대리기사 회원 | `/v1/biztotal/dm/**` |
| 관리자 | ADMN | 시스템 관리자 | `/v1/biztotal/adm/**` |

### 10.3 토큰 검증 스킵 경로

JWT 토큰 검증을 스킵하는 경로:

```java
private static final String[] SKIP_PATHS = {
    "/Login",
    "/auth-svr/refreshToken",
    "/biztotal/cm/listTerm",
    "/biztotal/cm/getTerm",
    "/biztotal/dm/listTerm",
    "/biztotal/dm/getTerm",
    "/biztotal/webview/mobilians/getMobilSelfAuthInfo",
    "/biztotal/cb/regVirtualPaymentRestNotice",
    "/health",
    "/swagger-ui",
    "/mbr/newMember"
};
```

### 10.4 JWT 설정값

```yaml
jwt:
  header: Authorization
  system-secret: YVISBIGAUTHKEY80XXXZZZ...  # Base64 인코딩된 비밀키
  token-validity-in-seconds: 3600           # Access Token: 1시간
  refresh-token-validity-in-seconds: 2592000 # Refresh Token: 30일
```

### 10.5 SNS OAuth2 설정

```yaml
sns:
  providers:
    kakao:
      client-id: <카카오 앱 키>
      client-secret: <카카오 앱 시크릿>
      authorization-uri: https://kauth.kakao.com/oauth/authorize
      token-uri: https://kauth.kakao.com/oauth/token
      user-info-uri: https://kapi.kakao.com/v2/user/me
    naver:
      client-id: <네이버 클라이언트 ID>
      client-secret: <네이버 클라이언트 시크릿>
      authorization-uri: https://nid.naver.com/oauth2.0/authorize
      token-uri: https://nid.naver.com/oauth2.0/token
      user-info-uri: https://openapi.naver.com/v1/nid/me
    google:
      client-id: <구글 클라이언트 ID>
      client-secret: <구글 클라이언트 시크릿>
      authorization-uri: https://accounts.google.com/o/oauth2/v2/auth
      token-uri: https://www.googleapis.com/oauth2/v4/token
      user-info-uri: https://www.googleapis.com/oauth2/v3/userinfo
    facebook:
      client-id: <페이스북 앱 ID>
      client-secret: <페이스북 앱 시크릿>
      authorization-uri: https://www.facebook.com/v18.0/dialog/oauth
      token-uri: https://graph.facebook.com/v18.0/oauth/access_token
      user-info-uri: https://graph.facebook.com/me?fields=id,email,name
  default-redirect-uri: http://localhost:81/sns/callback  # 로그인 완료 후 기본 리다이렉트

app:
  cors:
    allowed-origins: http://localhost:81,http://localhost:8085  # 허용 출처
  cookie:
    domain: localhost
    secure: false  # 운영 환경에서는 true
```

> SNS 각 프로바이더 개발자 콘솔에 콜백 URL 등록 필요:
> `http://{서버주소}/v1/auth-svr/auth/sns/callback/{provider}`

### 10.6 Refresh Token 쿠키 정책

| 속성 | 값 |
|------|-----|
| 쿠키명 | `msa_refresh_token` |
| HttpOnly | true (XSS 방어) |
| Secure | false (로컬) / true (운영) |
| SameSite | Lax |
| Path | `/v1/auth-svr/auth` |
| MaxAge | 2592000초 (30일) |
| Domain | `app.cookie.domain` 설정값 |

---

## 11. 암호화 메커니즘

### 11.1 CryptoUtil 클래스

#### AES-256 암호화/복호화

```java
// 암호화
public static String encryptAES256(String msg, String key)

// 복호화
public static String decryptAES256(String msg, String key)
```

**알고리즘 상세**:

| 항목 | 값 |
|------|-----|
| Algorithm | AES/CBC/PKCS5Padding |
| Key Size | 256 bits |
| Key Derivation | PBKDF2WithHmacSHA1 |
| Iterations | 70,000 |
| Salt Size | 20 bytes |
| IV Size | 16 bytes |

**암호화 포맷**:
```
Base64( Salt[20 bytes] + IV[16 bytes] + CipherText )
```

#### HMAC-SHA256

```java
public static String getHMAC(String msg, String secretKey, String algorithm)
```

**사용 예시**:
```java
String msg = mediaId + userId + userNm + callDate;
String hmac = CryptoUtil.getHMAC(msg, secretKey, "HmacSHA256");
```

#### 해시 함수

```java
// SHA-256
public static String sha256(String msg)

// MD5
public static String md5(String msg)

// 비밀번호 암호화 (Salt + SHA-256)
public static String getPasswordEncryt(String msg)
// Salt: "REMOVED_SALT_KEY"
```

### 11.2 암호화 흐름

```
┌──────────────────────────────────────────────────────────────────┐
│                    언론사 비밀키 암호화 저장                      │
├──────────────────────────────────────────────────────────────────┤
│                                                                  │
│  원본 비밀키: "qwert1234"                                        │
│       │                                                          │
│       ▼                                                          │
│  시스템 비밀키: "YVISBIGAUTHKEY80XXXZZZ..."                      │
│       │                                                          │
│       ▼                                                          │
│  AES-256 암호화 (PBKDF2 + CBC)                                  │
│       │                                                          │
│       ▼                                                          │
│  암호화된 비밀키: "Base64EncodedString..."                       │
│       │                                                          │
│       ▼                                                          │
│  DB 저장 (svr_media_key.media_secret_key)                       │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────────────────┐
│                    토큰 발급 시 HMAC 검증                         │
├──────────────────────────────────────────────────────────────────┤
│                                                                  │
│  1. DB에서 암호화된 비밀키 조회                                  │
│       │                                                          │
│       ▼                                                          │
│  2. AES-256 복호화 → 원본 비밀키                                │
│       │                                                          │
│       ▼                                                          │
│  3. 서버측 HMAC 계산                                            │
│     msg = mediaId + userId + userNm + callDate                  │
│     serverHmac = HMAC-SHA256(msg, 원본비밀키)                   │
│       │                                                          │
│       ▼                                                          │
│  4. 클라이언트 HMAC과 비교                                      │
│     if (serverHmac == clientHmac) → JWT 발급                    │
│     else → 인증 실패 (22001)                                    │
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
```

---

## 12. 에러 코드 정의

### 12.1 에러 코드 체계

| 범위 | 분류 |
|------|------|
| 0 | 성공 |
| 21xxx | 파일/요청 오류 |
| 22xxx | 인증/권한 오류 |
| 25xxx | 데이터베이스 오류 |
| 26xxx | 서버 내부 오류 |
| 40xxx | 외부 인터페이스 오류 |
| 90xxx | 알 수 없는 오류 |

### 12.2 상세 에러 코드

#### 인증/권한 오류 (22xxx)

| 코드 | 이름 | 메시지 | 상세 |
|------|------|--------|------|
| 22001 | JWT_NOT_AUTH | Not Auth | HMAC 인증 실패 |
| 22002 | JWT_NOT_ADMIN | Not Admin | 사이트 관리자가 아님 |
| 22003 | JWT_NOT_PERMISSION | Has not Permission | 권한 없음 |
| 22004 | JWT_NOT_EXIST_USER | User not exist | 사용자 존재하지 않음 |
| 22005 | JWT_REGIST_USEREREDIS_ERROR | User Redis Info error | 사용자 레디스 등록 오류 |
| 22006 | JWT_INVALID_PASSWORD | Invalid password | 비밀번호 불일치 |
| 22007 | JWT_INVALID_REFRESH_TOKEN | Invalid refresh token | 유효하지 않은 리프레시 토큰 |
| 22010 | JWT_TOKEN_TIME_OUT | Token Timeout | 토큰 타임아웃 |
| 22011 | JWT_TOKEN_REFRESH_DIFF | Token Auto Refresh Difference | 리프레시키 불일치 |
| 22012 | JWT_TOKEN_REFRESH_TIMEOUT | Token Auto Refresh time out | 리프레시 토큰 만료 |
| 22013 | LOGIN_DEVICEID_DIFF | device Id diff | 디바이스 ID 불일치 |
| 22014 | LOGIN_CMDM_DIFF | client,driver check | 고객/기사 구분 필요 |
| 22020 | JWT_SNS_INVALID_STATE | SNS state invalid | OAuth2 state 불일치 (CSRF 방어) |
| 22021 | JWT_SNS_CONSENT_REQUIRED | SNS consent required | SNS 필수 동의 항목 미동의 (이름/이메일) |
| 22022 | JWT_SNS_AUTH_FAILED | SNS auth failed | SNS 토큰 교환/사용자 정보 조회 실패 |
| 22023 | JWT_SNS_USER_BLOCKED | SNS user blocked | 차단된 SNS 사용자 |

#### 파일/요청 오류 (21xxx)

| 코드 | 이름 | 메시지 |
|------|------|--------|
| 21009 | IO_EXCEPTION | IOException 오류 |
| 21010 | REQUEST_PARAM_NULL | 파라미터 정보 없음 |
| 21011 | REQUEST_UPFIEL_ZERO | 업로드 파일 없음 |
| 21012 | FILE_MAKE_DIRECTORY | 디렉토리 생성 오류 |
| 21013 | FILE_UPLOAD_UNKNOWN | 알 수 없는 업로드 파일 |
| 21014 | REQUEST_PARAM_ERROR | 파라미터 정보 오류 |
| 21015 | FILE_NOT_FOUND | 파일을 찾을 수 없음 |

#### 데이터베이스 오류 (25xxx)

| 코드 | 이름 | 메시지 |
|------|------|--------|
| 25001 | SQL_GENERAL_NODATA | 데이터 없음/잘못된 업데이트 |
| 25002 | SQL_GRAMMER_EXCEPTION | SQL 구문 오류 |

#### 서버 오류 (26xxx, 40xxx, 90xxx)

| 코드 | 이름 | 메시지 |
|------|------|--------|
| 26001 | SERVER_INTERNAL_EXCEPTION | 서버 내부 오류 |
| 40001 | EXIF_HTTPCALL_ERROR | 외부 호출 오류 |
| 40002 | EXIF_FCM_TOKENEXFIRE | FCM 토큰 만료 |
| 40003 | EXIF_TGMOBIL_ERROR | TG모빌리언스 연동 오류 |
| 90001 | UNKNOWN | 알 수 없는 오류 |
| 90002 | BINDING_ERROR | 파라미터 바인딩 오류 |

### 12.3 예외 처리 응답

**HTTP Status Code**: 모든 비즈니스 예외는 `200 OK`로 응답 (일부 인증 관련 예외는 `401`)

**응답 형식**:
```json
{
  "bizErrCode": 22001,
  "message": "Not Auth",
  "detailMessage": "인증실패:HMAC 인증 실패.",
  "path": "/v1/auth-svr/getToken",
  "messageKey": "NOT_AUTH"
}
```

---

## 13. Enum 정의

### 13.1 인증/권한 관련

#### MbrPriviligeType (회원 권한 유형)

```java
public enum MbrPriviligeType {
    CMMB("CMMB"),  // 고객 사용자
    DMMB("DMMB"),  // 대리기사 회원
    ADMN("ADMN");  // 관리자
}
```

#### AccessScopeType (접근 범위)

```java
public enum AccessScopeType {
    SYSTEM("system", "admin"),     // 시스템 관리자
    PRIVATE("private", "authcheck"), // 인증 필요
    PUBLIC("public", "openall");   // 공개
}
```

#### RedisKeyType (Redis 키 유형)

```java
public enum RedisKeyType {
    REDISKEY_REFRESHTOKEN,  // 리프레시 토큰
    REDISKEY_USERINFO       // 사용자 정보
}
```

### 13.2 회원 상태 관련

#### MbrState (회원 상태)

```java
public enum MbrState {
    I("I"),  // 임시저장
    M("M"),  // 가입완료
    D("D"),  // 휴면계정
    W("W");  // 탈퇴
}
```

### 13.3 비즈니스 Enum 목록

| Enum | 설명 |
|------|------|
| DrvRequestState | 기사 요청 상태 |
| DmRegReqType | DM 등록 요청 유형 |
| DmReqwdtSt | DM 요청 대기 상태 |
| DmWorkType | DM 작업 유형 |
| DmDrvCancelType | DM 기사 취소 유형 |
| CmDrvCancelType | CM 기사 취소 유형 |
| DmCmmuaddTp | DM 커뮤니케이션 추가 유형 |
| DmCmmuTp | DM 커뮤니케이션 유형 |
| DmPicType | DM 사진 유형 |
| AdmFileType | 관리 파일 유형 |
| AdmPicType | 관리 사진 유형 |
| MbrRegisterType | 회원 등록 유형 |
| MbrUseType | 회원 사용 유형 |
| ChangeRegprogressSt | 등록 진행 상태 변경 |
| EduMovSt | 교육 무비 상태 |
| IncomReqSt | 수입 요청 상태 |
| IncomReqUseTp | 수입 요청 사용 유형 |
| InqRtnSt | 조회 반환 상태 |
| MessageQueueType | 메시지 큐 유형 |
| NotiSvrSt | 알림 서버 상태 |
| PaymentKind | 결제 종류 |
| PromotionSt | 프로모션 상태 |
| TrmSvrSt | 약관 서버 상태 |
| TrmTp | 약관 유형 |

---

## 14. 환경 설정

### 14.1 프로필 구성

```yaml
spring:
  profiles:
    active: local  # 기본 프로필
    group:
      local:
        - common
      dev:
        - common
      prod:
        - common
```

### 14.2 환경별 설정

#### Local 환경

| 설정 | 값 |
|------|-----|
| 서버 포트 | 8085 |
| DB URL | jdbc:mysql://REMOVED_DB_HOST:3306/yvisbig_local |
| DB 사용자 | mysql |
| Redis Host | localhost:6379 |
| Redis Key Prefix | LOCAL |
| Eureka 등록 | false |
| 로그 경로 | C:\\Temp\\ |

#### Dev 환경

| 설정 | 값 |
|------|-----|
| 서버 포트 | 8085 |
| DB URL | jdbc:mysql://REMOVED_DB_HOST:3306/yvisbig_local |
| DB 사용자 | dev |
| Redis Host | REMOVED_REDIS_HOST |
| Redis Key Prefix | DEV |
| Eureka 등록 | true |
| 로그 경로 | /opt/project_kdmp/msasvr_auth/logs |

### 14.3 application.yml 전체 구조

```yaml
# 공통 설정
spring:
  application:
    name: auth-service
server:
  port: 8085

# JWT 설정
jwt:
  header: Authorization
  system-secret: YVISBIGAUTHKEY80XXXZZZ...
  token-validity-in-seconds: 86400      # 24시간
  refresh-token-validity-in-seconds: 604800  # 7일

# 데이터소스 설정
spring:
  datasource:
    hikari:
      driver-class-name: com.mysql.cj.jdbc.Driver
      jdbc-url: jdbc:mysql://...
      maximum-pool-size: 10

# Redis 설정
spring:
  redis:
    host: localhost
    port: 6379
    password: REMOVED_PASSWORD
    sessiontime: 3600000  # 1시간
    sessionUseYn: Y

# Eureka 설정
eureka:
  client:
    register-with-eureka: true
    fetch-registry: true
    service-url:
      defaultZone: http://127.0.0.1:8761/eureka

# Swagger 설정
spring-doc:
  packages-to-scan: com.yjmedia.yvisbig.baseauth
  swagger-ui:
    path: swagger-ui.html

# 배치 설정
batch:
  sts:
    newmbrs: true  # 신규 회원 배치 활성화
```

---

## 15. 실행 방법

### 15.1 빌드

```bash
# 프로젝트 루트에서
./gradlew clean build

# 또는 Windows
gradlew.bat clean build
```

### 15.2 실행

#### Local 환경
```bash
java -jar -Dspring.profiles.active=local \
     -Dfile.encoding=UTF-8 \
     yvisbig-auth/build/libs/yvisbig-auth-1.0-SNAPSHOT.jar
```

#### Dev 환경
```bash
java -jar -Dspring.profiles.active=dev \
     -Dfile.encoding=UTF-8 \
     yvisbig-auth-1.0-SNAPSHOT.jar
```

#### Prod 환경
```bash
java -jar -Dspring.profiles.active=prod \
     -Dfile.encoding=UTF-8 \
     yvisbig-auth-1.0-SNAPSHOT.jar
```

### 15.3 서비스 확인

| 확인 항목 | URL |
|----------|-----|
| 헬스체크 | http://localhost:8085/v1/auth-svr/health |
| API 테스트 | http://localhost:8085/v1/auth-svr/simplecheck |
| Swagger UI | http://localhost:8085/swagger-ui.html |
| API Docs | http://localhost:8085/api-docs/json |
| H2 Console | http://localhost:8085/h2-console |

### 15.4 H2 인메모리 DB (로컬 테스트용)

Local 환경에서는 외부 MySQL/Redis 없이 H2 인메모리 데이터베이스로 테스트할 수 있습니다.

> **Note**: yvisbig-auth와 yvisbig-api 모두 H2를 지원합니다.

#### H2 Console 접속 정보

| 모듈 | URL | JDBC URL | Username | Password |
|------|-----|----------|----------|----------|
| yvisbig-auth | http://localhost:8085/h2-console | `jdbc:h2:mem:testdb` | `sa` | (빈값) |
| yvisbig-api | http://localhost:8087/h2-console | `jdbc:h2:mem:testdb` | `sa` | (빈값) |

> Driver Class: `org.h2.Driver` (공통)

#### 자동 생성 테이블

각 모듈 시작 시 `schema.sql`과 `data.sql`이 자동 실행됩니다.

**schema.sql** - 테이블 구조:
```sql
-- 언론사 키 정보
CREATE TABLE svr_media_key (
    media_id VARCHAR(100),
    media_key VARCHAR(100) PRIMARY KEY,
    media_secret_key VARCHAR(1000),
    create_id VARCHAR(50),
    create_dt TIMESTAMP,
    update_id VARCHAR(50),
    update_dt TIMESTAMP
);

-- 서버 사용자 정보
CREATE TABLE svr_user (
    user_sq INT AUTO_INCREMENT PRIMARY KEY,
    media_id VARCHAR(100),
    user_id VARCHAR(100),
    user_nm VARCHAR(100),
    ...
);
```

**data.sql** - 테스트 데이터:
```sql
-- 테스트용 언론사 키
INSERT INTO svr_media_key (media_id, media_key, media_secret_key, ...)
VALUES ('HANKOOK', '12312s3213123xqweqwe123', '암호화된비밀키', ...);

-- 테스트용 사용자
INSERT INTO svr_user (media_id, user_id, user_nm, ...)
VALUES ('HANKOOK', 'testuser001', '테스트유저', ...);
```

#### 설정 (application.yml - local 프로필)

```yaml
spring:
  datasource:
    driver-class-name: org.h2.Driver
    url: jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1
    username: sa
    password:
  h2:
    console:
      enabled: true
      path: /h2-console
  sql:
    init:
      mode: always
      schema-locations: classpath:schema.sql
      data-locations: classpath:data.sql
  redis:
    sessionUseYn: N  # Redis 비활성화
```

### 15.6 Docker 실행 (선택)

```dockerfile
FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG JAR_FILE=yvisbig-auth/build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-jar", "/app.jar"]
```

```bash
docker build -t yvisbig-auth .
docker run -p 8085:8085 yvisbig-auth
```

---

## 16. 테스트 정보

### 16.1 테스트 계정

| 항목 | 값 |
|------|-----|
| 언론사 | 한국일보 |
| 발급키 (mediaKey) | `12312s3213123xqweqwe123` |
| 비밀키 (원문) | `qwert1234` |

### 16.2 시스템 키

| 항목 | 값 |
|------|-----|
| 시스템 비밀키 | `REMOVED_JWT_SECRET` |
| 암호화 방식 | AES-256 (PBKDF2) |

### 16.3 토큰 유효기간

| 토큰 유형 | 유효기간 | 저장 방식 |
|----------|----------|----------|
| Access Token | 3,600초 (1시간) | 클라이언트 메모리/로컬스토리지 |
| Refresh Token | 2,592,000초 (30일) | httpOnly 쿠키 + Redis |

### 16.4 API 테스트 예시

#### 토큰 발급 요청

```bash
curl -X POST http://localhost:8085/v1/auth-svr/getToken \
  -H "Content-Type: application/json" \
  -d '{
    "mediaId": "HANKOOK",
    "userId": "testuser001",
    "userNm": "테스트유저",
    "mediaKey": "12312s3213123xqweqwe123",
    "callDate": "20241215120000",
    "hmacHash": "<HMAC-SHA256 해시값>",
    "clientVersion": "1.0.0",
    "clientId": "test-device-001"
  }'
```

#### HMAC 해시 생성 (Java)

```java
String msg = mediaId + userId + userNm + callDate;
// msg = "HANKOOKtestuser001테스트유저20241215120000"

String hmacHash = CryptoUtil.getHMAC(msg, "qwert1234", "HmacSHA256");
```

---

## 변경 이력

| 버전 | 날짜 | 작성자 | 내용 |
|------|------|--------|------|
| 1.0 | - | - | 최초 작성 |
| 1.1 | - | - | 스케줄러 비활성화 |
| 1.2 | - | - | H2 인메모리 DB 지원 추가 (로컬 테스트용) |
| 1.3 | 2026-03 | - | SNS OAuth2 로그인 추가 (카카오/네이버/구글/페이스북), 일반 ID/PW 로그인 API 추가, Refresh Token Rotation 도입, httpOnly 쿠키 방식으로 Refresh Token 관리, Access Token 유효기간 24h→1h, Refresh Token 유효기간 7d→30d |

---

## 참고 자료

- [Spring Boot 2.7.x Documentation](https://docs.spring.io/spring-boot/docs/2.7.x/reference/html/)
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [JJWT Documentation](https://github.com/jwtk/jjwt)
- [MyBatis Spring Boot Starter](https://mybatis.org/spring-boot-starter/)
- [Netflix Eureka](https://cloud.spring.io/spring-cloud-netflix/reference/html/)
