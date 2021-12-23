package com.example.demodatn.repository;

import com.example.demodatn.entity.DeliveryAddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddressEntity, Long> {
    List<DeliveryAddressEntity> findAllByUserAppId(Long userAppId);
    DeliveryAddressEntity findByIdAndUserAppId(Long id, Long userAppId);
}
