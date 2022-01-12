package com.example.demodatn.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class StoreDetailDomain {
    private String id;
    private String name;
    private String address;
    private String phone;
    private String avatar;
    private List<StoreDetailByFoodIdDomain> listSubFoodType;
    private List<CommentDomain> listComments;
    private Integer isFavourite;
    private Double distance;
    private Double summaryRating;
    private Double latitude;
    private Double longitude;
    private Integer numberOfRating;
    private List<FoodDomain> listRecommendFood;
    private List<FoodDomain> listMustTryFood;
    private String openTime;
    private String priceRange;

    public Integer getNumberOfRating() {
        return numberOfRating;
    }

    public void setNumberOfRating(Integer numberOfRating) {
        this.numberOfRating = numberOfRating;
    }

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public List<StoreDetailByFoodIdDomain> getListSubFoodType() {
        return listSubFoodType;
    }

    public void setListSubFoodType(List<StoreDetailByFoodIdDomain> listSubFoodType) {
        this.listSubFoodType = listSubFoodType;
    }

    public List<CommentDomain> getListComments() {
        return listComments;
    }

    public void setListComments(List<CommentDomain> listComments) {
        this.listComments = listComments;
    }

    public Integer getIsFavourite() {
        return isFavourite;
    }

    public void setIsFavourite(Integer isFavourite) {
        this.isFavourite = isFavourite;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Double getSummaryRating() {
        return summaryRating;
    }

    public void setSummaryRating(Double summaryRating) {
        this.summaryRating = summaryRating;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public List<FoodDomain> getListRecommendFood() {
        return listRecommendFood;
    }

    public void setListRecommendFood(List<FoodDomain> listRecommendFood) {
        this.listRecommendFood = listRecommendFood;
    }

    public List<FoodDomain> getListMustTryFood() {
        return listMustTryFood;
    }

    public void setListMustTryFood(List<FoodDomain> listMustTryFood) {
        this.listMustTryFood = listMustTryFood;
    }

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public String getPriceRange() {
        return priceRange;
    }

    public void setPriceRange(String priceRange) {
        this.priceRange = priceRange;
    }
}
