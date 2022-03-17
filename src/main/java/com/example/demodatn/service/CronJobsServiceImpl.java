package com.example.demodatn.service;

import com.example.demodatn.entity.*;
import com.example.demodatn.exception.CustomException;
import com.example.demodatn.repository.*;
import com.example.demodatn.util.CalculateDistanceUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CronJobsServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(CronJobsServiceImpl.class.getName());

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










    @Scheduled(fixedRate = 1700000)
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
        logger.info("Find top ten best seller");
    }

    @Scheduled(fixedRate = 1000000)
//    need
    public void setSummaryRating(){
        List<FoodEntity> listFood = foodRepository.findAll();
        List<FoodEntity> listResultFood = new ArrayList<>();
        for (FoodEntity foodEntity : listFood) {
            List<RatingEntity> listRatingOfFood = ratingRepository.findAllByFoodId(foodEntity.getId());
            if (!CollectionUtils.isEmpty(listRatingOfFood)) {
                Double totalRating = listRatingOfFood.stream().map(t -> t.getRating()).reduce(0l, (t1, t2) -> t1 + t2).doubleValue()/ listRatingOfFood.size();
                foodEntity.setSummaryRating(Math.round(totalRating*1000.0)/1000.0);
                listResultFood.add(foodEntity);
            }
        }
        foodRepository.saveAll(listResultFood);
        List<StoreEntity> listStore = storeRepository.findAll();
        List<StoreEntity> listResultStore = new ArrayList<>();
        for (StoreEntity store : listStore){
            List<FoodEntity> listFoodOfStore = foodRepository.findAllByStoreId(store.getId());
            List<FoodEntity> listFoodHaveRating = listFoodOfStore.stream().filter(t -> t.getSummaryRating() != null).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(listFoodHaveRating)){
                Double sumRating = listFoodHaveRating.stream().map(t -> t.getSummaryRating()).reduce(0.0, (t1, t2) -> t1 + t2)/listFoodHaveRating.size();
                store.setSummaryRating(Math.round(sumRating*1000.0)/1000.0);
                listResultStore.add(store);
                System.out.println("Store : " + store.getId());
                listFoodHaveRating.forEach(t -> System.out.println("Food : " + t.getId() + ", rating : " + t.getSummaryRating()));
            }

        }
        storeRepository.saveAll(listResultStore);
        logger.info("Set summary rating");
    }

    @Scheduled(fixedRate = 86500000)
//    need
    public void findInfoForAdminPage(){
        Long total = transactionRepository.findAll()
                .stream()
                .map(t -> t.getTotal())
                .reduce(0l, (t1, t2) -> (t1 + t2));
        MetadataEntity metadataEntity = metadataRepository.findById(1l).orElse(null);
        if (metadataEntity == null){
            throw new CustomException("tinh doanh thu cua thang bi sai", "tinh doanh thu cua thang bi sai", HttpStatus.BAD_REQUEST);
        }
        metadataEntity.setTotalIncome(total);
        metadataEntity.setTotalFood(foodRepository.findAll().size());
        metadataRepository.save(metadataEntity);
        logger.info("Find info for admin page");
    }

}
