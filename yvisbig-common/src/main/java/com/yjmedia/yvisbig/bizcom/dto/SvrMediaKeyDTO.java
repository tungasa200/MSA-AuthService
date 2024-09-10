package com.yjmedia.yvisbig.bizcom.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SvrMediaKeyDTO {
    private String mediaId;
    private String mediaKey;
    private String mediaSecretKey;
    private String  createId;
    private LocalDateTime createDt;
    private String  updateId;
    private LocalDateTime updateDt;
}
