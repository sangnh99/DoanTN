package com.example.demodatn.repository;

import com.example.demodatn.entity.RatingEntity;
import com.example.demodatn.entity.RatingImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingImageRepository extends JpaRepository<RatingImageEntity, Long> {
    List<RatingImageEntity> findByRatingId(Long ratingId);
}
