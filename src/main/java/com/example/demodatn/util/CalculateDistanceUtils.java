package com.example.demodatn.util;

import com.example.demodatn.constant.Error;
import com.example.demodatn.entity.DeliveryAddressEntity;
import com.example.demodatn.entity.StoreEntity;
import com.example.demodatn.entity.UserAppEntity;
import com.example.demodatn.exception.CustomException;
import com.example.demodatn.repository.DeliveryAddressRepository;
import com.example.demodatn.repository.StoreRepository;
import com.example.demodatn.repository.UserAppRepository;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CalculateDistanceUtils {

    @Autowired
    private UserAppRepository userAppRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private DeliveryAddressRepository deliveryAddressRepository;

    public double getDistance(double lat1, double lon1,
                            double lat2, double lon2) {
        // distance between latitudes and longitudes
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        // convert to radians
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        // apply formulae
        double a = Math.pow(Math.sin(dLat / 2), 2) +
                Math.pow(Math.sin(dLon / 2), 2) *
                        Math.cos(lat1) *
                        Math.cos(lat2);
        double rad = 6371;
        double c = 2 * Math.asin(Math.sqrt(a));
        return (double) Math.round(rad * c * 10) / 10;
    }

    public Map<Long, Double> getDistanceOfAllStores(UserAppEntity userAppEntity){
        Map<Long,  Double> result = new HashMap<>();
        DeliveryAddressEntity deliveryAddressEntity = deliveryAddressRepository.findById(userAppEntity.getActiveAddressId()).orElse(null);
        if (deliveryAddressEntity == null){
            throw new CustomException(Error.PARAMETER_INVALID.getMessage()
                    , Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }
        Double userLatitude = deliveryAddressEntity.getLatitude();
        Double userLongitude = deliveryAddressEntity.getLongitude();
        List<StoreEntity> listStore = storeRepository.findAll();
        Double distance;
        for (StoreEntity storeEntity : listStore){
             distance = getDistance(userLatitude, userLongitude, storeEntity.getLatitude(), storeEntity.getLongitude());
             result.put(storeEntity.getId(), distance);
        }
        for (Map.Entry a : result.entrySet()){
            System.out.println(a.getKey() + "-" + a.getValue());
        }
        return result;
    }


    public Double getDistanceOfOnlyOneStore(Long userAppId, Long storeId){
        UserAppEntity userAppEntity = userAppRepository.findById(userAppId).orElse(null);
        DeliveryAddressEntity deliveryAddressEntity = deliveryAddressRepository.findById(userAppEntity.getActiveAddressId()).orElse(null);
        if (deliveryAddressEntity == null){
            throw new CustomException(Error.PARAMETER_INVALID.getMessage()
                    , Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }
        Double userLatitude = deliveryAddressEntity.getLatitude();
        Double userLongitude = deliveryAddressEntity.getLongitude();
        StoreEntity storeEntity = storeRepository.findById(storeId).orElse(null);
        Double distance = getDistance(userLatitude, userLongitude, storeEntity.getLatitude(), storeEntity.getLongitude());
        return distance;
    }
}
