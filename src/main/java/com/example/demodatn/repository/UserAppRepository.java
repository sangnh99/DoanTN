package com.example.demodatn.repository;


import com.example.demodatn.entity.UserAppEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    UserAppEntity findByUsernameAndIsLocked(String username, Integer isLocked);

    @Query(value = "select u from UserAppEntity u where u.isDeleted <> 1 and u.isLocked <> 1")
    List<UserAppEntity> getListUserActive();

    @Query(value = "select u from UserAppEntity  u where lower(u.username) like %?1% and u.id <> 18")
    Page<UserAppEntity> getListUserBySearchValue(String valueSearch, Pageable pageable);

    @Query(value = "select u from UserAppEntity u order by u.createdDate asc ")
    List<UserAppEntity> getListUserAppByCreateDate();

    UserAppEntity findByEmailAndIsLocked(String email, Integer isLocked);

    @Query(value = "select u from UserAppEntity u inner join UserRoleEntity ur on ur.userId = u.id inner join RoleEntity r on r.id = ur.roleId where lower(u.fullName) like %?1% and u.id <> 18 and r.code = ?2")
    Page<UserAppEntity> getListShipperBySearchValue(String valueSearch, String role,  Pageable pageable);
}
