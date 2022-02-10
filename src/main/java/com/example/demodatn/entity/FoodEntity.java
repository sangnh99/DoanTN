package com.example.demodatn.entity;

import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "FOOD")
@Where(clause = "is_deleted = 0")
public class FoodEntity extends BaseEntity{
    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "FOOD_SEQ", sequenceName = "FOOD_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "FOOD_SEQ")
    private Long id;
    @Column(name = "NAME")
    private String name;
    @Column(name = "FOOD_TYPE_ID")
    private Long foodTypeId;
    @Column(name = "STORE_ID")
    private Long storeId;
    @Column(name = "PRICE")
    private Long price;
    @Column(name = "SUMMARY_RATING")
    private Double summaryRating;
    @Column(name = "AVATAR")
    private String avatar;
    @Column(name = "SUB_FOOD_TYPE_ID")
    private Long subFoodTypeId;
    @Column(name = "DISCOUNT_PERCENT")
    private Integer discountPercent;
    @Column(name = "ORIGINAL_PRICE")
    private Long originalPrice;
    @Column(name = "TOTAL_BUY")
    private Integer totalBuy;
    @Column(name = "IS_BEST_SELLER")
    private Integer isBestSeller;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getFoodTypeId() {
        return foodTypeId;
    }

    public void setFoodTypeId(Long foodTypeId) {
        this.foodTypeId = foodTypeId;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public Double getSummaryRating() {
        return summaryRating;
    }

    public void setSummaryRating(Double summaryRating) {
        this.summaryRating = summaryRating;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Long getSubFoodTypeId() {
        return subFoodTypeId;
    }

    public void setSubFoodTypeId(Long subFoodTypeId) {
        this.subFoodTypeId = subFoodTypeId;
    }

    public Integer getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(Integer discountPercent) {
        this.discountPercent = discountPercent;
    }

    public Long getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(Long originalPrice) {
        this.originalPrice = originalPrice;
    }

    public Integer getTotalBuy() {
        return totalBuy;
    }

    public void setTotalBuy(Integer totalBuy) {
        this.totalBuy = totalBuy;
    }

    public Integer getIsBestSeller() {
        return isBestSeller;
    }

    public void setIsBestSeller(Integer isBestSeller) {
        this.isBestSeller = isBestSeller;
    }
}
