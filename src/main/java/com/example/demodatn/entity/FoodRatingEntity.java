package com.example.demodatn.entity;

import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "FOOD_RATING")
@Where(clause = "is_deleted = 0")
public class FoodRatingEntity extends BaseEntity{
    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "FOOD_RATING_SEQ", sequenceName = "FOOD_RATING_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "FOOD_RATING_SEQ")
    private Long id;
    @Column(name = "FOOD_ID")
    private Long foodId;
    @Column(name = "RATING_ID")
    private Long ratingId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFoodId() {
        return foodId;
    }

    public void setFoodId(Long foodId) {
        this.foodId = foodId;
    }

    public Long getRatingId() {
        return ratingId;
    }

    public void setRatingId(Long ratingId) {
        this.ratingId = ratingId;
    }
}
