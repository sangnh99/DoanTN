package com.example.demodatn.repository;

import com.example.demodatn.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
    List<TransactionEntity> findAllByUserAppIdOrderByCreatedDateDesc(Long userAppId);
}
