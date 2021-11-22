package com.example.demodatn.entity;

import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "CART")
@Where(clause = "is_deleted = 0")
public class CartEntity extends BaseEntity{
    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "CART_SEQ", sequenceName = "CART_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "CART_SEQ")
    private Long id;
    @Column(name = "USER_APP_ID")
    private Long userAppId;
    @Column(name = "FOOD_ID")
    private Long foodId;
    @Column(name = "AMOUNT")
    private Integer amount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserAppId() {
        return userAppId;
    }

    public void setUserAppId(Long userAppId) {
        this.userAppId = userAppId;
    }

    public Long getFoodId() {
        return foodId;
    }

    public void setFoodId(Long foodId) {
        this.foodId = foodId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}
