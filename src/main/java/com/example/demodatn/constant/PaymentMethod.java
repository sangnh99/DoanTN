package com.example.demodatn.constant;

public enum PaymentMethod {
    DIRECT("Thanh toán khi nhận hàng", 1), PAYPAL("paypal", 2);

    private String name;
    private Integer number;

    PaymentMethod(String name, Integer number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }
}
