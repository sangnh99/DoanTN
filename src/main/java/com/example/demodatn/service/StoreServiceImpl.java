package com.example.demodatn.service;

import com.example.demodatn.constant.Error;
import com.example.demodatn.constant.FavouriteType;
import com.example.demodatn.domain.CommentDomain;
import com.example.demodatn.domain.FoodDomain;
import com.example.demodatn.domain.StoreDetailByFoodIdDomain;
import com.example.demodatn.domain.StoreDetailDomain;
import com.example.demodatn.entity.*;
import com.example.demodatn.exception.CustomException;
import com.example.demodatn.repository.*;
import com.example.demodatn.util.CalculateDistanceUtils;
import com.example.demodatn.util.FormatRatingUtils;
import com.example.demodatn.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StoreServiceImpl {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private SubFoodTypeRepository subFoodTypeRepository;

    @Autowired
    private FoodRatingRepository foodRatingRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private UserAppRepository userAppRepository;

    @Autowired
    private FavouriteRepository favouriteRepository;

    @Autowired
    private CalculateDistanceUtils calculateDistanceUtils;

    @Autowired
    private FormatRatingUtils formatRatingUtils;

    @Autowired
    private TransactionItemRepository transactionItemRepository;

    public StoreDetailDomain getStoreDetail(String store, String userApp){
        Long userAppId = StringUtils.convertStringToLongOrNull(userApp);
        Long storeId = StringUtils.convertObjectToLongOrNull(store);
        if (storeId == null || userAppId == null){
            throw new CustomException(Error.PARAMETER_INVALID.getMessage()
                    , Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }

        Double distance = calculateDistanceUtils.getDistanceOfOnlyOneStore(userAppId, storeId);
        StoreEntity storeEntity = storeRepository.getById(storeId);
        if (storeEntity == null){
            throw new CustomException(Error.PARAMETER_INVALID.getMessage()
                    , Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }
        StoreDetailDomain storeDetailDomain = new StoreDetailDomain();
        storeDetailDomain.setId(StringUtils.convertObjectToString(storeEntity.getId()));
        storeDetailDomain.setName(storeEntity.getName());
        storeDetailDomain.setAddress(storeEntity.getAddress());
        storeDetailDomain.setPhone(storeEntity.getPhone());
        storeDetailDomain.setAvatar(storeEntity.getAvatar());
        storeDetailDomain.setLatitude(storeEntity.getLatitude());
        storeDetailDomain.setLongitude(storeEntity.getLongitude());
        storeDetailDomain.setOpenTime(storeEntity.getOpenTime());
        storeDetailDomain.setPriceRange(storeEntity.getPriceRange());

        if (storeEntity.getSummaryRating() != null){
            storeDetailDomain.setSummaryRating(formatRatingUtils.formatRatingOneNumber(storeEntity.getSummaryRating()));
        } else {
            storeDetailDomain.setSummaryRating(0.0);
        }
        FavouriteEntity favouriteEntity = favouriteRepository.findByUserAppIdAndItemIdAndType(userAppId, storeId, FavouriteType.STORE.getValue());
        if (favouriteEntity == null) {
            storeDetailDomain.setIsFavourite(0);
        } else {
            storeDetailDomain.setIsFavourite(1);
        }
        List<RatingEntity> listRatingIds = ratingRepository.getListRatingOfStore(storeId);
        storeDetailDomain.setNumberOfRating(listRatingIds.size());
        List<CommentDomain> listComments = new ArrayList<>();
        if (!CollectionUtils.isEmpty(listRatingIds)){
            listComments = listRatingIds.stream().map(ratingEntity -> {
                CommentDomain commentDomain = new CommentDomain();
                UserAppEntity userAppEntity = userAppRepository.getById(ratingEntity.getUserAppId());
                FoodEntity foodEntity = foodRepository.findById(ratingEntity.getFoodId()).orElse(null);
                commentDomain.setId(StringUtils.convertObjectToString(ratingEntity.getId()));
                commentDomain.setFoodId(StringUtils.convertObjectToString(foodEntity.getId()));
                commentDomain.setFoodName(foodEntity.getName());
                commentDomain.setUserAppName(userAppEntity.getUsername());
                commentDomain.setRating(StringUtils.convertObjectToString(ratingEntity.getRating()));
                commentDomain.setComment(ratingEntity.getComment());
                commentDomain.setLikeNumber(ratingEntity.getLikeNumber() == null ? "0" : StringUtils.convertObjectToString(ratingEntity.getLikeNumber()));
                commentDomain.setDislikeNumber(ratingEntity.getDislikeNumber() == null ? "0" : StringUtils.convertObjectToString(ratingEntity.getDislikeNumber()));
                return commentDomain;
            }).filter(entity -> !StringUtils.isEmpty(entity.getComment())).collect(Collectors.toList());
            storeDetailDomain.setListComments(listComments);
        } else {
            storeDetailDomain.setListComments(new ArrayList<>());
        }

        Map<Integer, String> topMap = new HashMap<>();
        topMap.put(1, "http://res.cloudinary.com/djifhw3lo/image/upload/v1641893032/jvhdomyqo6wqdzcdh3uk.png");
        topMap.put(2, "http://res.cloudinary.com/djifhw3lo/image/upload/v1641893165/lbzzhufhwypefpg9abfu.png");
        topMap.put(3, "http://res.cloudinary.com/djifhw3lo/image/upload/v1641893206/ljcqvbefreejrs87yar1.png");

        List<SubFoodTypeEntity> listSubfoodType = subFoodTypeRepository.findAllByStoreId(storeEntity.getId()).stream().sorted((t1, t2) -> t1.getId().compareTo(t2.getId())).collect(Collectors.toList());
        List<FoodEntity> listFood = foodRepository.findAllByStoreId(storeEntity.getId());

        //get list must try food
        List<FoodEntity> listMustTryFood = listFood.stream().sorted(Comparator.comparing(FoodEntity::getSummaryRating, Comparator.nullsLast(Comparator.reverseOrder())))
                                        .limit(4)
                                        .collect(Collectors.toList());
        List<FoodDomain> listMustTryDomain = new ArrayList<>();

        for (FoodEntity t : listMustTryFood){
            FoodDomain foodDomain = new FoodDomain();
            foodDomain.setId(StringUtils.convertObjectToString(t.getId()));
            foodDomain.setName(t.getName());
            foodDomain.setFoodTypeId(StringUtils.convertObjectToString(t.getFoodTypeId()));
            foodDomain.setStoreId(StringUtils.convertObjectToString(t.getStoreId()));
            foodDomain.setStoreName(storeEntity.getName());
            if (t.getSummaryRating() != null){
                foodDomain.setSummaryRating(StringUtils.convertObjectToString(formatRatingUtils.formatRatingOneNumber(t.getSummaryRating())));
            } else {
                foodDomain.setSummaryRating("0");
            }
            foodDomain.setAvatar(t.getAvatar());
            foodDomain.setDiscountPercent(t.getDiscountPercent());
            foodDomain.setOriginalPrice(t.getOriginalPrice());
            foodDomain.setPrice(StringUtils.convertObjectToString(t.getPrice()));
            foodDomain.setDistance(distance);
            listMustTryDomain.add(foodDomain);
        }
        storeDetailDomain.setListMustTryFood(listMustTryDomain);

        //get list recommend food
        Map<FoodEntity, Integer> recommendFoodMap = new HashMap<>();
        List<FoodDomain> listRecommendFood = new ArrayList<>();
        for (FoodEntity foodEntity : listFood){
            List<TransactionItemEntity> listItem = transactionItemRepository.findAllByFoodId(foodEntity.getId());
            if (!CollectionUtils.isEmpty(listItem)){
                recommendFoodMap.put(foodEntity, listItem.size());
            }
        }
        if (CollectionUtils.isEmpty(recommendFoodMap)){
            storeDetailDomain.setListRecommendFood(new ArrayList<>());
        } else {
            List<FoodEntity> list3FoodRecommend = recommendFoodMap.entrySet()
                    .stream().sorted(Map.Entry.<FoodEntity, Integer>comparingByValue().reversed())
                    .limit(3)
                    .map(t -> t.getKey())
                    .collect(Collectors.toList());
            int count = 1;
            for (FoodEntity t : list3FoodRecommend){
                FoodDomain foodDomain = new FoodDomain();
                foodDomain.setId(StringUtils.convertObjectToString(t.getId()));
                foodDomain.setName(t.getName());
                foodDomain.setFoodTypeId(StringUtils.convertObjectToString(t.getFoodTypeId()));
                foodDomain.setSummaryRating(StringUtils.convertObjectToString(t.getSummaryRating()));
                foodDomain.setAvatar(t.getAvatar());
                foodDomain.setDiscountPercent(t.getDiscountPercent());
                foodDomain.setOriginalPrice(t.getOriginalPrice());
                foodDomain.setPrice(StringUtils.convertObjectToString(t.getPrice()));
                foodDomain.setTopImageUrl(topMap.get(count++));
                listRecommendFood.add(foodDomain);
            }
            storeDetailDomain.setListRecommendFood(listRecommendFood);
        }

        List<StoreDetailByFoodIdDomain> listResult = new ArrayList<>();
        int count = 1;
        for (SubFoodTypeEntity subType : listSubfoodType){
            StoreDetailByFoodIdDomain domain = new StoreDetailByFoodIdDomain();
            domain.setSubFoodTypeId(StringUtils.convertObjectToString(subType.getId()));
            domain.setSubFoodTypeName(subType.getName());
            domain.setKey(StringUtils.convertObjectToString(count ++));
            domain.setListFood(listFood.stream().filter(t -> subType.getId().equals(t.getSubFoodTypeId()))
                    .map(t -> {
                        FoodDomain foodDomain = new FoodDomain();
                        foodDomain.setId(StringUtils.convertObjectToString(t.getId()));
                        foodDomain.setName(t.getName());
                        foodDomain.setFoodTypeId(StringUtils.convertObjectToString(t.getFoodTypeId()));
                        foodDomain.setStoreId(StringUtils.convertObjectToString(t.getStoreId()));
                        foodDomain.setStoreName(storeEntity.getName());
                        foodDomain.setSummaryRating(StringUtils.convertObjectToString(t.getSummaryRating()));
                        foodDomain.setAvatar(t.getAvatar());
                        foodDomain.setDiscountPercent(t.getDiscountPercent());
                        foodDomain.setOriginalPrice(t.getOriginalPrice());
                        foodDomain.setPrice(StringUtils.convertObjectToString(t.getPrice()));
                        foodDomain.setDistance(distance);
                        return foodDomain;
                    })
                    .collect(Collectors.toList()));
            listResult.add(domain);
        }
        storeDetailDomain.setListSubFoodType(listResult);
        return storeDetailDomain;
    }
}

