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

    @Query(value = "select f from FoodEntity f where lower(f.name) like %?1% and f.foodTypeId <> 7")
    Page<FoodEntity> findFoodBySearchValue(String valueSearch, Pageable pageable);

    @Query(value = "select f from FoodEntity f where f.discountPercent is not null order by f.discountPercent desc ")
    List<FoodEntity> getListFoodOnSale();

    @Query(value = "select f from FoodEntity f where f.storeId = ?1 and lower(f.name) like %?2%")
    Page<FoodEntity> getAllFoodOfStoreAdmin(Long storeId, String searchValue, Pageable pageable);


    @Query(value = "select f from FoodEntity f inner join FavouriteEntity fav on fav.itemId = f.id inner join UserAppEntity u on u.id = fav.userAppId where u.id = ?1 and f.storeId = ?2 and fav.type = ?3 and fav.isDeleted = 0")
    List<FoodEntity> getListLikeFoodComment(Long userAppId, Long storeId, Integer type);
}
