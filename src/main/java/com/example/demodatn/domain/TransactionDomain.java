package com.example.demodatn.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TransactionDomain {
    private String id;
    private String userAppId;
    private String userAppName;
    private String comment;
    private Long total;
    private Double distance;
    private String paymentMethod;
    private String createDate;
    private String storeId;
    private String storeName;
    private String address;
    private String storeAvatar;
    private List<TransactionItemDomain> listItem;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public List<TransactionItemDomain> getListItem() {
        return listItem;
    }

    public void setListItem(List<TransactionItemDomain> listItem) {
        this.listItem = listItem;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStoreAvatar() {
        return storeAvatar;
    }

    public void setStoreAvatar(String storeAvatar) {
        this.storeAvatar = storeAvatar;
    }

    public String getUserAppId() {
        return userAppId;
    }

    public void setUserAppId(String userAppId) {
        this.userAppId = userAppId;
    }

    public String getUserAppName() {
        return userAppName;
    }

    public void setUserAppName(String userAppName) {
        this.userAppName = userAppName;
    }
}
