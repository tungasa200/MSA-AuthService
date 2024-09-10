package com.yjmedia.yvisbig.bizcom.enums;

public enum DmWorkType {
  ATTN("ATTN"), // 출근
  LEAV("LEAV"); // 퇴근

  private final String code;

  DmWorkType(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }

}
