package com.yjmedia.yvisbig.bizcom.enums;

public enum MbrState {
    I("I"),  //임시저장
    M("M"),//가입완료
    D("D"),//휴면계정
    W("W");  //탈퇴

    private String keyName;

    private MbrState(String keyName){
        this.keyName = keyName;
    }

    public String getKeyName(){
        return keyName;
    }
}
