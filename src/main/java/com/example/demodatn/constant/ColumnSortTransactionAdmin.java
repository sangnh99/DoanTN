package com.example.demodatn.constant;

import java.util.Arrays;
import java.util.List;

public enum ColumnSortTransactionAdmin {
    TOTAL( "total", "TOTAL"),
    CREATED_DATE("createdDate", "CREATED_DATE");


    private String name;
    private String referenceName;

    ColumnSortTransactionAdmin(String name, String referenceName) {
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

    public static List<String> getListColumnSortTransactionAdmin(){
        return Arrays.asList(ColumnSortTransactionAdmin.TOTAL.getName(), ColumnSortTransactionAdmin.CREATED_DATE.getName());
    }
}
