package com.example.demodatn.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.annotations.ApiModelProperty;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class BasicRequest {
    @ApiModelProperty(value = "limit", example = "10")
    private Integer limit;
    @ApiModelProperty(value = "offset", example = "1")
    private Integer offset;
    @ApiModelProperty(value = "valueSearch", example = "test")
    private String valueSearch;
    @ApiModelProperty(value = "columnSort",notes = "TITLE|DESCRIPTION|STATUS|UPDATED_DATE", example = "TITLE")
    private String columnSort;
    @ApiModelProperty(value = "typeSort",notes = "DESC|ASC", example = "ASC")
    private String typeSort;

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public String getValueSearch() {
        return valueSearch;
    }

    public void setValueSearch(String valueSearch) {
        this.valueSearch = valueSearch;
    }

    public String getColumnSort() {
        return columnSort;
    }

    public void setColumnSort(String columnSort) {
        this.columnSort = columnSort;
    }

    public String getTypeSort() {
        return typeSort;
    }

    public void setTypeSort(String typeSort) {
        this.typeSort = typeSort;
    }
}
