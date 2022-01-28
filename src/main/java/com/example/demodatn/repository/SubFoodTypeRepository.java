package com.example.demodatn.repository;

import com.example.demodatn.entity.SubFoodTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubFoodTypeRepository extends JpaRepository<SubFoodTypeEntity, Long> {
    List<SubFoodTypeEntity> findAllByStoreId(Long storeId);

    SubFoodTypeEntity findByStoreIdAndName(Long storeId, String name);
}
