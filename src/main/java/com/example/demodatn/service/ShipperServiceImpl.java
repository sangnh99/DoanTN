package com.example.demodatn.service;

import com.example.demodatn.constant.ShipperBusy;
import com.example.demodatn.constant.ShipperStatus;
import com.example.demodatn.domain.*;
import com.example.demodatn.entity.*;
import com.example.demodatn.exception.CustomException;
import com.example.demodatn.repository.*;
import com.example.demodatn.util.DateTimeUtils;
import com.example.demodatn.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ShipperServiceImpl {

    @Autowired
    private UserAppRepository userAppRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionItemRepository transactionItemRepository;

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private DeliveryAddressRepository deliveryAddressRepository;

    public ResponseDataAPI getAllAvailableTransaction(String valueSearch, Integer offset) {
        ResponseDataAPI responseDataAPI = new ResponseDataAPI();

        Sort sort = Sort.by(Sort.Order.asc("id"));

        Pageable pageable = PageRequest.of(offset, 8, sort);

        String searchValue = valueSearch.trim().toLowerCase(Locale.ROOT);

        Page<TransactionEntity> availableTransactions = transactionRepository.getListAvailableTransactions(searchValue, pageable);

        if (availableTransactions.getTotalElements() == 0l){
            responseDataAPI.setTotalRows(0);
            responseDataAPI.setData(new ArrayList<Object>());
            return responseDataAPI;
        }

        List<TransactionDomain> listResult = availableTransactions.stream().map(transaction -> {
            TransactionDomain domain = new TransactionDomain();
            domain.setId(StringUtils.convertObjectToString(transaction.getId()));

            domain.setDistance(transaction.getDistance());
            domain.setPaymentMethod(transaction.getPaymentMethod());
            domain.setTotal(transaction.getTotal());
            domain.setUserAppId(StringUtils.convertObjectToString(transaction.getUserAppId()));
            UserAppEntity userAppEntity = userAppRepository.findById(transaction.getUserAppId()).orElse(null);
            if (userAppEntity == null){
                throw new CustomException("User ko ton tai", "User ko ton tai", HttpStatus.BAD_REQUEST);
            }
            domain.setUserAppName(userAppEntity.getUsername() + " - " + userAppEntity.getPhone());
            domain.setComment(transaction.getDeliveryAddress());
            Date date = transaction.getCreatedDate();
            String createDate = DateTimeUtils.convertDateToStringOrEmpty(date, DateTimeUtils.YYYYMMDDhhmm);
            domain.setCreateDate(createDate);
            StoreEntity storeEntity = storeRepository.findById(transaction.getStoreId()).orElse(null);
            if (storeEntity == null) {
                throw new CustomException("Store ko ton tai", "Store ko ton tai", HttpStatus.BAD_REQUEST);
            }
            domain.setStoreId(storeEntity.getId().toString());
            domain.setStoreName(storeEntity.getName());
            domain.setAddress(storeEntity.getAddress());
            domain.setStoreAvatar(storeEntity.getAvatar());

            return domain;
        }).collect(Collectors.toList());
        responseDataAPI.setData(listResult);
        responseDataAPI.setTotalRows(availableTransactions.getTotalElements());
        return responseDataAPI;
    }

    public void shipperTakeOrder(ShipperTakeOrderDomain domain) {
        Long shipperId = StringUtils.convertObjectToLongOrNull(domain.getShipperId());
        Long transactionId = StringUtils.convertObjectToLongOrNull(domain.getTransactionId());

        if (shipperId == null || transactionId == null){
            throw new CustomException("Invalid input", "Invalid input", HttpStatus.BAD_REQUEST);
        }

        UserAppEntity shipperEntity = userAppRepository.findById(shipperId).orElse(null);
        TransactionEntity transactionEntity = transactionRepository.findById(transactionId).orElse(null);

        if (shipperEntity == null || transactionEntity == null){
            throw new CustomException("Invalid input", "Invalid input", HttpStatus.BAD_REQUEST);
        }

        if (!ShipperBusy.NOT_BUSY.getNumber().equals(shipperEntity.getIsBusyShipper())){
            throw new CustomException("Vui lòng hoàn thành đơn hàng khác trước khi nhận đơn này", "Vui lòng hoàn thành đơn hàng khác trước khi nhận đơn này", HttpStatus.BAD_REQUEST);
        }
        if (!ShipperStatus.DANG_TIM_TAI_XE.getNumber().equals(transactionEntity.getStatus())){
            throw new CustomException("Đơn hàng này đã được nhận bởi tài xế khác !", "Đơn hàng này đã được nhận bởi tài xế khác !", HttpStatus.BAD_REQUEST);
        }

        transactionEntity.setStatus(ShipperStatus.DANG_CHO_LAY_HANG.getNumber());
        transactionEntity.setShipperId(shipperId);
        shipperEntity.setIsBusyShipper(transactionId.intValue());

        transactionRepository.save(transactionEntity);
        userAppRepository.save(shipperEntity);
    }

    public ResponseDataAPI getCurrentOrderShipper(String shipper) {
        Long shipperId = StringUtils.convertObjectToLongOrNull(shipper);

        if (shipperId == null){
            throw new CustomException("Invalid input", "Invalid input", HttpStatus.BAD_REQUEST);
        }

        UserAppEntity shipperEntity = userAppRepository.findById(shipperId).orElse(null);

        if (shipperEntity == null){
            throw new CustomException("Invalid input", "Invalid input", HttpStatus.BAD_REQUEST);
        }

        if (ShipperBusy.NOT_BUSY.getNumber().equals(shipperEntity.getIsBusyShipper())){
            throw new CustomException("Vui lòng hoàn thành đơn hàng khác trước khi nhận đơn này", "Vui lòng hoàn thành đơn hàng khác trước khi nhận đơn này", HttpStatus.BAD_REQUEST);
        }

        TransactionEntity transaction = transactionRepository.findById(shipperEntity.getIsBusyShipper().longValue()).orElse(null);

        if (transaction == null){
            throw new CustomException("Không tìm thấy đơn hàng", "Không tìm thấy đơn hàng", HttpStatus.BAD_REQUEST);
        }

        if (ShipperStatus.DANG_TIM_TAI_XE.getNumber().equals(transaction.getStatus())){
            throw new CustomException("Đơn hàng này đã bị lỗi !", "Đơn hàng này đã bị lỗi !", HttpStatus.BAD_REQUEST);
        }


        TransactionDomain domain = new TransactionDomain();
        domain.setId(StringUtils.convertObjectToString(transaction.getId()));
        domain.setDistance(transaction.getDistance());
        domain.setPaymentMethod(transaction.getPaymentMethod());
        domain.setTotal(transaction.getTotal());
        domain.setUserAppId(StringUtils.convertObjectToString(transaction.getUserAppId()));
        UserAppEntity userAppEntity = userAppRepository.findById(transaction.getUserAppId()).orElse(null);
        if (userAppEntity == null){
            throw new CustomException("User ko ton tai", "User ko ton tai", HttpStatus.BAD_REQUEST);
        }
        domain.setUserAppName(userAppEntity.getUsername() + " - " + userAppEntity.getPhone());
        domain.setComment(transaction.getDeliveryAddress());
        Date date = transaction.getCreatedDate();
        String createDate = DateTimeUtils.convertDateToStringOrEmpty(date, DateTimeUtils.YYYYMMDDhhmm);
        domain.setCreateDate(createDate);
        List<TransactionItemEntity> listItem = transactionItemRepository.findAllByTransactionId(transaction.getId());

        StoreEntity storeEntity = storeRepository.findById(transaction.getStoreId()).orElse(null);
        if (storeEntity == null) {
            throw new CustomException("Store ko ton tai", "Store ko ton tai", HttpStatus.BAD_REQUEST);
        }
        domain.setStoreId(storeEntity.getId().toString());
        domain.setStoreName(storeEntity.getName());
        domain.setAddress(storeEntity.getAddress());
        domain.setStoreAvatar(storeEntity.getAvatar());
        domain.setLat(transaction.getDeliveryLatitude());
        domain.setLng(transaction.getDeliveryLongitude());
        List<TransactionItemDomain> listResponseItem = new ArrayList<>();
        for (TransactionItemEntity transactionItemEntity : listItem){
            TransactionItemDomain itemDomain = new TransactionItemDomain();
            itemDomain.setTransactionId(transaction.getId().toString());
            itemDomain.setFoodId(transactionItemEntity.getFoodId().toString());
            FoodEntity foodEntity = foodRepository.findById(transactionItemEntity.getFoodId()).orElse(null);
            if (foodEntity != null){
                itemDomain.setAmount(transactionItemEntity.getAmount());
                itemDomain.setNote(transactionItemEntity.getNote());
                itemDomain.setPrice(transactionItemEntity.getPrice());
                itemDomain.setDiscountPercent(transactionItemEntity.getDiscountPercent());
                itemDomain.setOriginalPrice(transactionItemEntity.getOriginalPrice());
                itemDomain.setFoodAvatar(foodEntity.getAvatar());
                itemDomain.setFoodName(foodEntity.getName());
            } else {
                throw new CustomException("Có món ăn đã bị xóa", "Có món ăn đã bị xóa", HttpStatus.BAD_REQUEST);
            }

            listResponseItem.add(itemDomain);
        }
        domain.setListItem(listResponseItem);

        transaction.setTimeStart(new Date());
        transactionRepository.save(transaction);
        ResponseDataAPI responseDataAPI = new ResponseDataAPI();
        responseDataAPI.setData(domain);
        return responseDataAPI;
    }

    public void cancelCurrentOrder(ShipperTakeOrderDomain domain) {
        Long shipperId = StringUtils.convertObjectToLongOrNull(domain.getShipperId());
        Long transactionId = StringUtils.convertObjectToLongOrNull(domain.getTransactionId());

        if (shipperId == null || transactionId == null){
            throw new CustomException("Invalid input", "Invalid input", HttpStatus.BAD_REQUEST);
        }

        UserAppEntity shipperEntity = userAppRepository.findById(shipperId).orElse(null);
        TransactionEntity transactionEntity = transactionRepository.findById(transactionId).orElse(null);

        if (shipperEntity == null || transactionEntity == null || !transactionEntity.getId().equals(shipperEntity.getIsBusyShipper().longValue())){
            throw new CustomException("Đơn hàng này bị sai", "Đơn hàng này bị sai", HttpStatus.BAD_REQUEST);
        }

        if (ShipperStatus.DA_GIAO_THANH_CONG.getNumber().equals(transactionEntity.getStatus())){
            throw new CustomException("Đơn hàng này đã được hoàn thành !", "Đơn hàng này đã được hoàn thành !", HttpStatus.BAD_REQUEST);
        }

        transactionEntity.setStatus(ShipperStatus.DANG_TIM_TAI_XE.getNumber());
        transactionEntity.setTimeStart(null);
        transactionEntity.setShipperId(null);
        shipperEntity.setIsBusyShipper(ShipperBusy.NOT_BUSY.getNumber());

        transactionRepository.save(transactionEntity);
        userAppRepository.save(shipperEntity);
    }

    public void continueCurrentOrder(ShipperTakeOrderDomain domain) {
        Long shipperId = StringUtils.convertObjectToLongOrNull(domain.getShipperId());
        Long transactionId = StringUtils.convertObjectToLongOrNull(domain.getTransactionId());

        if (shipperId == null || transactionId == null){
            throw new CustomException("Invalid input", "Invalid input", HttpStatus.BAD_REQUEST);
        }

        UserAppEntity shipperEntity = userAppRepository.findById(shipperId).orElse(null);
        TransactionEntity transactionEntity = transactionRepository.findById(transactionId).orElse(null);

        if (shipperEntity == null || transactionEntity == null || !transactionEntity.getId().equals(shipperEntity.getIsBusyShipper().longValue())){
            throw new CustomException("Đơn hàng này bị sai", "Đơn hàng này bị sai", HttpStatus.BAD_REQUEST);
        }

        if (ShipperStatus.DA_GIAO_THANH_CONG.getNumber().equals(transactionEntity.getStatus())){
            throw new CustomException("Đơn hàng này đã được hoàn thành !", "Đơn hàng này đã được hoàn thành !", HttpStatus.BAD_REQUEST);
        }
        transactionEntity.setStatus(ShipperStatus.DANG_GIAO.getNumber());
        transactionEntity.setShipperId(shipperId);

        transactionRepository.save(transactionEntity);
    }

    public void finishShipperOrder(ShipperTakeOrderDomain domain) {
        Long shipperId = StringUtils.convertObjectToLongOrNull(domain.getShipperId());
        Long transactionId = StringUtils.convertObjectToLongOrNull(domain.getTransactionId());

        if (shipperId == null || transactionId == null){
            throw new CustomException("Invalid input", "Invalid input", HttpStatus.BAD_REQUEST);
        }

        UserAppEntity shipperEntity = userAppRepository.findById(shipperId).orElse(null);
        TransactionEntity transactionEntity = transactionRepository.findById(transactionId).orElse(null);

        if (shipperEntity == null || transactionEntity == null || !transactionEntity.getId().equals(shipperEntity.getIsBusyShipper().longValue())){
            throw new CustomException("Đơn hàng này bị sai", "Đơn hàng này bị sai", HttpStatus.BAD_REQUEST);
        }

        if (ShipperStatus.DA_GIAO_THANH_CONG.getNumber().equals(transactionEntity.getStatus())){
            throw new CustomException("Đơn hàng này đã được hoàn thành !", "Đơn hàng này đã được hoàn thành !", HttpStatus.BAD_REQUEST);
        }

        Date date = new Date();
        transactionEntity.setStatus(ShipperStatus.DA_GIAO_THANH_CONG.getNumber());
        transactionEntity.setShipperId(shipperId);
        transactionEntity.setTimeEnd(date);
        shipperEntity.setIsBusyShipper(ShipperBusy.NOT_BUSY.getNumber());

        transactionRepository.save(transactionEntity);
        userAppRepository.save(shipperEntity);
    }

    public ResponseDataAPI getShipperTransaction(String shipper, String valueSearch, Integer offset) {
        Long shipperId = StringUtils.convertObjectToLongOrNull(shipper);

        if (shipperId == null){
            throw new CustomException("Invalid input", "Invalid input", HttpStatus.BAD_REQUEST);
        }

        UserAppEntity shipperEntity = userAppRepository.findById(shipperId).orElse(null);

        if (shipperEntity == null){
            throw new CustomException("Shipper này không tồn tại", "Shipper này không tồn tại", HttpStatus.BAD_REQUEST);
        }

        ResponseDataAPI responseDataAPI = new ResponseDataAPI();

        Sort sort = Sort.by(Sort.Order.desc("id"));

        Pageable pageable = PageRequest.of(offset, 8, sort);

        String searchValue = valueSearch.trim().toLowerCase(Locale.ROOT);

        Page<TransactionEntity> listTransaction = transactionRepository.getListTransactionOfShipper(shipperId, searchValue, pageable);

        if (listTransaction.getTotalElements() == 0l){
            responseDataAPI.setTotalRows(0);
            responseDataAPI.setData(new ArrayList<Object>());
            return responseDataAPI;
        }

        Map<Integer, String> shipperStatusMap = new HashMap<>();
        shipperStatusMap.put(ShipperStatus.DANG_TIM_TAI_XE.getNumber(), ShipperStatus.DANG_TIM_TAI_XE.getName());
        shipperStatusMap.put(ShipperStatus.DANG_CHO_LAY_HANG.getNumber(), ShipperStatus.DANG_CHO_LAY_HANG.getName());
        shipperStatusMap.put(ShipperStatus.DANG_GIAO.getNumber(), ShipperStatus.DANG_GIAO.getName());
        shipperStatusMap.put(ShipperStatus.DA_GIAO_THANH_CONG.getNumber(), ShipperStatus.DA_GIAO_THANH_CONG.getName());

        List<TransactionDomain> listResult = new ArrayList<>();

        for (TransactionEntity transaction : listTransaction){
            TransactionDomain domain = new TransactionDomain();
            domain.setId(StringUtils.convertObjectToString(transaction.getId()));
            domain.setComment(transaction.getDeliveryAddress());
            domain.setDistance(transaction.getDistance());
            domain.setPaymentMethod(transaction.getPaymentMethod());
            domain.setTotal(transaction.getTotal());
            domain.setStatus(shipperStatusMap.get(transaction.getStatus()));
            Date date = transaction.getCreatedDate();
            UserAppEntity userAppEntity = userAppRepository.findById(transaction.getUserAppId()).orElse(null);
            if (userAppEntity == null){
                throw new CustomException("User ko ton tai", "User ko ton tai", HttpStatus.BAD_REQUEST);
            }
            domain.setUserAppName(userAppEntity.getUsername() + " - " + userAppEntity.getPhone());
            String createDate = DateTimeUtils.convertDateToStringOrEmpty(date, DateTimeUtils.YYYYMMDDhhmm);
            domain.setCreateDate(createDate);
            List<TransactionItemEntity> listItem = transactionItemRepository.findAllByTransactionId(transaction.getId());

            StoreEntity storeEntity = storeRepository.findById(transaction.getStoreId()).orElse(null);
            domain.setStoreId(storeEntity.getId().toString());
            domain.setStoreName(storeEntity.getName());
            domain.setAddress(storeEntity.getAddress());


            domain.setShipperName(shipperEntity.getFullName());
            domain.setShipperId(shipperEntity.getId());
            domain.setTimeStart(DateTimeUtils.convertDateToStringOrEmpty(transaction.getTimeStart(), DateTimeUtils.YYYYMMDDhhmm));
            domain.setTimeEnd(DateTimeUtils.convertDateToStringOrEmpty(transaction.getTimeEnd(), DateTimeUtils.YYYYMMDDhhmm));


            domain.setStoreAvatar(storeEntity.getAvatar());
            List<TransactionItemDomain> listResponseItem = new ArrayList<>();
            for (TransactionItemEntity transactionItemEntity : listItem){
                TransactionItemDomain itemDomain = new TransactionItemDomain();
                itemDomain.setTransactionId(transaction.getId().toString());
                itemDomain.setFoodId(transactionItemEntity.getFoodId().toString());
                FoodEntity foodEntity = foodRepository.findById(transactionItemEntity.getFoodId()).orElse(null);
                if (foodEntity != null){
                    itemDomain.setAmount(transactionItemEntity.getAmount());
                    itemDomain.setNote(transactionItemEntity.getNote());
                    itemDomain.setPrice(transactionItemEntity.getPrice());
                    itemDomain.setDiscountPercent(transactionItemEntity.getDiscountPercent());
                    itemDomain.setOriginalPrice(transactionItemEntity.getOriginalPrice());
                    itemDomain.setFoodAvatar(foodEntity.getAvatar());
                    itemDomain.setFoodName(foodEntity.getName());
                } else {
                    itemDomain.setAmount(0);
                    itemDomain.setNote("");
                    itemDomain.setPrice(0l);
                    itemDomain.setDiscountPercent(0);
                    itemDomain.setOriginalPrice(0l);
                    itemDomain.setFoodAvatar("https://media.istockphoto.com/vectors/red-white-stamp-grunge-deleted-vector-id1174096245?s=612x612");
                    itemDomain.setFoodName("");
                }


                listResponseItem.add(itemDomain);
            }
            domain.setListItem(listResponseItem);
            listResult.add(domain);


        }
        responseDataAPI.setData(listResult);
        responseDataAPI.setTotalRows(listTransaction.getTotalElements());
        return responseDataAPI;
    }

    public ResponseDataAPI getPersonalInfoShipper(String shipper) {
        Long shipperId = StringUtils.convertObjectToLongOrNull(shipper);

        if (shipperId == null){
            throw new CustomException("Invalid input", "Invalid input", HttpStatus.BAD_REQUEST);
        }

        UserAppEntity shipperEntity = userAppRepository.findById(shipperId).orElse(null);

        if (shipperEntity == null){
            throw new CustomException("Shipper này không tồn tại", "Shipper này không tồn tại", HttpStatus.BAD_REQUEST);
        }

        ShipperInfoResponseDomain domain = new ShipperInfoResponseDomain();
        domain.setId(shipperEntity.getId());
        domain.setCode("Shipper00" + shipperEntity.getId());
        domain.setFullName(shipperEntity.getFullName());
        domain.setCmnd(shipperEntity.getCmnd());
        domain.setPhone(shipperEntity.getPhone());
        domain.setAddress(shipperEntity.getAddress());
        domain.setEmail(shipperEntity.getEmail());
        domain.setAvatar(shipperEntity.getAvatar());
        domain.setCreatedDate(DateTimeUtils.convertDateToStringOrEmpty(shipperEntity.getCreatedDate(), DateTimeUtils.YYYYMMDDhhmm));

        ResponseDataAPI responseDataAPI = new ResponseDataAPI();
        responseDataAPI.setData(domain);
        return responseDataAPI;
    }

    public void changePasswordShipper(ChangePasswordAdminDomain domain) {
        Long shipperId = StringUtils.convertObjectToLongOrNull(domain.getId());
        if (shipperId == null){
            throw new CustomException("Shipper ID bi sai", "Shipper ID bi sai", HttpStatus.BAD_REQUEST);
        }
        UserAppEntity shipperEntity = userAppRepository.findById(shipperId).orElse(null);

        if (shipperEntity == null){
            throw new CustomException("Shipper ID bi sai", "Shipper ID bi sai", HttpStatus.BAD_REQUEST);
        }

        if (!BCrypt.checkpw(domain.getOldPassword(), shipperEntity.getPassword())){
            throw new CustomException("Mat khau cu bi sai", "Mat khau cu bi sai", HttpStatus.BAD_REQUEST);
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(domain.getNewPassword());

        shipperEntity.setPassword(encodedPassword);
        userAppRepository.save(shipperEntity);
    }
}
