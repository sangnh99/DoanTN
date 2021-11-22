package com.example.demodatn.entity;

import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "RATING")
@Where(clause = "is_deleted = 0")
public class RatingEntity extends BaseEntity{
    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "RATING_SEQ", sequenceName = "RATING_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "RATING_SEQ")
    private Long id;
    @Column(name = "USER_APP_ID")
    private Long userAppId;
    @Column(name = "FOOD_ID")
    private Long foodId;
    @Column(name = "RATING")
    private Double rating;
    @Column(name = "COMMENT")
    private String comment;

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

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
