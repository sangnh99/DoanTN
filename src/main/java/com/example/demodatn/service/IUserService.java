package com.example.demodatn.service;
import com.example.demodatn.entity.UserAppEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

public interface IUserService extends IGeneralService<UserAppEntity>, UserDetailsService {
    Optional<UserAppEntity> findByUsername(String username);
}