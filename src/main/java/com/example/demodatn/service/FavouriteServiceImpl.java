package com.example.demodatn.service;

import com.example.demodatn.constant.FavouriteType;
import com.example.demodatn.constant.Error;
import com.example.demodatn.domain.AddToFavouriteDomain;
import com.example.demodatn.domain.FavouriteItemsDomain;
import com.example.demodatn.domain.FoodDomain;
import com.example.demodatn.domain.StoreDomain;
import com.example.demodatn.entity.FavouriteEntity;
import com.example.demodatn.entity.FoodEntity;
import com.example.demodatn.entity.StoreEntity;
import com.example.demodatn.entity.UserAppEntity;
import com.example.demodatn.exception.CustomException;
import com.example.demodatn.repository.*;
import com.example.demodatn.util.CalculateDistanceUtils;
import com.example.demodatn.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class FavouriteServiceImpl {
    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private StoreRepository storeRepository;

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

    public void AddToFavorite(AddToFavouriteDomain domain){
        Long userAppId = StringUtils.convertStringToLongOrNull(domain.getUserAppId());
        Long itemId = StringUtils.convertObjectToLongOrNull(domain.getItemId());
        Integer type = StringUtils.convertStringToIntegerOrNull((domain.getType()));

        if (userAppId == null || itemId == null || type == null){
            throw new CustomException(Error.PARAMETER_INVALID.getMessage()
                    , Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }
        FavouriteEntity favouriteEntity = favouriteRepository.findByUserAppIdAndItemIdAndType(userAppId, itemId, type);
        if (FavouriteType.STORE.getValue().equals(type)){
            StoreEntity storeEntity = storeRepository.findById(itemId).orElse(null);
            if (storeEntity == null){
                throw new CustomException(Error.PARAMETER_INVALID.getMessage()
                        , Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
            }

            if (favouriteEntity == null){
                FavouriteEntity result = new FavouriteEntity();
                result.setUserAppId(userAppId);
                result.setItemId(itemId);
                result.setType(type);
                favouriteRepository.save(result);
            }
        } else if (FavouriteType.FOOD.getValue().equals(type)){
            FoodEntity foodEntity = foodRepository.findById(itemId).orElse(null);
            if (foodEntity == null){
                throw new CustomException(Error.PARAMETER_INVALID.getMessage()
                        , Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
            }
            if (favouriteEntity == null){
                FavouriteEntity result = new FavouriteEntity();
                result.setUserAppId(userAppId);
                result.setItemId(itemId);
                result.setType(type);
                favouriteRepository.save(result);
                foodEntity.setLikeNumber(foodEntity.getLikeNumber() + 1);
                foodRepository.save(foodEntity);
            }
        } else {
            throw new CustomException("Khong them vao ua thich duoc"
                    , "Khong them vao ua thich duoc", HttpStatus.BAD_REQUEST);
        }
    }

    public void DeleteFromFavourite(AddToFavouriteDomain domain){
        Long userAppId = StringUtils.convertStringToLongOrNull(domain.getUserAppId());
        Long itemId = StringUtils.convertObjectToLongOrNull(domain.getItemId());
        Integer type = StringUtils.convertStringToIntegerOrNull((domain.getType()));

        if (userAppId == null || itemId == null || type == null){
            throw new CustomException(Error.PARAMETER_INVALID.getMessage()
                    , Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }
        FavouriteEntity favouriteEntity = favouriteRepository.findByUserAppIdAndItemIdAndType(userAppId, itemId, type);
        if (favouriteEntity == null){
            throw new CustomException("Item nay ko co trong favourite"
                    , "Item nay ko co trong favourite", HttpStatus.BAD_REQUEST);
        }
        favouriteEntity.setIsDeleted(1);
        favouriteRepository.save(favouriteEntity);
    }

    public FavouriteItemsDomain getListFavouriteItems(String userApp) {
        Long userAppId = StringUtils.convertStringToLongOrNull(userApp);
        if (userAppId == null){
            throw new CustomException(Error.PARAMETER_INVALID.getMessage()
                    , Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }
        UserAppEntity userAppEntity = userAppRepository.findById(userAppId).orElse(null);
        Map<Long, Double> distanceMap = calculateDistanceUtils.getDistanceOfAllStores(userAppEntity);
        List<FavouriteEntity> listFavourite = favouriteRepository.findAllByUserAppId(userAppId);
        List<FoodDomain> listFoods= new ArrayList<>();
        List<StoreDomain> listStores = new ArrayList<>();
        FavouriteItemsDomain domain = new FavouriteItemsDomain();
        if (!CollectionUtils.isEmpty(listFavourite)){
            for (FavouriteEntity entity : listFavourite){
                if (FavouriteType.STORE.getValue().equals(entity.getType())){
                    StoreEntity storeEntity = storeRepository.findById(entity.getItemId()).orElse(null);
                    if (storeEntity == null){
                        throw new CustomException(Error.PARAMETER_INVALID.getMessage()
                                , Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
                    }
                    StoreDomain storeDomain = new StoreDomain();
                    storeDomain.setName(storeEntity.getName());
                    storeDomain.setId(StringUtils.convertObjectToString(storeEntity.getId()));
                    storeDomain.setPhone(storeEntity.getPhone());
                    storeDomain.setAddress(storeEntity.getAddress().substring(0, storeEntity.getAddress().length() - 16));
                    storeDomain.setAvatar(storeEntity.getAvatar());
                    storeDomain.setDistance(distanceMap.get(storeEntity.getId()));
                    listStores.add(storeDomain);
                } else {
                    FoodEntity foodEntity = foodRepository.findById(entity.getItemId()).orElse(null);
                    if (foodEntity == null){
                        throw new CustomException(Error.PARAMETER_INVALID.getMessage()
                                , Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
                    }
                    FoodDomain foodDomain = new FoodDomain();
                    foodDomain.setId(StringUtils.convertObjectToString(foodEntity.getId()));
                    foodDomain.setName(foodEntity.getName());
                    foodDomain.setIsBestSeller(foodEntity.getIsBestSeller());
                    foodDomain.setTotalBuy(foodDomain.getTotalBuy());
                    foodDomain.setFoodTypeId(StringUtils.convertObjectToString(foodEntity.getFoodTypeId()));
                    foodDomain.setStoreId(StringUtils.convertObjectToString(foodEntity.getStoreId()));
                    foodDomain.setStoreName(storeRepository.findById(foodEntity.getStoreId()).orElse(null).getName());
                    foodDomain.setSummaryRating(StringUtils.convertObjectToString(foodEntity.getSummaryRating()));
                    foodDomain.setAvatar(foodEntity.getAvatar());
                    foodDomain.setPrice(StringUtils.convertObjectToString(foodEntity.getPrice()));
                    foodDomain.setDiscountPercent(foodEntity.getDiscountPercent());
                    foodDomain.setDistance(distanceMap.get(foodEntity.getStoreId()));
                    listFoods.add(foodDomain);
                }
            }
        }
        domain.setListFoods(listFoods);
        domain.setListStores(listStores);

        return domain;
    }
}
