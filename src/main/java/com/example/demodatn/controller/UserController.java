package com.example.demodatn.controller;

import com.example.demodatn.domain.*;
import com.example.demodatn.entity.CartEntity;
import com.example.demodatn.entity.FoodEntity;
import com.example.demodatn.entity.StoreEntity;
import com.example.demodatn.entity.UserAppEntity;
import com.example.demodatn.exception.CustomException;
import com.example.demodatn.repository.CartRepository;
import com.example.demodatn.repository.FoodRepository;
import com.example.demodatn.repository.StoreRepository;
import com.example.demodatn.repository.UserAppRepository;
import com.example.demodatn.service.UserAppServiceImpl;
import com.example.demodatn.util.StringUtils;
//import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin("*")
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserAppRepository userAppRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private UserAppServiceImpl userAppService;

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

    @GetMapping("/cart")
    public ResponseEntity<ResponseDataAPI> geUserCurrentCart(@RequestParam("id") String idStr){
        return ResponseEntity.ok(ResponseDataAPI.builder().data(userAppService.getUserCurrentCart(idStr)).build());
    }

    @PostMapping("/cart/add")
    public ResponseEntity<ResponseDataAPI> addToCart(@RequestBody AddToCartDomain domain){
        Long userAppId = StringUtils.convertStringToLongOrNull(domain.getUserAppId());
        Long foodId = StringUtils.convertObjectToLongOrNull(domain.getFoodId());
        FoodEntity foodEntity = foodRepository.getById(foodId);
        CartEntity cartEntity = cartRepository.findByUserAppIdAndFoodId(userAppId, foodId);
        Integer number = StringUtils.convertStringToIntegerOrNull(domain.getAmount());
        if (cartEntity == null){
            CartEntity newCart = new CartEntity();
            newCart.setUserAppId(userAppId);
            newCart.setFoodId(foodId);
            newCart.setAmount(number);
            newCart.setPrice(foodEntity.getPrice());
            cartRepository.save(newCart);
        } else {
            cartEntity.setAmount(number + cartEntity.getAmount());
            cartEntity.setPrice(foodEntity.getPrice());
            if (cartEntity.getAmount().equals(0)){
                cartEntity.setIsDeleted(1);
            }
            cartRepository.save(cartEntity);
        }
        return ResponseEntity.ok(ResponseDataAPI.builder().build());
    }

    @PostMapping("/cart/add-with-note")
    public ResponseEntity<ResponseDataAPI> addToCartWithNote(@RequestBody AddToCartDomain domain){
        Long userAppId = StringUtils.convertStringToLongOrNull(domain.getUserAppId());
        Long foodId = StringUtils.convertObjectToLongOrNull(domain.getFoodId());
        FoodEntity foodEntity = foodRepository.getById(foodId);
        CartEntity cartEntity = cartRepository.findByUserAppIdAndFoodId(userAppId, foodId);
        Integer number = StringUtils.convertStringToIntegerOrNull(domain.getAmount());
        if (cartEntity == null){
            CartEntity newCart = new CartEntity();
            newCart.setUserAppId(userAppId);
            newCart.setFoodId(foodId);
            newCart.setAmount(number);
            newCart.setPrice(foodEntity.getPrice());
            newCart.setNote(domain.getNote());
            cartRepository.save(newCart);
        } else {
            cartEntity.setAmount(number + cartEntity.getAmount());
            cartEntity.setPrice(foodEntity.getPrice());
            cartEntity.setNote(domain.getNote());
            if (cartEntity.getAmount().equals(0)){
                cartEntity.setIsDeleted(1);
            }
            cartRepository.save(cartEntity);
        }
//        List<CartEntity> cartEntityList = cartRepository.findAllByUserAppId(userAppId);
//        long totalPrice = 0l;
//        if (!CollectionUtils.isEmpty(cartEntityList)){
//            totalPrice = cartEntityList.stream().reduce(0l, (a, b) -> a + b.getPrice()*b.getAmount(), Long::sum);
//        }
        return ResponseEntity.ok(ResponseDataAPI.builder().data(userAppService.getUserCurrentCart(domain.getUserAppId())).build());
    }

    @PostMapping("/cart/delete-and-add")
    public ResponseEntity<ResponseDataAPI> deleteAndAddToCart(@RequestBody AddToCartDomain domain){
        userAppService.deleteAndAddToCart(domain);
        return ResponseEntity.ok(ResponseDataAPI.builder().data(userAppService.getUserCurrentCart(domain.getUserAppId())).build());
    }

    @GetMapping("/transaction")
    public ResponseEntity<ResponseDataAPI> getUserTransaction(@RequestParam("user_app_id") String userApp){
        ResponseDataAPI response = new ResponseDataAPI();
        response.setData(userAppService.getUserTransaction(userApp));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/payment/direct")
    public ResponseEntity<ResponseDataAPI> paymentDirect(@RequestBody DirectPaymentDomain domain){
        return ResponseEntity.ok(ResponseDataAPI.builder().data(userAppService.paymentDirect(domain)).build());
    }
}
