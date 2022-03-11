package com.example.demodatn.repository;

import com.example.demodatn.entity.FavouriteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavouriteRepository extends JpaRepository<FavouriteEntity, Long> {
    FavouriteEntity findByUserAppIdAndItemIdAndType(Long userAppId, Long itemId, Integer type);

    List<FavouriteEntity> findAllByUserAppId(Long userAppId);

    @Modifying
    @Query(value = "update FavouriteEntity set isDeleted = 1 where itemId = ?1 and type = 2")
    void deleteAllFavouriteByFoodId(Long foodId);

    List<FavouriteEntity> findByItemIdAndType(Long itemId, Integer type);

    @Modifying
    @Query(value = "update FavouriteEntity set isDeleted = 1 where itemId = ?1 and type = 1")
    void deleteAllFavouriteByStoreId(Long storeId);
}
