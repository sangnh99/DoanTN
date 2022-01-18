package com.example.demodatn.constant;

public enum MetadataType {
    DATA_RECOMMEND_FILE(1);

    private Integer value;

    MetadataType(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
