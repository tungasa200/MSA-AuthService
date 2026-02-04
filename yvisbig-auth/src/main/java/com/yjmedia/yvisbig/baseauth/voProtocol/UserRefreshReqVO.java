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

    @NotBlank(message = "Refresh Token은 필수입니다")
    private String refreshToken;
}