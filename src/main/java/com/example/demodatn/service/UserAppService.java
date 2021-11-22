package com.example.demodatn.service;

import com.example.demodatn.domain.RegisterDomain;
import com.example.demodatn.domain.ValidateEmailDomain;
import com.example.demodatn.entity.UserAppEntity;
import org.springframework.stereotype.Service;

@Service
public interface UserAppService {
    public void sendEmail(String recipientEmail, String link) throws Exception;
    public void sendEmailVerify(String recipientEmail, String token) throws Exception;
    public void updateResetPasswordToken(String token, String email) throws Exception;
    public void updatePassword(UserAppEntity userApp, String newPassword);
    public void registerUserApp(RegisterDomain domain, String token) throws Exception;
    public void validateAccountByEmail(ValidateEmailDomain domain) throws Exception;
}
