package com.example.demodatn.service;

import com.example.demodatn.constant.Error;
import com.example.demodatn.constant.PaymentMethod;
import com.example.demodatn.entity.*;
import com.example.demodatn.exception.CustomException;
import com.example.demodatn.repository.*;
import com.example.demodatn.util.CalculateDistanceUtils;
import com.example.demodatn.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PaypalServiceImpl {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private CalculateDistanceUtils calculateDistanceUtils;

    @Autowired
    private UserAppRepository userAppRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionItemRepository transactionItemRepository;

    public String handlePaypalSuccess(String userApp) {
        Long userAppId = StringUtils.convertStringToLongOrNull(userApp);
        if (userAppId == null){
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
        transactionEntity.setPaymentMethod(PaymentMethod.PAYPAL.getName());
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
        transactionEntity.setTotal(totalPrice);
        transactionRepository.save(transactionEntity);
        transactionItemRepository.saveAll(listTransactionItem);
        cartRepository.saveAll(listCartOfUser);

        return "success";
    }
}
