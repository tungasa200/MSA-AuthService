package com.yjmedia.yvisbig.baseauth.voProtocol;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 회원가입 요청 VO
 */
@Data
@Schema(description = "회원가입 요청")
public class UserRegisterReqVO {

    @Schema(description = "언론사 ID", example = "YJMEDIA")
    private String mediaId;

    @Schema(description = "언론사 Key", example = "REMOVED_MEDIA_KEY")
    private String mediaKey;

    @Schema(description = "언론사 Secret", example = "REMOVED_MEDIA_SECRET")
    private String mediaSecret;

    @Schema(description = "사용자 로그인 ID", example = "testuser", required = true)
    @NotBlank(message = "사용자 ID는 필수입니다.")
    @Size(min = 4, max = 50, message = "사용자 ID는 4~50자여야 합니다.")
    private String userLogin;

    @Schema(description = "비밀번호", example = "password123", required = true)
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 4, max = 100, message = "비밀번호는 4~100자여야 합니다.")
    private String password;

    @Schema(description = "사용자 이름", example = "홍길동", required = true)
    @NotBlank(message = "사용자 이름은 필수입니다.")
    @Size(max = 100, message = "사용자 이름은 100자 이하여야 합니다.")
    private String userName;

    @Schema(description = "이메일", example = "test@example.com")
    @Email(message = "올바른 이메일 형식이어야 합니다.")
    private String userEmail;

    @Schema(description = "조직 단위 (OU)", example = "DEPARTMENT1")
    private String userOu;

    @Schema(description = "언어 설정", example = "ko")
    private String userLanguage;
}