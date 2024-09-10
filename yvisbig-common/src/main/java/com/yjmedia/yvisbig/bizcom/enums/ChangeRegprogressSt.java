package com.yjmedia.yvisbig.bizcom.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import java.util.stream.Stream;

public enum ChangeRegprogressSt {
  //  DM10("DM10"), //	기사이용약관동의
  DM20("DM20"),  // 기사본인확인
  DM30("DM30"),  // 운전면허증사진제출
  DM40("DM40"),  // 본인확인사진제출
  DM50("DM50"),  // 프로필사진제출
  DM60("DM60"),  // 보험 등록
  DM70("DM70"),  // 주소 등록
  DM80("DM80");  // 가인신청
//  DM90("DM90");  // 가입승인완료


  private final String code;

  ChangeRegprogressSt(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }

  @JsonCreator(mode = Mode.DELEGATING)
  public static ChangeRegprogressSt findByCode(String code) {
    return Stream.of(ChangeRegprogressSt.values())
        .filter(c -> c.code.equals(code))
        .findFirst()
        .orElse(null);
  }
}
