package com.example.demodatn.entity;

import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "RATING_IMAGE")
@Where(clause = "is_deleted = 0")
public class RatingImageEntity extends BaseEntity {
    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "RATING_IMAGE_SEQ", sequenceName = "RATING_IMAGE_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "RATING_IMAGE_SEQ")
    private Long id;
    @Column(name = "RATING_ID")
    private Long ratingId;
    @Column(name = "IMAGE_URL")
    private String imageUrl;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRatingId() {
        return ratingId;
    }

    public void setRatingId(Long ratingId) {
        this.ratingId = ratingId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
