package com.yjmedia.yvisbig.bizcom.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import java.util.stream.Stream;

public enum IncomReqUseTp {

  TAX("TAX"); // 신청용도 소득세신고

  private final String code;

  IncomReqUseTp(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }

  @JsonCreator(mode = Mode.DELEGATING)
  public static IncomReqUseTp findByCode(String code) {
    return Stream.of(IncomReqUseTp.values())
        .filter(c -> c.code.equals(code))
        .findFirst()
        .orElse(null);
  }
}
