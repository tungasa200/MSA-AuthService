package com.yjmedia.yvisbig.bizcom.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import java.util.stream.Stream;

public enum InqRtnSt {
  WAIT("WAIT"), // 답변 대기
  COMP("COMP"); // 답변 완료

  private final String code;

  InqRtnSt(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }

  @JsonCreator(mode = Mode.DELEGATING)
  public static InqRtnSt findByCode(String code) {
    return Stream.of(InqRtnSt.values())
        .filter(c -> c.code.equals(code))
        .findFirst()
        .orElse(null);
  }
}
