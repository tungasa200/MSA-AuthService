package com.yjmedia.yvisbig.bizcom.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import java.util.stream.Stream;

public enum DmCmmuaddTp {
  CMTS("CMTS"), //	댓글
  LIKE("LIKE"), //	좋아요
  REPO("REPO");  //	신고

  private final String code;

  DmCmmuaddTp(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }

  @JsonCreator(mode = Mode.DELEGATING)
  public static DmCmmuaddTp findByCode(String code) {
    return Stream.of(DmCmmuaddTp.values())
        .filter(c -> c.code.equals(code))
        .findFirst()
        .orElse(null);
  }
}
