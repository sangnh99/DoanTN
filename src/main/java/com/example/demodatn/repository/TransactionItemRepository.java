package com.example.demodatn.repository;

import com.example.demodatn.entity.TransactionEntity;
import com.example.demodatn.entity.TransactionItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionItemRepository extends JpaRepository<TransactionItemEntity, Long> {
    List<TransactionItemEntity> findAllByTransactionId(Long transactionId);
}
