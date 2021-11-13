//package com.example.demodatn.service;
//
//import com.example.demodatn.entity.RoleEntity;
//import com.example.demodatn.entity.UserAppEntity;
//import com.example.demodatn.entity.UserRoleEntity;
//import com.example.demodatn.repository.RoleRepository;
//import com.example.demodatn.repository.UserAppRepository;
//import com.example.demodatn.repository.UserRoleRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//public class UserDetailsServiceImpl implements UserDetailsService {
//
//    @Autowired
//    private UserAppRepository userAppRepository;
//
//    @Autowired
//    private UserRoleRepository userRoleRepository;
//
//    @Autowired
//    private RoleRepository roleRepository;
//
//    @Override
//    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
//        UserAppEntity appUser = userAppRepository.findByUsername(userName);
//
//        if (appUser == null) {
//            System.out.println("User not found! " + userName);
//            throw new UsernameNotFoundException("User " + userName + " was not found in the database");
//        }
//
//        System.out.println("Found User: " + appUser);
//
//        // [ROLE_USER, ROLE_ADMIN,..]
//        List<UserRoleEntity> userRoleEntitys = userRoleRepository.findByUserId(appUser.getId());
//        List<String> roleNames = new ArrayList<>();
//        for (UserRoleEntity entity : userRoleEntitys){
//            RoleEntity roleEntity = entity.getRole();
//            roleNames.add(roleEntity.getCode());
//        }
//
//        List<GrantedAuthority> grantList = new ArrayList<GrantedAuthority>();
//        if (roleNames != null) {
//            for (String role : roleNames) {
//                // ROLE_USER, ROLE_ADMIN,..
//                GrantedAuthority authority = new SimpleGrantedAuthority(role);
//                grantList.add(authority);
//            }
//        }
//
//        UserDetails userDetails = (UserDetails) new User(appUser.getUsername(), //
//                appUser.getPassword(), grantList);
//
//        return userDetails;
//    }
//
//}
