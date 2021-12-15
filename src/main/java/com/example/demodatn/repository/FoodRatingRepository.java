package com.example.demodatn.repository;

import com.example.demodatn.entity.FoodEntity;
import com.example.demodatn.entity.FoodRatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodRatingRepository extends JpaRepository<FoodRatingEntity, Long> {

    @Query(value = "select fr.ratingId from FoodRatingEntity fr where fr.foodId = ?1 ")
    List<Long> getListRatingIdsFromFoodId(Long foodId);

    @Query(value = "select fr.ratingId from FoodRatingEntity fr inner join FoodEntity f " +
            "on f.id = fr.foodId inner join StoreEntity s on s.id = f.storeId where s.id = ?1 ")
    List<Long> getListRatingIdsFromStore(Long storeId);

    @Query(value = "select f from FoodRatingEntity fr inner join FoodEntity f on fr.foodId = f.id" +
            " where fr.ratingId = ?1")
    FoodEntity findFoodEntityByRatingId(Long ratingId);
}
