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
    @Column(name = "RATING")
    private Long rating;
    @Column(name = "COMMENT")
    private String comment;
    @Column(name = "LIKE_NUMBER")
    private Long likeNumber;
    @Column(name = "DISLIKE_NUMBER")
    private Long dislikeNumber;
    @Column(name = "FOOD_ID")
    private Long foodId;

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

    public Long getRating() {
        return rating;
    }

    public void setRating(Long rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getLikeNumber() {
        return likeNumber;
    }

    public void setLikeNumber(Long likeNumber) {
        this.likeNumber = likeNumber;
    }

    public Long getDislikeNumber() {
        return dislikeNumber;
    }

    public void setDislikeNumber(Long dislikeNumber) {
        this.dislikeNumber = dislikeNumber;
    }

    public Long getFoodId() {
        return foodId;
    }

    public void setFoodId(Long foodId) {
        this.foodId = foodId;
    }
}
