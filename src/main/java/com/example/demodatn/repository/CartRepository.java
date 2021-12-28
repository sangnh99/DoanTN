package com.example.demodatn.repository;

import com.example.demodatn.entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<CartEntity, Long> {

    List<CartEntity> findAllByUserAppId(Long id);

    @Query(value = "select c from CartEntity c where c.userAppId = ?1 order by c.id")
    List<CartEntity> getAllByUserAppIdWithOrder(Long id);

    CartEntity findByUserAppIdAndFoodId(Long userId, Long foodId);

    @Modifying
    @Query(value = "update CartEntity set isDeleted = 1 where userAppId = ?1")
    void deleteCartByUserAppId(Long userAppId);
}
