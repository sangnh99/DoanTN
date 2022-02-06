package com.example.demodatn.service;

import com.example.demodatn.entity.FoodEntity;
import com.example.demodatn.entity.TransactionItemEntity;
import com.example.demodatn.repository.*;
import com.example.demodatn.util.CalculateDistanceUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

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

//    @Scheduled(fixedRate = 20000000)
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


}
