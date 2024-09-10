package com.yjmedia.yvisbig.bizcom.voHeader;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class RequestHeaderVO {
    private String clientVersion;
    private String clientId;  //device
}
