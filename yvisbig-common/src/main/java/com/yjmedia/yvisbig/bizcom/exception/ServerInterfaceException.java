package com.yjmedia.yvisbig.bizcom.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ServerInterfaceException extends RuntimeException{

    private final HttpStatus httpStatus;

    private final int errCode;

    private final String message;
    private final String detailMessage;
    private final String messageKey;

    public ServerInterfaceException(ErrorType errorType) {
        this.httpStatus = HttpStatus.NOT_IMPLEMENTED;
        this.errCode = errorType.getBizErrorCode();
        this.message = errorType.getMessage();
        this.detailMessage = errorType.getDetailMessage();
        this.messageKey = errorType.getMessageKey();
    }


    public ServerInterfaceException(ErrorType errorType, String messageError) {
        this.httpStatus = HttpStatus.NOT_IMPLEMENTED;
        this.errCode = errorType.getBizErrorCode();
        this.message = messageError;
        this.detailMessage = errorType.getDetailMessage();
        this.messageKey = errorType.getMessageKey();
    }

}
