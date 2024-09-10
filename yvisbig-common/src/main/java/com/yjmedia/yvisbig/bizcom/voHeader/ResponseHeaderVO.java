package com.yjmedia.yvisbig.bizcom.voHeader;

import com.yjmedia.yvisbig.bizcom.constants.GlobalConstants;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseHeaderVO {
    private String serverVersion = GlobalConstants.YVISBIG_MSA_SERVER_VERSION;         //
    private String serverId = GlobalConstants.YVISBIG_MSA_SERVER_ID;         //
}
