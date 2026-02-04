package com.yjmedia.yvisbig.baseauth.voProtocol;

import com.yjmedia.yvisbig.bizcom.voHeader.RequestHeaderVO;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

/**
 * 사용자 로그인 요청 VO
 */
@Data
@ToString(callSuper = true)
public class UserLoginReqVO extends RequestHeaderVO {

    @NotBlank(message = "언론사 ID는 필수입니다")
    private String mediaId;

    @NotBlank(message = "사용자 ID는 필수입니다")
    private String userId;

    @NotBlank(message = "비밀번호는 필수입니다")
    private String password;
}