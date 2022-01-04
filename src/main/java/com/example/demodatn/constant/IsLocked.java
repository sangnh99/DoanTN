package com.example.demodatn.constant;

public enum IsLocked {
    TRUE(1), FALSE(0);

    private Integer value;

    IsLocked(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
