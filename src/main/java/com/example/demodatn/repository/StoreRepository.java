package com.example.demodatn.repository;

import com.example.demodatn.entity.StoreEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<StoreEntity, Long> {
    Optional<StoreEntity> findById(Long id);

    @Query(value = "select s from StoreEntity s where lower(s.name) like %?1% ")
    Page<StoreEntity> findStoreBySearchValue(String valueSearch, Pageable pageable);
}
