package com.example.demodatn.repository;

import com.example.demodatn.entity.FoodTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodTypeRepository extends JpaRepository<FoodTypeEntity, Long> {
}
