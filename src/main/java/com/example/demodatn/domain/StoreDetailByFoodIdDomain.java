package com.example.demodatn.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class StoreDetailByFoodIdDomain {
    private String subFoodTypeId;
    private String subFoodTypeName;
    private String key;
    private List<FoodDomain> listFood;

    public String getSubFoodTypeId() {
        return subFoodTypeId;
    }

    public void setSubFoodTypeId(String subFoodTypeId) {
        this.subFoodTypeId = subFoodTypeId;
    }

    public String getSubFoodTypeName() {
        return subFoodTypeName;
    }

    public void setSubFoodTypeName(String subFoodTypeName) {
        this.subFoodTypeName = subFoodTypeName;
    }

    public List<FoodDomain> getListFood() {
        return listFood;
    }

    public void setListFood(List<FoodDomain> listFood) {
        this.listFood = listFood;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
