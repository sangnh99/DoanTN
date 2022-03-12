package com.example.demodatn.repository;

import com.example.demodatn.entity.TransactionEntity;
import com.example.demodatn.entity.TransactionItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionItemRepository extends JpaRepository<TransactionItemEntity, Long> {
    List<TransactionItemEntity> findAllByTransactionId(Long transactionId);
    List<TransactionItemEntity> findAllByFoodId(Long foodId);

    @Modifying
    @Query(value = "update TransactionItemEntity set isDeleted = 1 where foodId = ?1")
    void deleteAllByFoodId(Long foodId);
}
