package com.example.demodatn.service;

import com.example.demodatn.entity.*;
import com.example.demodatn.repository.*;
import com.example.demodatn.util.CalculateDistanceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CronJobsServiceImpl {

    @Autowired
    private UserAppRepository userAppRepository;

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private FavouriteRepository favouriteRepository;

    @Autowired
    private CalculateDistanceUtils calculateDistanceUtils;

    @Autowired
    private MetadataRepository metadataRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionItemRepository transactionItemRepository;

    @Autowired
    private DeliveryAddressRepository deliveryAddressRepository;

//    @Scheduled(fixedRate = 20000000)
//    no need
    public void setTotalBuyForFood(){
        List<FoodEntity> listFood = foodRepository.findAll();
        List<FoodEntity> listSave = new ArrayList<>();
        for (FoodEntity foodEntity : listFood){
            List<TransactionItemEntity> listItem = transactionItemRepository.findAllByFoodId(foodEntity.getId());
            if (!CollectionUtils.isEmpty(listItem)){
                Integer totalAmount = listItem.stream()
                        .map(t -> t.getAmount())
                        .reduce(0, (t1, t2) -> t1 + t2);
                foodEntity.setTotalBuy(totalAmount);
            } else  {
                foodEntity.setTotalBuy(0);
            }
            listSave.add(foodEntity);
        }
        foodRepository.saveAll(listSave);
    }

//    @Scheduled(fixedRate = 20000000)
//    no need
    public void setLikeNumber(){
        List<FoodEntity> listFood = foodRepository.findAll();
        List<FoodEntity> listSave = new ArrayList<>();
        for (FoodEntity foodEntity : listFood){
            List<FavouriteEntity> listFavourite = favouriteRepository.findByItemIdAndType(foodEntity.getId(), 2);

            if (!CollectionUtils.isEmpty(listFavourite)){
                foodEntity.setLikeNumber(listFavourite.size());
            } else  {
                foodEntity.setLikeNumber(0);
            }
            listSave.add(foodEntity);
        }
        foodRepository.saveAll(listSave);
        System.out.println("da in xong");
        System.out.println("da in xong");
        System.out.println("da in xong");
        System.out.println("da in xong");
        System.out.println("da in xong");
        System.out.println("da in xong");
        System.out.println("da in xong");
        System.out.println("da in xong");
        System.out.println("da in xong");
        System.out.println("da in xong");
    }

//    @Scheduled(fixedRate = 20000000)
//    no need
    public void setDeliveryAddressForTransaction(){
        List<TransactionEntity> listTransaction = transactionRepository.findAll();
        List<TransactionEntity> listSave = new ArrayList<>();
        for (TransactionEntity transactionEntity : listTransaction){
            DeliveryAddressEntity deliveryAddressEntity = deliveryAddressRepository.findById(transactionEntity.getDeliveryAddressId()).orElse(null);
            transactionEntity.setDeliveryAddress(deliveryAddressEntity.getAddress());
            transactionEntity.setDeliveryLatitude(deliveryAddressEntity.getLatitude());
            transactionEntity.setDeliveryLongitude(deliveryAddressEntity.getLongitude());
            listSave.add(transactionEntity);
        }
        transactionRepository.saveAll(listSave);
    }

//       @Scheduled(fixedRate = 20000000)
//    no need
    public void setCompleteTransaction(){
        List<TransactionEntity> listTransaction = transactionRepository.findAll();
        List<TransactionEntity> listSave = new ArrayList<>();
        for (TransactionEntity transactionEntity : listTransaction){
            transactionEntity.setStatus(4);
            transactionEntity.setShipperId(30l);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(transactionEntity.getCreatedDate());
            calendar.add(Calendar.MINUTE, 3);
            transactionEntity.setTimeStart(calendar.getTime());

            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(transactionEntity.getCreatedDate());
            calendar1.add(Calendar.MINUTE, 30);
            transactionEntity.setTimeEnd(calendar1.getTime());

            listSave.add(transactionEntity);
        }
        transactionRepository.saveAll(listSave);
    }

    @Scheduled(fixedRate = 86500000)
//    need
    public void findTopTenBestSeller(){
        List<FoodEntity> listFood = foodRepository.findAll();
        List<Long> listTopSeller = listFood.stream()
                                        .sorted((o1, o2) -> o2.getTotalBuy().compareTo(o1.getTotalBuy()))
                                        .limit(10)
                                        .map(t -> t.getId())
                                        .collect(Collectors.toList());
        List<FoodEntity> listResult = new ArrayList<>();
        for (FoodEntity foodEntity : listFood){
            if (listTopSeller.contains(foodEntity.getId())){
                foodEntity.setIsBestSeller(1);
            } else {
                foodEntity.setIsBestSeller(0);
            }
            listResult.add(foodEntity);
        }

        foodRepository.saveAll(listResult);

    }


}
