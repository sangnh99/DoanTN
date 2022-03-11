package com.example.demodatn.repository;

import com.example.demodatn.entity.RatingEntity;
import com.example.demodatn.entity.RatingImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingImageRepository extends JpaRepository<RatingImageEntity, Long> {
    List<RatingImageEntity> findAllByRatingId(Long ratingId);

    @Modifying
    @Query(value = "update RatingImageEntity set isDeleted = 1 where ratingId = ?1")
    void deleteALlImageByRatingId(Long ratingId);
}
