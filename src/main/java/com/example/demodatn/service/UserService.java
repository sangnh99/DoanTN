package com.example.demodatn.service;
import com.example.demodatn.entity.UserAppEntity;
import com.example.demodatn.entity.UserPrinciple;
import com.example.demodatn.repository.UserAppRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class UserService implements IUserService {
    @Autowired
    private UserAppRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Iterable<UserAppEntity> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<UserAppEntity> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public UserAppEntity save(UserAppEntity user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public void remove(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserAppEntity> userOptional = userRepository.findByUsername(username);
        if (!userOptional.isPresent()) {
            throw new UsernameNotFoundException(username);
        }
        return UserPrinciple.build(userOptional.get());
    }

    @Override
    public Optional<UserAppEntity> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
