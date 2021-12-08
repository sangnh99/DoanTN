package com.example.demodatn.service;

import com.example.demodatn.constant.RoleConstant;
import com.example.demodatn.domain.AddToCartDomain;
import com.example.demodatn.domain.RegisterDomain;
import com.example.demodatn.domain.ValidateEmailDomain;
import com.example.demodatn.entity.CartEntity;
import com.example.demodatn.entity.FoodEntity;
import com.example.demodatn.entity.UserAppEntity;
import com.example.demodatn.entity.UserRoleEntity;
import com.example.demodatn.repository.CartRepository;
import com.example.demodatn.repository.FoodRepository;
import com.example.demodatn.repository.UserAppRepository;
import com.example.demodatn.repository.UserRoleRepository;
import com.example.demodatn.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;

@Service
public class UserAppServiceImpl implements UserAppService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserAppRepository userAppRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private CartRepository cartRepository;

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
    public void sendEmailVerify(String recipientEmail, String token) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("sangnh99@gmail.com", "Sang Nguyen");
        helper.setTo(recipientEmail);

        String subject = "Here's the link to verify your email";

        String content = "<p>Hello,</p>"
                + "<p>You have requested to verify your email.</p>"
                + "<p>This is the code to verify your email:</p>"
                + "<p>" + token + "</p>";


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

    @Transactional
    @Override
    public void registerUserApp(RegisterDomain domain, String token) throws Exception {
        if (!domain.getConfirmpassword().equals(domain.getPassword())){
            throw new Exception("Mat khau xac nhan khong dung!");
        }
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(domain.getPassword());
        UserAppEntity userApp1 = userAppRepository.findByEmail(domain.getEmail());
        UserAppEntity userApp2 = userAppRepository.findByUsername(domain.getUsername()).orElse(null);

        if (userApp1 != null || userApp2 != null){
            throw new Exception("Username hoac email da ton tai");
        }

        UserAppEntity userAppEntity = new UserAppEntity();
        userAppEntity.setUsername(domain.getUsername());
        userAppEntity.setEmail(domain.getEmail());
        userAppEntity.setPassword(encodedPassword);
        userAppEntity.setPhone(domain.getPhone());
        userAppEntity.setFullName(domain.getFullname());
        userAppEntity.setVerifyEmailToken(token);
        UserAppEntity user = userAppRepository.save(userAppEntity);
    }

    @Override
    public void validateAccountByEmail(ValidateEmailDomain domain) throws Exception {
        UserAppEntity userAppEntity = userAppRepository.findByEmail(domain.getEmail());
        if (userAppEntity == null){
            throw new Exception("Email khong ton tai");
        }
        if (!domain.getToken().equals(userAppEntity.getVerifyEmailToken())){
            userAppEntity.setIsDeleted(1);
            userAppRepository.save(userAppEntity);
            throw new Exception("Ma xac thuc khong dung");
        }

        userAppEntity.setVerifyEmailToken(null);
        userAppRepository.save(userAppEntity);

        UserRoleEntity userRoleEntity = new UserRoleEntity();
        userRoleEntity.setUserId(userAppEntity.getId());
        userRoleEntity.setRoleId(RoleConstant.ROLE_USER.getNumber());

        userRoleRepository.save(userRoleEntity);
    }

    @Transactional
    public void deleteAndAddToCart(AddToCartDomain domain){
        Long userAppId = StringUtils.convertStringToLongOrNull(domain.getUserAppId());
        Long foodId = StringUtils.convertObjectToLongOrNull(domain.getFoodId());
        FoodEntity foodEntity = foodRepository.getById(foodId);
        Integer number = StringUtils.convertStringToIntegerOrNull(domain.getAmount());

        cartRepository.deleteCartByUserAppId(userAppId);

        CartEntity cartEntity = new CartEntity();
        cartEntity.setUserAppId(userAppId);
        cartEntity.setFoodId(foodId);
        cartEntity.setAmount(number);
        cartEntity.setPrice(foodEntity.getPrice());

        cartRepository.save(cartEntity);
    }
}
