package com.example.demodatn.constant;

public enum FavouriteType {
    STORE(1), FOOD(2);

    private Integer value;

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    FavouriteType(Integer value) {
        this.value = value;
    }
}
