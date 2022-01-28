package com.example.demodatn.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateNewStoreDomain {
    private String name;
    private String phone;
    private String opentime;
    private String pricerange;
    private List<String> listSubFood;
    private String avatar;
    private String lat;
    private String lng;
    private String addressSave;
    private List<CreateNewFoodDomain> listNewFood;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOpentime() {
        return opentime;
    }

    public void setOpentime(String opentime) {
        this.opentime = opentime;
    }

    public String getPricerange() {
        return pricerange;
    }

    public void setPricerange(String pricerange) {
        this.pricerange = pricerange;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getAddressSave() {
        return addressSave;
    }

    public void setAddressSave(String addressSave) {
        this.addressSave = addressSave;
    }

    public List<CreateNewFoodDomain> getListNewFood() {
        return listNewFood;
    }

    public void setListNewFood(List<CreateNewFoodDomain> listNewFood) {
        this.listNewFood = listNewFood;
    }

    public List<String> getListSubFood() {
        return listSubFood;
    }

    public void setListSubFood(List<String> listSubFood) {
        this.listSubFood = listSubFood;
    }
}
