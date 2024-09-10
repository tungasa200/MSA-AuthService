package com.yjmedia.yvisbig.bizcom.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ServerErrorResponse {
    private final int bizErrCode;
    private final String message;
    private final String detailMessage;
    private final String path;
    private final String messageKey;

}
