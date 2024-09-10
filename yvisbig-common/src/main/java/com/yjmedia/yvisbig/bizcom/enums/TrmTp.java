package com.yjmedia.yvisbig.bizcom.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import java.util.stream.Stream;

public enum TrmTp {
  SUSE("SUSE"), // 서비스이용약관
  LOCA("LOCA"), // 위치정보
  COMU("COMU"), // 커뮤니티
  PRIV("PRIV"), // 개인정보 동의
  P3AR("P3AR"), // 개인정보 3자 동의
  TRAD("TRAD"), // 전자금융거래이용약관
  MEMB("MEMB"), // 회원연회비-수수료약관
  WINS("WINS"), // 보험허위기재약관
  NCAR("NCAR"); // 운행불가차량

  private final String code;

  TrmTp(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }

  @JsonCreator(mode = Mode.DELEGATING)
  public static TrmTp findByCode(String code) {
    return Stream.of(TrmTp.values())
        .filter(c -> c.code.equals(code))
        .findFirst()
        .orElse(null);
  }
}
