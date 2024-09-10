package com.yjmedia.yvisbig.bizcom.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import java.util.stream.Stream;

public enum IncomReqSt {
  REQW("REQW"), // 발급요청
  DONE("DONE"); // 발급완료

  private final String code;

  IncomReqSt(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }

  @JsonCreator(mode = Mode.DELEGATING)
  public static IncomReqSt findByCode(String code) {
    return Stream.of(IncomReqSt.values())
        .filter(c -> c.code.equals(code))
        .findFirst()
        .orElse(null);
  }
}
