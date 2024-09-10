package com.yjmedia.yvisbig.bizcom.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import java.util.stream.Stream;

public enum EduMovSt {
  N("N"), // 게시종료
  S("S"), // 게시중
  P("P"); // 대기중

  private final String code;

  EduMovSt(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }

  @JsonCreator(mode = Mode.DELEGATING)
  public static EduMovSt findByCode(String code) {
    return Stream.of(EduMovSt.values())
        .filter(c -> c.code.equals(code))
        .findFirst()
        .orElse(null);
  }
}
