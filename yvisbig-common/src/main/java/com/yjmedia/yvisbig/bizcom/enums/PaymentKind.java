package com.yjmedia.yvisbig.bizcom.enums;

public enum PaymentKind {
    CARD("CARD"),  //카드결제
    CASH("CASH");  //현금

    private String keyName;

    private PaymentKind(String keyName){
        this.keyName = keyName;
    }

    public String getKeyName(){
        return keyName;
    }
}
