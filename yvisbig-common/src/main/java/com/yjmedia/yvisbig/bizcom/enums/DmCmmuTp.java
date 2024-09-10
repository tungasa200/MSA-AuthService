package com.yjmedia.yvisbig.bizcom.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import java.util.stream.Stream;

public enum DmCmmuTp {
  G("G"), // 일반
  R("R"), // 신고
  Q("Q"), // 질문
  T("T"); // 건의

  private final String code;

  DmCmmuTp(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }

  @JsonCreator(mode = Mode.DELEGATING)
  public static DmCmmuTp findByCode(String code) {
    return Stream.of(DmCmmuTp.values())
        .filter(c -> c.code.equals(code))
        .findFirst()
        .orElse(null);
  }
}
