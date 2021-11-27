package com.example.demodatn.constant;

import java.util.Arrays;
import java.util.List;

public enum ColumnSortFood {
    PRICE( "price", "PRICE"),
    SUMMARY_RATING("summaryRating", "SUMMARY_RATING");

    private String name;
    private String referenceName;

    ColumnSortFood(String name, String referenceName) {
        this.name = name;
        this.referenceName = referenceName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReferenceName() {
        return referenceName;
    }

    public void setReferenceName(String referenceName) {
        this.referenceName = referenceName;
    }

    public static List<String> getListColumnSortFood(){
        return Arrays.asList(ColumnSortFood.PRICE.getName(), ColumnSortFood.SUMMARY_RATING.getName());
    }
}
