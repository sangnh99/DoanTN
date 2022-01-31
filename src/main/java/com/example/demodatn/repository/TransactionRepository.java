package com.example.demodatn.repository;

import com.example.demodatn.entity.TransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
    List<TransactionEntity> findAllByUserAppIdOrderByCreatedDateDesc(Long userAppId);

    List<TransactionEntity> findAllByUserAppIdOrderByIdDesc(Long userAppId);

    @Query(value = "select t from TransactionEntity t inner join StoreEntity s on s.id = t.storeId inner join UserAppEntity u on u.id = t.userAppId where lower(s.name) like %?1% or lower(u.username) like %?1% ")
    Page<TransactionEntity> getAllTransactionOfUser(String valueSearch, Pageable pageable);

    @Query(value = "select t from TransactionEntity t order by t.createdDate asc ")
    List<TransactionEntity> findAllAndOrderByCreatedDate();
}
