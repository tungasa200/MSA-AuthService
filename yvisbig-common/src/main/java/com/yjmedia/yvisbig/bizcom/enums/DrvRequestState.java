package com.yjmedia.yvisbig.bizcom.enums;

public enum DrvRequestState {
    RES("RES"), // 예약
    RCO("RCO"), // 예약 확장
    RWT("RCO"), // 예약 , 출발지 도착
    RST("RST"), // 예약 , 운행 시작
    RCD("RCD"), // 예약 , 카드결제완료
    REN("REN"), // 예약 , 운행 종료
    RDL("RDL"), // 예약 취소
    CAL("CAL"), // 호출중
    CCO("CCO"), // 호출확정
    WAT("WAT"), // 출발지 도착  대기중
    STA("STA"), // 운행 시작
    DEL("DEL"), // 운행
    DCD("DCD"), // 운행 카드결제완료
    END("END"); // 운행 종료

    private final String code;

    DrvRequestState(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}
