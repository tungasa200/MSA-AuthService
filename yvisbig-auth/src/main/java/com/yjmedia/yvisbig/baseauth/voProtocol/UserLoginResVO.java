package com.yjmedia.yvisbig.baseauth.voProtocol;

import com.yjmedia.yvisbig.bizcom.voHeader.ResponseHeaderVO;
import lombok.*;

/**
 * 사용자 로그인 응답 VO
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginResVO extends ResponseHeaderVO {

    private String accessToken;

    private String refreshToken;

    private String tokenType;

    private Long expiresIn;

    private String userId;

    private String userNm;

    private String mediaId;
}