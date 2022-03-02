package com.example.demodatn.controller;
import com.example.demodatn.constant.Error;
import com.example.demodatn.constant.IsLocked;
import com.example.demodatn.domain.*;
import com.example.demodatn.entity.UserAppEntity;
import com.example.demodatn.exception.CustomException;
import com.example.demodatn.repository.UserAppRepository;
import com.example.demodatn.service.*;
import com.example.demodatn.util.SiteUrlUtils;
import com.example.demodatn.util.StringUtils;
import net.bytebuddy.utility.RandomString;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@CrossOrigin("*")
@RestController
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private IUserService userService;

    @Autowired
    private UserAppService userAppService;

    @Autowired
    private UserAppRepository userAppRepository;

    @Autowired
    private AddressServiceImpl addressService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDomain user) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtService.generateTokenLogin(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//        UserAppEntity currentUser = userService.findByUsername(user.getUsername()).get();
        UserAppEntity currentUser = userAppRepository.findByUsernameAndIsLocked(user.getUsername(), IsLocked.FALSE.getValue());
        if (currentUser == null){
            throw new CustomException("User nay da bi khoa hoac khong hop le"
                    , "User nay da bi khoa hoac khong hop le", HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(new JwtResponse(jwt, currentUser.getId(), userDetails.getUsername(), currentUser.getFullName(), userDetails.getAuthorities()));
    }

    @PostMapping("/forgot_password")
    public ResponseEntity<ResponseDataAPI> processForgotPassword(HttpServletRequest request, @RequestBody EmailDomain domain) {
        String email = domain.getEmail();
//        String token = RandomStringUtils.randomAlphabetic(30);
        String token = RandomString.make(30);

        try {
            userAppService.updateResetPasswordToken(token, email);
            String resetPasswordLink = SiteUrlUtils.getSiteURL(request) + "/reset_password?token=" + token;
            userAppService.sendEmail(email, resetPasswordLink);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return ResponseEntity.ok(ResponseDataAPI.builder().build());
    }

    @PostMapping("/forgot_password/confirm-email")
    public ResponseEntity<ResponseDataAPI> congirmEmailForgotPassword(@RequestBody EmailDomain domain) throws Exception {
        String email = domain.getEmail();
//        String token = RandomStringUtils.randomAlphabetic(30);
        String token = RandomString.make(30);
        UserAppEntity userApp = null;
        try {
            userApp = userAppService.updateForgotPasswordToken(token, email);
            userAppService.sendEmailVerify(email, token);

        } catch (Exception ex) {
            throw new CustomException("Could not find any customer with the email " + email, "Could not find any customer with the email " + email, HttpStatus.BAD_REQUEST);
        }

        UserAppInfoResponseForgotPassDomain response = new UserAppInfoResponseForgotPassDomain();
        response.setToken(token);
        response.setUserAppId(userApp.getId());

        return ResponseEntity.ok(ResponseDataAPI.builder().data(response).build());
    }

    @PostMapping("/forgot_password/reset_password")
    public ResponseEntity<ResponseDataAPI> resetPasswordForgotPassword(@RequestBody ForgotPasswordDomain domain) throws Exception{
        String newPassword = domain.getNewPassword();

        UserAppEntity userAppEntity = userAppRepository.findById(StringUtils.convertStringToLongOrNull(domain.getUserAppId())).orElse(null);
        if (userAppEntity == null || userAppEntity.getIsLocked().equals(1) || !userAppEntity.getEmail().equals(domain.getEmail())){
            throw new CustomException("Đã có lỗi xảy ra, vui lòng thử lại sau !", "Đã có lỗi xảy ra, vui lòng thử lại sau !", HttpStatus.BAD_REQUEST);
        }
        userAppService.updatePassword(userAppEntity, newPassword);
        return ResponseEntity.ok(ResponseDataAPI.builder().build());
    }

    @PostMapping("/reset_password")
    public ResponseEntity<ResponseDataAPI> resetPassword(@RequestBody ResetPasswordDomain domain) throws Exception{
        String newPassword = domain.getNewPassword();
        String token = domain.getToken();
        UserAppEntity userAppEntity = userAppRepository.findByResetPasswordToken(token);
        if (userAppEntity == null){
            throw new Exception("There is no user with this token");
        }
        userAppService.updatePassword(userAppEntity, newPassword);
        return ResponseEntity.ok(ResponseDataAPI.builder().build());
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseDataAPI> register(@RequestBody RegisterDomain domain) throws Exception {
        String token = RandomString.make(30);
        System.out.println("Token :" + token);
        userAppService.registerUserApp(domain, token);
//        userAppService.sendEmailVerify(domain.getEmail(), token);
        return ResponseEntity.ok(ResponseDataAPI.builder().build());
    }

    @PostMapping("/register/handle-register")
    public ResponseEntity<ResponseDataAPI> validateRegister(@RequestBody ValidateEmailDomain domain) throws Exception {

        userAppService.validateAccountByEmail(domain);
        return ResponseEntity.ok(ResponseDataAPI.builder().build());
    }

    @PostMapping("/add-address-new-user")
    public ResponseEntity<ResponseDataAPI> addAddressNewUser(@RequestBody AddAddressNewUserDomain domain){
        addressService.addNewAddressNewUser(domain);
        return ResponseEntity.ok(ResponseDataAPI.builder().build());
    }
}
