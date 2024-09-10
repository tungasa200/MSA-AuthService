package com.yjmedia.yvisbig.baseauth.voProtocol;

import com.yjmedia.yvisbig.bizcom.voHeader.ResponseHeaderVO;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class SvrGetTokenResVO extends ResponseHeaderVO {
    public  String  jwt;
}
