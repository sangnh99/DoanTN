package com.example.demodatn.repository;


import com.example.demodatn.entity.UserAppEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAppRepository extends JpaRepository<UserAppEntity, Long> {
    Optional<UserAppEntity> findByUsername(String username);
    UserAppEntity findByEmail(String email);
    UserAppEntity findByResetPasswordToken(String token);

    @Modifying
    @Query(value = "update UserAppEntity u set u.isDeleted = 1 where u.id = ?1")
    void deleteUserApp(Long id);

    @Query(value = "select u from UserAppEntity u where u.email = ?1 and u.isDeleted = 1")
    UserAppEntity findUserByEmailToVerify(String email);
}
