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
import com.example.demodatn.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
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

    public StoreDetailDomain getStoreDetail(String store, String userApp){
        Long userAppId = StringUtils.convertStringToLongOrNull(userApp);
        Long storeId = StringUtils.convertObjectToLongOrNull(store);
        if (storeId == null || userAppId == null){
            throw new CustomException(Error.PARAMETER_INVALID.getMessage()
                    , Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }
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
        FavouriteEntity favouriteEntity = favouriteRepository.findByUserAppIdAndItemIdAndType(userAppId, storeId, FavouriteType.STORE.getValue());
        if (favouriteEntity == null) {
            storeDetailDomain.setIsFavourite(0);
        } else {
            storeDetailDomain.setIsFavourite(1);
        }
        List<Long> listRatingIds = foodRatingRepository.getListRatingIdsFromStore(storeId);
        List<CommentDomain> listComments = new ArrayList<>();
        if (!CollectionUtils.isEmpty(listRatingIds)){
            listComments = listRatingIds.stream().map(ratingId -> {
                RatingEntity ratingEntity = ratingRepository.getById(ratingId);
                CommentDomain commentDomain = new CommentDomain();
                UserAppEntity userAppEntity = userAppRepository.getById(ratingEntity.getUserAppId());
                FoodEntity foodEntity = foodRatingRepository.findFoodEntityByRatingId(ratingId);
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

        List<SubFoodTypeEntity> listSubfoodType = subFoodTypeRepository.findAllByStoreId(storeEntity.getId()).stream().sorted((t1, t2) -> t1.getId().compareTo(t2.getId())).collect(Collectors.toList());
        List<FoodEntity> listFood = foodRepository.findAllByStoreId(storeEntity.getId());
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
                        foodDomain.setStoreName(storeRepository.findById(t.getStoreId()).orElse(null).getName());
                        foodDomain.setSummaryRating(StringUtils.convertObjectToString(t.getSummaryRating()));
                        foodDomain.setAvatar(t.getAvatar());
                        foodDomain.setPrice(StringUtils.convertObjectToString(t.getPrice()));
                        return foodDomain;
                    })
                    .collect(Collectors.toList()));
            listResult.add(domain);
        }
        storeDetailDomain.setListSubFoodType(listResult);
        return storeDetailDomain;
    }
}

