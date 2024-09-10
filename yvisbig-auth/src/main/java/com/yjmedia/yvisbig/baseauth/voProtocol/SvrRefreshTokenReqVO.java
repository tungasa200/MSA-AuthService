package com.yjmedia.yvisbig.baseauth.voProtocol;

import com.yjmedia.yvisbig.bizcom.voHeader.RequestHeaderVO;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class SvrRefreshTokenReqVO extends RequestHeaderVO {
    String  mediaId;
    String  userId;
    String  userNm;
    String  mediaKey;
    String  callDate;
    String  hmacHash;
}
