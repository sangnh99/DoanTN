package com.example.demodatn.repository;

import com.example.demodatn.entity.FavouriteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavouriteRepository extends JpaRepository<FavouriteEntity, Long> {
    FavouriteEntity findByUserAppIdAndItemIdAndType(Long userAppId, Long itemId, Integer type);

    List<FavouriteEntity> findAllByUserAppId(Long userAppId);
}
