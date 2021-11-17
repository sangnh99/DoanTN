package com.example.demodatn.controller;

import com.example.demodatn.domain.EmailDomain;
import com.example.demodatn.domain.ResponseDataAPI;
import com.example.demodatn.entity.UserAppEntity;
import net.bytebuddy.asm.Advice;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@CrossOrigin("*")
@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("/profile")
    public ResponseEntity<?> getProfileUser(){
        UserAppEntity userAppEntity = new UserAppEntity();
        userAppEntity.setPassword("aaa");
        userAppEntity.setUsername("bbbb");
        System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaa");
        return  ResponseEntity.ok(userAppEntity);
    }
}
