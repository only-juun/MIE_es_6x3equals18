package com.example.barcode_generator;

public class DeliveryContents {
    private String code;
    private String Info;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getInfo() {
        return Info;
    }

    public void setInfo(String info) {
        Info = info;
    }

    public DeliveryContents() {}

    public DeliveryContents(String code, String Info){
        this.Info = Info;
        this.code = code;
    }
}
