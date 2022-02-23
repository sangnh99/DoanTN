package com.example.demodatn.constant;


public enum ShipperBusy {

    NOT_BUSY(0, "Đang rảnh"),
    BUSY(1, "Đang bận");

    private Integer number;
    private String name;

    ShipperBusy(Integer number, String name) {
        this.number = number;
        this.name = name;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
