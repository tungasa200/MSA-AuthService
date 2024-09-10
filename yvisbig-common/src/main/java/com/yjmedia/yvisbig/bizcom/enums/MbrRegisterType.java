package com.yjmedia.yvisbig.bizcom.enums;

public enum MbrRegisterType {
    HNPN("HNPN"),  //핸드폰 본인인증
    KAKO("KAKO"),  //핸카카오계정
    NAVR("NAVR"),  //네이버계정
    APPL("APPL");  //애플계정

    private String keyName;

    private MbrRegisterType(String keyName){
        this.keyName = keyName;
    }

    public String getKeyName(){
        return keyName;
    }
}
