package com.yjmedia.yvisbig.baseauth.voProtocol;

import com.yjmedia.yvisbig.bizcom.voHeader.RequestHeaderVO;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

/**
 * 토큰 갱신 요청 VO
 */
@Data
@ToString(callSuper = true)
public class UserRefreshReqVO extends RequestHeaderVO {

    private String refreshToken; // 쿠키에서 우선 읽음, body는 하위호환용

    private String mediaId;
    
    @NotBlank(message = "User ID는 필수입니다")
    private String userId;
}