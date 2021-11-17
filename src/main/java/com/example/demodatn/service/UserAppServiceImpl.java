package com.example.demodatn.service;

import com.example.demodatn.entity.UserAppEntity;
import com.example.demodatn.repository.UserAppRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@Service
public class UserAppServiceImpl implements UserAppService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserAppRepository userAppRepository;

    @Override
    public void sendEmail(String recipientEmail, String link) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("sangnh99@gmail.com", "Sang Nguyen");
        helper.setTo(recipientEmail);

        String subject = "Here's the link to reset your password";

        String content = "<p>Hello,</p>"
                + "<p>You have requested to reset your password.</p>"
                + "<p>Click the link below to change your password:</p>"
                + "<p><a href=\"" + link + "\">Change my password</a></p>"
                + "<br>"
                + "<p>Ignore this email if you do remember your password, "
                + "or you have not made the request.</p>";

        helper.setSubject(subject);

        helper.setText(content, true);

        mailSender.send(message);
    }

    @Override
    public void updateResetPasswordToken(String token, String email) throws Exception {
        UserAppEntity userApp = userAppRepository.findByEmail(email);
        if (userApp != null) {
            userApp.setResetPasswordToken(token);
            userAppRepository.save(userApp);
        } else {
            throw new Exception("Could not find any customer with the email " + email);
        }
    }

    @Override
    public void updatePassword(UserAppEntity userApp, String newPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(newPassword);
        userApp.setPassword(encodedPassword);

        userApp.setResetPasswordToken(null);
        userAppRepository.save(userApp);
    }
}
