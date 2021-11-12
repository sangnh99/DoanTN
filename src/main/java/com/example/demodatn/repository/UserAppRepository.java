package com.example.demodatn.repository;


import com.example.demodatn.entity.UserAppEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAppRepository extends JpaRepository<UserAppEntity, Long> {
    UserAppEntity findByUsername(String username);
}
