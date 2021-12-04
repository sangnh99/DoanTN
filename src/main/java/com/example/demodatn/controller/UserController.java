package com.example.demodatn.controller;

import com.example.demodatn.domain.*;
import com.example.demodatn.entity.UserAppEntity;
import com.example.demodatn.exception.CustomException;
import com.example.demodatn.repository.UserAppRepository;
import com.example.demodatn.util.StringUtils;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@CrossOrigin("*")
@RestController
@RequestMapping("/user")
public class UserController {

//    @GetMapping("/profile")
//    public ResponseEntity<?> getProfileUser(){
//        UserAppEntity userAppEntity = new UserAppEntity();
//        userAppEntity.setPassword("aaa");
//        userAppEntity.setUsername("bbbb");
//        System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaa");
//        return  ResponseEntity.ok(userAppEntity);
//    }

    @Autowired
    private UserAppRepository userAppRepository;

    @GetMapping("/info")
    public ResponseEntity<ResponseDataAPI> geUserInfo(@RequestParam("id") String idStr){
        UserAppEntity userApp = userAppRepository.getById(StringUtils.convertStringToLongOrNull(idStr));
        if (userApp == null) {
            throw new CustomException("Id cua user bi sai", "wrong user id", HttpStatus.BAD_REQUEST);
        }

        UserInfoDomain domain = new UserInfoDomain();
        domain.setId(StringUtils.convertObjectToString(userApp.getId()));
        domain.setAddress(userApp.getAddress());
        domain.setPhone(userApp.getPhone());
        domain.setUsername(userApp.getUsername());
        domain.setEmail(userApp.getEmail());
        domain.setAvatar(userApp.getAvatar());
        domain.setFullName((userApp.getFullName()));

        return ResponseEntity.ok(ResponseDataAPI.builder().data(domain).build());
    }

    @PostMapping("/info")
    public ResponseEntity<ResponseDataAPI> updateUserInfo(@RequestBody UpdateUserInfoDomain domain){
        UserAppEntity userApp = userAppRepository.getById(StringUtils.convertStringToLongOrNull(domain.getId()));
        if (userApp == null) {
            throw new CustomException("Id cua user bi sai", "wrong user id", HttpStatus.BAD_REQUEST);
        }

        userApp.setFullName(domain.getFullName());
        userApp.setPhone(domain.getPhone());
        userApp.setAddress(domain.getAddress());

        userAppRepository.save(userApp);

        return ResponseEntity.ok(ResponseDataAPI.builder().data(domain).build());
    }

    @PostMapping("/password")
    public ResponseEntity<ResponseDataAPI> updatePassword(@RequestBody UpdatePasswordDomain domain){
        UserAppEntity userApp = userAppRepository.getById(StringUtils.convertStringToLongOrNull(domain.getId()));
        if (userApp == null) {
            throw new CustomException("Id cua user bi sai", "wrong user id", HttpStatus.BAD_REQUEST);
        }
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        userApp.setPassword(bCryptPasswordEncoder.encode(domain.getNewPassword()));
        userAppRepository.save(userApp);
        return ResponseEntity.ok(ResponseDataAPI.builder().data(domain).build());
    }
}
