package com.example.demodatn.service;

import com.example.demodatn.constant.Error;
import com.example.demodatn.constant.IsLocked;
import com.example.demodatn.domain.AddAddressDomain;
import com.example.demodatn.domain.AddAddressNewUserDomain;
import com.example.demodatn.domain.AddressDomain;
import com.example.demodatn.domain.ChangeActiveAddressDomain;
import com.example.demodatn.entity.DeliveryAddressEntity;
import com.example.demodatn.entity.UserAppEntity;
import com.example.demodatn.exception.CustomException;
import com.example.demodatn.repository.DeliveryAddressRepository;
import com.example.demodatn.repository.UserAppRepository;
import com.example.demodatn.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.print.DocFlavor;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class AddressServiceImpl {
    @Autowired
    private DeliveryAddressRepository deliveryAddressRepository;

    @Autowired
    private UserAppRepository userAppRepository;

    public List<AddressDomain> getListAddressOfUser(String userApp) {
        Long userAppId = StringUtils.convertStringToLongOrNull(userApp);
        if (userAppId == null) {
            throw new CustomException(Error.PARAMETER_INVALID.getMessage()
                    , Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }
        UserAppEntity userAppEntity = userAppRepository.findById(userAppId).orElse(null);
        if (userAppEntity == null) {
            throw new CustomException(Error.PARAMETER_INVALID.getMessage()
                    , Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }

        Long isActiveAddress = userAppEntity.getActiveAddressId();
        if (isActiveAddress == null){
            isActiveAddress = 0L;
        }
        List<AddressDomain> result = new ArrayList<>();
        List<DeliveryAddressEntity> listAddress = deliveryAddressRepository.findAllByUserAppId(userAppId);
        if (!CollectionUtils.isEmpty(listAddress)) {
            for (DeliveryAddressEntity address : listAddress) {
                AddressDomain domain = new AddressDomain();
                domain.setId(StringUtils.convertObjectToString(address.getId()));
                domain.setName(address.getName());
                domain.setAddress(address.getAddress());
                domain.setLatitude(address.getLatitude());
                domain.setLongitude(address.getLongitude());
                domain.setNote(address.getNote() == null ? "" : address.getNote());
                if (isActiveAddress.equals(address.getId())) {
                    domain.setIsActive(1);
                } else {
                    domain.setIsActive(0);
                }
                result.add(domain);
            }
            result.sort((t1, t2) -> t2.getIsActive().compareTo(t1.getIsActive()));
            return result;
        }
        return new ArrayList<>();
    }

    public List<AddressDomain> changeActiveAddress(String userApp, ChangeActiveAddressDomain domain) {
        Long userAppId = StringUtils.convertStringToLongOrNull(userApp);
        Long addressId = StringUtils.convertObjectToLongOrNull(domain.getAddressId());
        if (userAppId == null || addressId == null) {
            throw new CustomException(Error.PARAMETER_INVALID.getMessage()
                    , Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }
        DeliveryAddressEntity addressEntity = deliveryAddressRepository.findByIdAndUserAppId(addressId, userAppId);
        UserAppEntity userAppEntity = userAppRepository.findById(userAppId).orElse(null);
        if (addressEntity == null || userAppEntity == null) {
            throw new CustomException(Error.PARAMETER_INVALID.getMessage()
                    , Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }
        userAppEntity.setActiveAddressId(addressId);
        userAppRepository.save(userAppEntity);
        return getListAddressOfUser(userApp);
    }

    public List<AddressDomain> deleteAddress(String userApp, ChangeActiveAddressDomain domain) {
        Long userAppId = StringUtils.convertStringToLongOrNull(userApp);
        Long addressId = StringUtils.convertObjectToLongOrNull(domain.getAddressId());
        if (userAppId == null || addressId == null) {
            throw new CustomException(Error.PARAMETER_INVALID.getMessage()
                    , Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }
        DeliveryAddressEntity addressEntity = deliveryAddressRepository.findByIdAndUserAppId(addressId, userAppId);
        UserAppEntity userAppEntity = userAppRepository.findById(userAppId).orElse(null);
        if (addressEntity == null || userAppEntity == null) {
            throw new CustomException(Error.PARAMETER_INVALID.getMessage()
                    , Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }
        List<DeliveryAddressEntity> listCurrentAddress = deliveryAddressRepository.findAllByUserAppId(userAppId);
        if (!CollectionUtils.isEmpty(listCurrentAddress) && listCurrentAddress.size() == 1){
            throw new CustomException("Bạn phải có ít nhất một địa chỉ giao hàng"
                    , "Bạn phải có ít nhất một địa chỉ giao hàng", HttpStatus.BAD_REQUEST);
        }
        addressEntity.setIsDeleted(1);
        deliveryAddressRepository.save(addressEntity);
        if (addressId.equals(userAppEntity.getActiveAddressId())){
            List<DeliveryAddressEntity> listAddress = deliveryAddressRepository.findAllByUserAppId(userAppId);
            if (!CollectionUtils.isEmpty(listAddress)){
                listAddress.sort((t1, t2) -> t2.getId().compareTo(t1.getId()));
                userAppEntity.setActiveAddressId(listAddress.get(0).getId());
                userAppRepository.save(userAppEntity);
            }
        }
        return getListAddressOfUser(userApp);
    }

    public List<AddressDomain> addNewAddress(String userApp, AddAddressDomain domain) {
        Long userAppId = StringUtils.convertStringToLongOrNull(userApp);
        if (userAppId == null) {
            throw new CustomException(Error.PARAMETER_INVALID.getMessage()
                    , Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }
        UserAppEntity userAppEntity = userAppRepository.findById(userAppId).orElse(null);
        if (userAppEntity == null) {
            throw new CustomException(Error.PARAMETER_INVALID.getMessage()
                    , Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }
        List<DeliveryAddressEntity> listAddress = deliveryAddressRepository.findAllByUserAppId(userAppId);
        DeliveryAddressEntity deliveryAddressEntity = new DeliveryAddressEntity();
        deliveryAddressEntity.setUserAppId(userAppId);
        deliveryAddressEntity.setName(domain.getAddressName());
        deliveryAddressEntity.setNote(domain.getAddressNote());
        deliveryAddressEntity.setAddress(domain.getAddressSave());
        deliveryAddressEntity.setLatitude(domain.getLat());
        deliveryAddressEntity.setLongitude(domain.getLng());

        deliveryAddressEntity = deliveryAddressRepository.save(deliveryAddressEntity);
        if (CollectionUtils.isEmpty(listAddress) || userAppEntity.getActiveAddressId() == null){
            userAppEntity.setActiveAddressId(deliveryAddressEntity.getId());
        }
        userAppRepository.save(userAppEntity);
        return getListAddressOfUser(userApp);
    }

    public void addNewAddressNewUser(AddAddressNewUserDomain domain) {
        UserAppEntity userAppEntity = userAppRepository.findByEmail(domain.getEmail());
        if (userAppEntity == null){
            throw new CustomException(Error.PARAMETER_INVALID.getMessage()
                    , Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }
        DeliveryAddressEntity deliveryAddressEntity = new DeliveryAddressEntity();
        deliveryAddressEntity.setUserAppId(userAppEntity.getId());
        deliveryAddressEntity.setName(domain.getAddressName());
        deliveryAddressEntity.setNote(domain.getAddressNote());
        deliveryAddressEntity.setAddress(domain.getAddressSave());
        deliveryAddressEntity.setLatitude(domain.getLat());
        deliveryAddressEntity.setLongitude(domain.getLng());

        deliveryAddressEntity = deliveryAddressRepository.save(deliveryAddressEntity);
        userAppEntity.setActiveAddressId(deliveryAddressEntity.getId());
        userAppEntity.setIsLocked(IsLocked.FALSE.getValue());
        userAppEntity.setAvatar("https://t4.ftcdn.net/jpg/03/46/93/61/360_F_346936114_RaxE6OQogebgAWTalE1myseY1Hbb5qPM.jpg");
        userAppRepository.save(userAppEntity);
    }

    public AddressDomain getActiveAddress(String userApp) {
        Long userAppId = StringUtils.convertStringToLongOrNull(userApp);
        if (userAppId == null) {
            throw new CustomException(Error.PARAMETER_INVALID.getMessage()
                    , Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }
        UserAppEntity userAppEntity = userAppRepository.findById(userAppId).orElse(null);
        if (userAppEntity == null) {
            throw new CustomException(Error.PARAMETER_INVALID.getMessage()
                    , Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }
        Long userAddressId = userAppEntity.getActiveAddressId();
        if (userAddressId == null){
            throw new CustomException(Error.PARAMETER_INVALID.getMessage()
                    , Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }
        DeliveryAddressEntity deliveryAddressEntity = deliveryAddressRepository.findById(userAddressId).orElse(null);
        if (deliveryAddressEntity == null){
            throw new CustomException(Error.PARAMETER_INVALID.getMessage()
                    , Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }

        AddressDomain domain = new AddressDomain();
        domain.setId(StringUtils.convertObjectToString(deliveryAddressEntity.getId()));
        domain.setIsActive(1);
        domain.setAddress(deliveryAddressEntity.getAddress());
        domain.setLongitude(deliveryAddressEntity.getLongitude());
        domain.setLatitude(deliveryAddressEntity.getLatitude());
        domain.setName(deliveryAddressEntity.getName());
        domain.setNote(deliveryAddressEntity.getNote());

        return domain;
    }
}