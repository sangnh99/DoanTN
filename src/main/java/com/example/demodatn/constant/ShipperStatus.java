package com.example.demodatn.constant;

public enum ShipperStatus {
    DANG_TIM_TAI_XE(1, "đang tìm tài xế"),
    DANG_CHO_LAY_HANG(2, "đang chờ lấy hàng"),
    DANG_GIAO(3, "đang giao hàng"),
    DA_GIAO_THANH_CONG(4, "đã giao thành công");

    private Integer number;
    private String name;

    ShipperStatus(Integer number, String name) {
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
