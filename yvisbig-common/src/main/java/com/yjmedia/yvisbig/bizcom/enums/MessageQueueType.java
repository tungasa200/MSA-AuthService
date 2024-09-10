package com.yjmedia.yvisbig.bizcom.enums;

public enum MessageQueueType {
    DDVL("DDVL"),  //기사용 기사 리스트
    DDVR("DDVR"), //기사용 예약 리스트
    DELL("DELL"),  //호출취소
    DELR("DELR"), //예약 호출 취소
    CONL("CODL"),  //콜확정
    CONR("CODR");  //예약 확정

    private String keyName;

    private MessageQueueType(String keyName){
        this.keyName = keyName;
    }

    public String getKeyName(){
        return keyName;
    }
}
