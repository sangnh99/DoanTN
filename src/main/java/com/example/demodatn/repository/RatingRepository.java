package com.example.demodatn.repository;
import com.example.demodatn.entity.RatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<RatingEntity, Long> {
    List<RatingEntity> findAllByFoodId(Long foodId);

    @Query(value = "select r from RatingEntity r inner join FoodEntity f on f.id = r.foodId inner join " +
            "StoreEntity s on s.id = f.storeId where s.id = ?1")
    List<RatingEntity> getListRatingOfStore(Long storeId);
}
