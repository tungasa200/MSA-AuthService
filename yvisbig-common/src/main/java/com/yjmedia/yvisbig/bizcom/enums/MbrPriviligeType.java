package com.yjmedia.yvisbig.bizcom.enums;

public enum MbrPriviligeType {
    CMMB("CMMB"),  //고객사용자
    DMMB("DMMB"), // 대리기사 회원
    ADMN("ADMN");  // 관리자

    private String keyName;

    private MbrPriviligeType(String keyName){
        this.keyName = keyName;
    }

    public String getKeyName(){
        return keyName;
    }
}
