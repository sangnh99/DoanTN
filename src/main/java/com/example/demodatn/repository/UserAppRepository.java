package com.example.demodatn.repository;


import com.example.demodatn.entity.UserAppEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAppRepository extends JpaRepository<UserAppEntity, Long> {
    Optional<UserAppEntity> findByUsername(String username);
}
