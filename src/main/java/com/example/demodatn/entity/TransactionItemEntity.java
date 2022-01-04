package com.example.demodatn.entity;

import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "TRANSACTION_ITEM")
@Where(clause = "is_deleted = 0")
public class TransactionItemEntity extends BaseEntity{
    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "TRANSACTION_ITEM_SEQ", sequenceName = "TRANSACTION_ITEM_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "TRANSACTION_ITEM_SEQ")
    private Long id;
    @Column(name = "TRANSACTION_ID")
    private Long transactionId;
    @Column(name = "FOOD_ID")
    private Long foodId;
    @Column(name = "AMOUNT")
    private Integer amount;
    @Column(name = "PRICE")
    private Long price;
    @Column(name = "DISCOUNT_PERCENT")
    private Integer discountPercent;
    @Column(name = "ORIGINAL_PRICE")
    private Long originalPrice;
    @Column(name = "NOTE")
    private String note;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
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

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
