package com.example.demodatn.repository;

import com.example.demodatn.entity.StoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<StoreEntity, Long> {
    Optional<StoreEntity> findById(Long id);
}
