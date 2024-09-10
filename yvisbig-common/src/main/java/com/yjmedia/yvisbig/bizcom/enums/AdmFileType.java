package com.yjmedia.yvisbig.bizcom.enums;

public enum AdmFileType {
  TXEX("TXEX"), // 종합소득세 엑셀
  EDTH("EDTH"), // 동영상 썸네일 파일
  PMTH("PMTH") // 프로모션 썸네일 파일
  ;

  private final String code;

  AdmFileType(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }
}
