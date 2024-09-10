package com.yjmedia.yvisbig.bizcom.enums;

public enum CmDrvCancelType {
  OTHS("OTHS"), // 다른서비스
  DRVC("DRVC"), // 기사님 연락후 취소
  DRVO("DRVO"), // 기사님 일방적인 취소
  ETCD("ETCD"), // 기타
  COMP("COMP"); // 확정후, 고객,기사 협의취소


  private final String code;

  CmDrvCancelType(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }

}
