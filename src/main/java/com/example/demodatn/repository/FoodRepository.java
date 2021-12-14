package com.example.demodatn.repository;

import com.example.demodatn.entity.FoodEntity;
import com.example.demodatn.entity.StoreEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodRepository extends JpaRepository<FoodEntity, Long> {

    @Query(value = "select f from FoodEntity f inner join StoreEntity s on s.id = f.storeId" +
            " where f.foodTypeId = ?1 and (lower(f.name) like %?2% or lower(s.name) like %?2%)")
    Page<FoodEntity> getListFoodByFoodType(Long foodType, String searchValue, Pageable pageable);

    List<FoodEntity> findAllByStoreId(Long storeId);

    @Query(value = "select f from FoodEntity f where lower(f.name) like %?1% ")
    Page<FoodEntity> findFoodBySearchValue(String valueSearch, Pageable pageable);
}
