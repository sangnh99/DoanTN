package com.example.demodatn.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class FoodDomain {
    private String id;
    private String name;
    private String foodTypeId;
    private String storeId;
    private String storeName;
    private String summaryRating;
    private String avatar;
    private String price;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFoodTypeId() {
        return foodTypeId;
    }

    public void setFoodTypeId(String foodTypeId) {
        this.foodTypeId = foodTypeId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getSummaryRating() {
        return summaryRating;
    }

    public void setSummaryRating(String summaryRating) {
        this.summaryRating = summaryRating;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
