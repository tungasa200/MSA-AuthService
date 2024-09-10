package com.yjmedia.yvisbig.bizcom.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;

import java.util.stream.Stream;

public enum DmPicType {
  DRIV("DRIV"), // 운전면허사진
  SELF("SELF"), // 본인인증사진
  PRFI("PRFI"); // 프로필 사진

  private final String code;

  DmPicType(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }

}
