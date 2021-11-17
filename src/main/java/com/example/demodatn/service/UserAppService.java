package com.example.demodatn.service;

import com.example.demodatn.entity.UserAppEntity;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

@Service
public interface UserAppService {
    public void sendEmail(String recipientEmail, String link) throws Exception;
    public void updateResetPasswordToken(String token, String email) throws Exception;
    public void updatePassword(UserAppEntity userApp, String newPassword);
}
