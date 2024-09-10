package com.yjmedia.yvisbig.bizcom.exception;

import com.yjmedia.yvisbig.bizcom.constants.GlobalConstants;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ServerBizException extends RuntimeException{

    private final HttpStatus httpStatus;

    private final int errCode;

    private final String message;
    private final String detailMessage;
    private final String messageKey;

    public ServerBizException(ErrorType errorType) {
        this.httpStatus = HttpStatus.NOT_IMPLEMENTED;
        this.errCode = errorType.getBizErrorCode();
        this.message = errorType.getMessage();
        this.detailMessage = errorType.getDetailMessage();
        this.messageKey = errorType.getMessageKey();
    }


    public ServerBizException(ErrorType errorType, String messageError) {
        this.httpStatus = HttpStatus.NOT_IMPLEMENTED;
        this.errCode = errorType.getBizErrorCode();
        this.detailMessage = errorType.getDetailMessage();

        //개발 환경에서만 상세 메세지를 넣는다.
        if(GlobalConstants.YVISBIG_MSA_SERVER_ACTIVE_PROFILE.contains("local") ||
                GlobalConstants.YVISBIG_MSA_SERVER_ACTIVE_PROFILE.contains("dev")){
            this.message = messageError;
        }else {
            this.message = errorType.getMessage();
        }

        this.messageKey = errorType.getMessageKey();
    }

    public ServerBizException(ErrorType errorType, String messageError, String detailMessage) {
        this.httpStatus = HttpStatus.NOT_IMPLEMENTED;
        this.errCode = errorType.getBizErrorCode();
        this.detailMessage = detailMessage;
        this.messageKey = errorType.getMessageKey();

        //개발 환경에서만 상세 메세지를 넣는다.
        if(GlobalConstants.YVISBIG_MSA_SERVER_ACTIVE_PROFILE.contains("local") ||
                GlobalConstants.YVISBIG_MSA_SERVER_ACTIVE_PROFILE.contains("dev")){
            this.message = messageError;
        }else {
            this.message = errorType.getMessage();
        }
    }

}
