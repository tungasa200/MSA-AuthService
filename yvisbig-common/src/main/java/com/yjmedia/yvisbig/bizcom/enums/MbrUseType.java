package com.yjmedia.yvisbig.bizcom.enums;

public enum MbrUseType {
    CM("CM"),  //고객용
    DM("DM");  //기사용

    private String keyName;

    private MbrUseType(String keyName){
        this.keyName = keyName;
    }

    public String getKeyName(){
        return keyName;
    }
}
