package com.yjmedia.yvisbig.bizcom.enums;

public enum AdmPicType {
  EDTH("EDTH"), // 동영상 썸네일
  PMTH("PMTH"); // 프로모션 썸네일

  private final String code;

  AdmPicType(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }

}
