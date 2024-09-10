package com.yjmedia.yvisbig.bizcom.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import java.util.stream.Stream;

public enum TrmSvrSt {
  N("N"), // 게시종료
  S("S"), // 게시중
  P("P"); // 대기중


  private final String code;

  TrmSvrSt(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }

  @JsonCreator(mode = Mode.DELEGATING)
  public static TrmSvrSt findByCode(String code) {
    return Stream.of(TrmSvrSt.values())
        .filter(c -> c.code.equals(code))
        .findFirst()
        .orElse(null);
  }
}
