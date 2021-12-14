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
}
