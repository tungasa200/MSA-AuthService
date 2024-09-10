package com.yjmedia.yvisbig.bizcom.exception;


import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@ControllerAdvice
@RestController
public class GlobalExceptionHandler {

  @Autowired
  HttpServletRequest request;
  @Autowired
  HttpServletResponse response;

  @Autowired
  MessageSource messageSource;

  public boolean isApiCall() {
    if (request.getRequestURI().endsWith(".do")) {
      return false;
    }
    return true;
  }

  //@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(ServerBizException.class)
  public ResponseEntity<ServerErrorResponse> BizExceptionMessageHandler(ServerBizException ge, HttpServletRequest request)
      throws IOException {

    if (!isApiCall()) {
      response.sendRedirect(request.getContextPath() + "/bizError.html");
      return null;
    } else if(ge.getErrCode() == ErrorType.JWT_TOKEN_REFRESH_TIMEOUT.getBizErrorCode() ||
            ge.getErrCode() == ErrorType.JWT_TOKEN_REFRESH_DIFF.getBizErrorCode() ||
        ge.getErrCode() == ErrorType.JWT_REGIST_USEREREDIS_ERROR.getBizErrorCode()){
      return ResponseEntity
              .status(HttpStatus.UNAUTHORIZED)
              .body(ServerErrorResponse.builder()
                      .bizErrCode(ge.getErrCode())
                      .message(ge.getMessage())
                      .detailMessage(ge.getDetailMessage())
                      .path(request.getRequestURI())
                      .messageKey(ge.getMessageKey()).build());
    }else {
      return ResponseEntity
          //.status(ge.getHttpStatus())
          .status(HttpStatus.OK)
              .body(ServerErrorResponse.builder()
              .bizErrCode(ge.getErrCode())
              .message(ge.getMessage())
              .detailMessage(ge.getDetailMessage())
              .path(request.getRequestURI())
              .messageKey(ge.getMessageKey()).build());
    }
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ServerErrorResponse> MethodArgumentNotValidExceptionHandler(
      MethodArgumentNotValidException exception, HttpServletRequest request) {

    return ResponseEntity
        //.status(HttpStatus.BAD_REQUEST)
        .status(HttpStatus.OK)
        .body(
            ServerErrorResponse.builder()
                .bizErrCode(ErrorType.BINDING_ERROR.getBizErrorCode())
                .message(exception.getBindingResult().getAllErrors().get(0).getDefaultMessage())
                .detailMessage(exception.getBindingResult().getAllErrors().toString())
                .path(request.getRequestURI())
                .build());
  }

  @ExceptionHandler(BindException.class)
  public ResponseEntity<ServerErrorResponse> BindExceptionHandler(
      BindException exception, HttpServletRequest request) {

    return ResponseEntity
        //.status(HttpStatus.BAD_REQUEST)
        .status(HttpStatus.OK)
        .body(
            ServerErrorResponse.builder()
                .bizErrCode(ErrorType.BINDING_ERROR.getBizErrorCode())
                .message(exception.getBindingResult().getAllErrors().get(0).getDefaultMessage())
                .detailMessage(exception.getBindingResult().getAllErrors().toString())
                .path(request.getRequestURI())
                .build());
  }

}
