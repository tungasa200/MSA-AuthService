package com.yjmedia.yvisbig.baseauth.voProtocol;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 회원가입 응답 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원가입 응답")
public class UserRegisterResVO {

    @Schema(description = "성공 여부")
    private boolean success;

    @Schema(description = "메시지")
    private String message;

    @Schema(description = "생성된 사용자 ID")
    private String userId;

    @Schema(description = "사용자 로그인 ID")
    private String userLogin;

    @Schema(description = "사용자 이름")
    private String userName;

    @Schema(description = "언론사 ID")
    private String mediaId;
}