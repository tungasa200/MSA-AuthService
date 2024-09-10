package com.yjmedia.yvisbig.bizcom.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;

import java.util.stream.Stream;

public enum DmRegReqType {
  WAIT("WAIT"), // 대기중
  APPR("APPR"), // 승인
  REFU("REFU"), // 거절

  STOP("STOP"); // 사용정지


  private String keyName;

  private DmRegReqType(String keyName){
    this.keyName = keyName;
  }

  public String getKeyName(){
    return keyName;
  }
}
