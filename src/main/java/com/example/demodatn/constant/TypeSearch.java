package com.example.demodatn.constant;

public enum TypeSearch {
    STORE(1), FOOD(2);

    private Integer value;

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    TypeSearch(Integer value) {
        this.value = value;
    }
}
