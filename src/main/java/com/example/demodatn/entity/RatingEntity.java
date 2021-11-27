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
    @Column(name = "RATING")
    private Double rating;
    @Column(name = "COMMENT")
    private String comment;
    @Column(name = "LIKE_NUMBER")
    private Long likeNumber;
}
