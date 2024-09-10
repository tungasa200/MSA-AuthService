package com.yjmedia.yvisbig.bizcom.enums;

public enum DmDrvCancelType {
  LOWT("LOWT"); // 기사님 긴 대기시간으로 취소

  private final String code;

  DmDrvCancelType(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }

}
