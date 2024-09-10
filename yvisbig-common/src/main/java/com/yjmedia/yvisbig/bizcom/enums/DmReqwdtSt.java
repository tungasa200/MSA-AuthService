package com.yjmedia.yvisbig.bizcom.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import java.util.stream.Stream;

public enum DmReqwdtSt {
  REQW("REQW"), // 신청중
  DONE("DONE"); // 지불 완료

  private final String code;

  DmReqwdtSt(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }

  @JsonCreator(mode = Mode.DELEGATING)
  public static DmReqwdtSt findByCode(String code) {
    return Stream.of(DmReqwdtSt.values())
        .filter(c -> c.code.equals(code))
        .findFirst()
        .orElse(null);
  }
}
