package com.example.demodatn.repository;

import com.example.demodatn.entity.FoodRatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodRatingRepository extends JpaRepository<FoodRatingEntity, Long> {

    @Query(value = "select fr.ratingId from FoodRatingEntity fr where fr.foodId = ?1 ")
    List<Long> getListRatingIdsFromFoodId(Long foodId);
}
