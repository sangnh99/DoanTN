package com.example.demodatn.service;

import com.example.demodatn.constant.Error;
import com.example.demodatn.constant.IsLocked;
import com.example.demodatn.constant.PaymentMethod;
import com.example.demodatn.constant.RoleConstant;
import com.example.demodatn.domain.*;
import com.example.demodatn.entity.*;
import com.example.demodatn.exception.CustomException;
import com.example.demodatn.repository.*;
import com.example.demodatn.util.CalculateDistanceUtils;
import com.example.demodatn.util.DateTimeUtils;
import com.example.demodatn.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private CalculateDistanceUtils calculateDistanceUtils;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionItemRepository transactionItemRepository;

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
        userAppEntity.setIsLocked(IsLocked.TRUE.getValue());
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

    public List<CartDomain> getUserCurrentCart(String idStr){
        UserAppEntity userApp = userAppRepository.getById(StringUtils.convertStringToLongOrNull(idStr));
        if (userApp == null) {
            throw new CustomException("Id cua user bi sai", "wrong user id", HttpStatus.BAD_REQUEST);
        }

        List<CartEntity> cartEntityList = cartRepository.getAllByUserAppIdWithOrder(userApp.getId());
        Double distance = 0.0;
        if (!CollectionUtils.isEmpty(cartEntityList)){
            FoodEntity foodEntity = foodRepository.findById(cartEntityList.get(0).getFoodId()).orElse(null);
            distance = calculateDistanceUtils.getDistanceOfOnlyOneStore(StringUtils.convertStringToLongOrNull(idStr), foodEntity.getStoreId());
        }

        Double finalDistance = distance;
        List<CartDomain> listResult = cartEntityList.stream().map(t -> {
            CartDomain domain = new CartDomain();
//            domain.setCartId(StringUtils.convertObjectToString(t.getId()));
            domain.setFoodId(StringUtils.convertObjectToString(t.getFoodId()));
            FoodEntity foodEntity = foodRepository.findById(t.getFoodId()).orElse(null);
            domain.setFoodTypeId(StringUtils.convertObjectToString(foodEntity.getFoodTypeId()));
            domain.setFoodName(foodEntity.getName());
            StoreEntity storeEntity = storeRepository.findById(foodEntity.getStoreId()).orElse(null);
            domain.setStoreId(StringUtils.convertObjectToString(storeEntity.getId()));
            domain.setStoreName(storeEntity.getName());
            domain.setAmount(t.getAmount());
            domain.setPrice(StringUtils.convertObjectToString(t.getPrice()));
            domain.setAvatar(foodEntity.getAvatar());
            domain.setDiscountPercent(foodEntity.getDiscountPercent());
            domain.setOriginalPrice(foodEntity.getOriginalPrice());
            domain.setNote(t.getNote());
            domain.setDistance(finalDistance);
            System.out.println(finalDistance);
            return domain;
        }).collect(Collectors.toList());
        return listResult;
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
        cartEntity.setNote(domain.getNote());

        cartRepository.save(cartEntity);
    }

    public List<TransactionDomain> getUserTransaction(String userApp) {
        Long userAppId = StringUtils.convertStringToLongOrNull(userApp);
        UserAppEntity userAppEntity = userAppRepository.findById(userAppId).orElse(null);
        if (userAppEntity == null){
            throw new CustomException("Id cua user bi sai", "wrong user id", HttpStatus.BAD_REQUEST);
        }
        List<TransactionEntity> listTransaction = transactionRepository.findAllByUserAppIdOrderByCreatedDateDesc(userAppId);
        listTransaction = listTransaction.stream().limit(10).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(listTransaction)){
            return new ArrayList<>();
        }

        List<TransactionDomain> listResult = new ArrayList<>();

        for (TransactionEntity transaction : listTransaction){
            TransactionDomain domain = new TransactionDomain();
            domain.setId(StringUtils.convertObjectToString(transaction.getId()));
            domain.setComment(transaction.getComment());
            domain.setDistance(transaction.getDistance());
            domain.setPaymentMethod(transaction.getPaymentMethod());
            domain.setTotal(transaction.getTotal());
            Date date = transaction.getCreatedDate();
            String createDate = DateTimeUtils.convertDateToStringOrEmpty(date, DateTimeUtils.YYYYMMDDhhmm);
            domain.setCreateDate(createDate);
            List<TransactionItemEntity> listItem = transactionItemRepository.findAllByTransactionId(transaction.getId());

            StoreEntity storeEntity = storeRepository.findById(foodRepository.getById(listItem.get(0).getFoodId()).getStoreId()).orElse(null);
            domain.setStoreId(storeEntity.getId().toString());
            domain.setStoreName(storeEntity.getName());
            domain.setAddress(storeEntity.getAddress());
            domain.setStoreAvatar(storeEntity.getAvatar());
            List<TransactionItemDomain> listResponseItem = new ArrayList<>();
            for (TransactionItemEntity transactionItemEntity : listItem){
                TransactionItemDomain itemDomain = new TransactionItemDomain();
                itemDomain.setTransactionId(transaction.getId().toString());
                itemDomain.setFoodId(transactionItemEntity.getFoodId().toString());
                FoodEntity foodEntity = foodRepository.findById(transactionItemEntity.getFoodId()).orElse(null);
                itemDomain.setAmount(transactionItemEntity.getAmount());
                itemDomain.setNote(transactionItemEntity.getNote());
                itemDomain.setPrice(transactionItemEntity.getPrice());
                itemDomain.setDiscountPercent(transactionItemEntity.getDiscountPercent());
                itemDomain.setOriginalPrice(transactionItemEntity.getOriginalPrice());
                itemDomain.setFoodAvatar(foodEntity.getAvatar());
                itemDomain.setFoodName(foodEntity.getName());
                listResponseItem.add(itemDomain);
            }
            domain.setListItem(listResponseItem);
            listResult.add(domain);
        }
        return listResult;
    }

    public String paymentDirect(DirectPaymentDomain domain) {
        Long userAppId = StringUtils.convertStringToLongOrNull(domain.getUserAppId());
        Long total = StringUtils.convertObjectToLongOrNull(domain.getTotal());

        if (userAppId == null || total == null){
            throw new CustomException(Error.PARAMETER_INVALID.getMessage()
                    , Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }

        List<CartEntity> listCartOfUser = cartRepository.findAllByUserAppId(userAppId);
        FoodEntity foodEntity = foodRepository.findById(listCartOfUser.get(0).getFoodId()).orElse(null);
        StoreEntity storeEntity = storeRepository.findById(foodEntity.getStoreId()).orElse(null);


        Double distance = calculateDistanceUtils.getDistanceOfOnlyOneStore(userAppId, storeEntity.getId());

        Double distanceDeliveryPrice =  Math.floor(distance*7) * 1000;

        Long totalPrice = distanceDeliveryPrice.longValue();

        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.setUserAppId(userAppId);
        transactionEntity.setPaymentMethod(PaymentMethod.DIRECT.getName());
        transactionEntity.setDistance(distance);
        transactionEntity.setDeliveryAddressId(userAppRepository.findById(userAppId).orElse(null).getActiveAddressId());

        transactionEntity = transactionRepository.save(transactionEntity);

        List<TransactionItemEntity> listTransactionItem = new ArrayList<>();
        for (CartEntity cartEntity : listCartOfUser){
            //get total price
            totalPrice += cartEntity.getPrice()*cartEntity.getAmount();

            //insert transaction item
            TransactionItemEntity transactionItemEntity = new TransactionItemEntity();
            transactionItemEntity.setTransactionId(transactionEntity.getId());
            FoodEntity foodEntityItem = foodRepository.findById(cartEntity.getFoodId()).orElse(null);
            transactionItemEntity.setFoodId(foodEntityItem.getId());
            transactionItemEntity.setAmount(cartEntity.getAmount());
            transactionItemEntity.setPrice(foodEntityItem.getPrice());
            transactionItemEntity.setDiscountPercent(foodEntityItem.getDiscountPercent());
            transactionItemEntity.setOriginalPrice((foodEntityItem.getOriginalPrice()));
            transactionItemEntity.setNote(cartEntity.getNote());
            listTransactionItem.add(transactionItemEntity);

            //delete cart
            cartEntity.setIsDeleted(1);
        }
        if (!totalPrice.equals(total)){
            throw new CustomException("Gia tri don hang bi tinh sai"
                    , "Gia tri don hang bi tinh sai", HttpStatus.BAD_REQUEST);
        }
        transactionEntity.setTotal(totalPrice);
        transactionRepository.save(transactionEntity);
        transactionItemRepository.saveAll(listTransactionItem);
        cartRepository.saveAll(listCartOfUser);

        return "success";
    }
}
