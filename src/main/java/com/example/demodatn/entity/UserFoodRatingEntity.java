package com.example.demodatn.entity;

import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "USER_FOOD_RATING")
@Where(clause = "is_deleted = 0")
public class UserFoodRatingEntity extends BaseEntity{
    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "USER_FOOD_RATING_SEQ", sequenceName = "USER_FOOD_RATING_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "USER_FOOD_RATING_SEQ")
    private Long id;
    @Column(name = "USER_APP_ID")
    private Long userAppId;
    @Column(name = "FOOD_ID")
    private Long foodId;
    @Column(name = "RATING_ID")
    private Double ratingId;
}
