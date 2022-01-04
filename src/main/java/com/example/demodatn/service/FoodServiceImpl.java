package com.example.demodatn.service;

import com.example.demodatn.constant.*;
import com.example.demodatn.constant.Error;
import com.example.demodatn.domain.*;
import com.example.demodatn.entity.*;
import com.example.demodatn.exception.CustomException;
import com.example.demodatn.repository.*;
import com.example.demodatn.util.CalculateDistanceUtils;
import com.example.demodatn.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FoodServiceImpl {

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
    private SubFoodTypeRepository subFoodTypeRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private FavouriteRepository favouriteRepository;

    @Autowired
    private CalculateDistanceUtils calculateDistanceUtils;

    @Autowired
    private DeliveryAddressRepository deliveryAddressRepository;

    public List<FoodDomain> getListFoodByFoodType(String userApp, String foodTypeStr, BasicRequest request){
        Long userAppId = StringUtils.convertStringToLongOrNull(userApp);
        if (userAppId == null){
            throw new CustomException(Error.PARAMETER_INVALID.getMessage()
                    , Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }
        UserAppEntity userAppEntity = userAppRepository.findById(userAppId).orElse(null);
        Map<Long, Double> distanceMap = calculateDistanceUtils.getDistanceOfAllStores(userAppEntity);
        Long foodType = StringUtils.convertObjectToLongOrNull(foodTypeStr);
        if (foodType == null){
            throw new CustomException(Error.PARAMETER_INVALID.getMessage()
                    , Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }
        List<String> listColumnSort = ColumnSortFood.getListColumnSortFood();
        if (!listColumnSort.contains(request.getColumnSort())){
                request.setColumnSort("id");
            }
        List<String> listTypeSort = ConstantDefine.getTypeSortList();
        if (!listTypeSort.contains(request.getTypeSort())){
            request.setTypeSort("DESC");
        }

        Sort sort = null;

        if (ConstantDefine.SORT_ASC.equals(request.getTypeSort())){
            sort = Sort.by(Sort.Order.asc(request.getColumnSort()));
        } else {
            sort = Sort.by(Sort.Order.desc(request.getColumnSort()));
        }
        Pageable pageable = PageRequest.of(request.getOffset(), request.getLimit(), sort);

        String searchValue = request.getValueSearch().trim().toLowerCase(Locale.ROOT);
        Page<FoodEntity> foodEntities = foodRepository.getListFoodByFoodType(foodType, searchValue , pageable);

        List<FoodDomain> responseList = foodEntities.stream().map(t -> {
                    FoodDomain domain = new FoodDomain();
                    domain.setId(StringUtils.convertObjectToString(t.getId()));
                    domain.setName(t.getName());
                    domain.setFoodTypeId(StringUtils.convertObjectToString(t.getFoodTypeId()));
                    domain.setStoreId(StringUtils.convertObjectToString(t.getStoreId()));
                    domain.setStoreName(storeRepository.findById(t.getStoreId()).orElse(null).getName());
                    domain.setSummaryRating(StringUtils.convertObjectToString(t.getSummaryRating()));
                    domain.setAvatar(t.getAvatar());
                    domain.setPrice(StringUtils.convertObjectToString(t.getPrice()));
                    domain.setDiscountPercent(t.getDiscountPercent());
                    domain.setOriginalPrice(t.getOriginalPrice());
                    domain.setDistance(distanceMap.get(t.getStoreId()));
                    return domain;
                }
        ).collect(Collectors.toList());
        return responseList;
    }

    public FoodWithCommentDomain getFoodDetail(String food, String userApp) {
        Long userAppId = StringUtils.convertStringToLongOrNull(userApp);
        Long foodId = StringUtils.convertObjectToLongOrNull(food);
        if (foodId == null || userAppId == null){
            throw new CustomException(Error.PARAMETER_INVALID.getMessage()
                    , Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }
        FoodEntity t = foodRepository.findById(foodId).orElse(null);
        if (t == null){
            throw new CustomException(Error.PARAMETER_INVALID.getMessage()
                    , Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }
        CartEntity cartEntity = cartRepository.findByUserAppIdAndFoodId(userAppId, foodId);
        String cartNote = cartEntity == null ? "" : cartEntity.getNote();
        FoodWithCommentDomain domain = new FoodWithCommentDomain();
        domain.setFoodId(StringUtils.convertObjectToString(t.getId()));
        domain.setFoodName(t.getName());
        domain.setFoodTypeId(StringUtils.convertObjectToString(t.getFoodTypeId()));
        domain.setStoreId(StringUtils.convertObjectToString(t.getStoreId()));
        domain.setStoreName(storeRepository.findById(t.getStoreId()).orElse(null).getName());
        domain.setSummaryRating(t.getSummaryRating() == null ? "0" : StringUtils.convertObjectToString(t.getSummaryRating()));
        domain.setAvatar(t.getAvatar());
        domain.setPrice(StringUtils.convertObjectToString(t.getPrice()));
        domain.setDiscountPercent(t.getDiscountPercent());
        domain.setOriginalPrice(t.getOriginalPrice());
        domain.setDistance(calculateDistanceUtils.getDistanceOfOnlyOneStore(userAppId, t.getStoreId()));
        FavouriteEntity favouriteEntity = favouriteRepository.findByUserAppIdAndItemIdAndType(userAppId, foodId, FavouriteType.FOOD.getValue());
        if (favouriteEntity != null){
            domain.setIsFavourite(1);
        } else {
            domain.setIsFavourite(0);
        }
        domain.setNote(cartNote);
        List<RatingEntity> listRatingIds = ratingRepository.findAllByFoodId(foodId);
        List<CommentDomain> listComments = new ArrayList<>();
        domain.setNumberOfVote("Chưa có lượt đánh giá");
        if (!CollectionUtils.isEmpty(listRatingIds)){
            domain.setNumberOfVote(StringUtils.convertObjectToString(listRatingIds.size()));
            listComments = listRatingIds.stream().map(ratingEntity -> {
                CommentDomain commentDomain = new CommentDomain();
                UserAppEntity userAppEntity = userAppRepository.getById(ratingEntity.getUserAppId());
                commentDomain.setId(StringUtils.convertObjectToString(ratingEntity.getId()));
                commentDomain.setUserAppName(userAppEntity.getUsername());
                commentDomain.setRating(StringUtils.convertObjectToString(ratingEntity.getRating()));
                commentDomain.setComment(ratingEntity.getComment());
                commentDomain.setLikeNumber(ratingEntity.getLikeNumber() == null ? "0" : StringUtils.convertObjectToString(ratingEntity.getLikeNumber()));
                commentDomain.setDislikeNumber(ratingEntity.getDislikeNumber() == null ? "0" : StringUtils.convertObjectToString(ratingEntity.getDislikeNumber()));
                return commentDomain;
            }).filter(entity -> !StringUtils.isEmpty(entity.getComment())).collect(Collectors.toList());
        }
        domain.setListComments(listComments);
        return domain;
    }

    public void updateVoteForFood(VoteDomain domain) {
        Long ratingId = StringUtils.convertObjectToLongOrNull(domain.getRatingId());
        if (ratingId == null){
            throw new CustomException(Error.PARAMETER_INVALID.getMessage()
                    , Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }
        // like la 1, dislike la 0
        RatingEntity ratingEntity = ratingRepository.getById(ratingId);
        if (ratingEntity == null){
            throw new CustomException("Rating id khong ton tai"
                    , Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }
        if ("1".equals(domain.getVote())){
            ratingEntity.setLikeNumber(ratingEntity.getLikeNumber() + 1);
        } else if ("0".equals(domain.getVote())){
            ratingEntity.setDislikeNumber(ratingEntity.getDislikeNumber() + 1);
        }

        ratingRepository.save(ratingEntity);
    }

    public List<StoreDetailByFoodIdDomain> getAllFoodOfStoreByFoodId(String food, String userApp) {
        Long userAppId = StringUtils.convertStringToLongOrNull(userApp);
        Long foodId = StringUtils.convertObjectToLongOrNull(food);
        if (foodId == null || userAppId == null) {
            throw new CustomException(Error.PARAMETER_INVALID.getMessage()
                    , Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }
        FoodEntity foodEntity = foodRepository.findById(foodId).orElse(null);
        if (foodEntity == null) {
            throw new CustomException(Error.PARAMETER_INVALID.getMessage()
                    , Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }
        Double distance = calculateDistanceUtils.getDistanceOfOnlyOneStore(userAppId, foodEntity.getStoreId());
        StoreEntity storeEntity = storeRepository.getById(foodEntity.getStoreId());
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
                        foodDomain.setDiscountPercent(t.getDiscountPercent());
                        foodDomain.setOriginalPrice(t.getOriginalPrice());
                        foodDomain.setDistance(distance);
                        return foodDomain;
                    })
                    .collect(Collectors.toList()));
            listResult.add(domain);
        }
        return listResult;
    }

    public List<Object> getAllBySearchValue(String valueSearchOriginal, String typeSearchStr, Integer offset, String userApp){
        Long userAppId = StringUtils.convertStringToLongOrNull(userApp);
        UserAppEntity userAppEntity = userAppRepository.findById(userAppId).orElse(null);
        Integer typeSearch = StringUtils.convertStringToIntegerOrNull(typeSearchStr);
        if (typeSearch == null){
            throw new CustomException(Error.PARAMETER_INVALID.getMessage()
                    , Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }

        Sort sort = Sort.by(Sort.Order.desc("id"));

        Pageable pageable = PageRequest.of(offset, 12, sort);

        Map<Long, Double> distanceMap = calculateDistanceUtils.getDistanceOfAllStores(userAppEntity);
        String valueSearch = valueSearchOriginal.trim().toLowerCase(Locale.ROOT);
        if (TypeSearch.STORE.getValue().equals(typeSearch)){
            Page<StoreEntity> listStore = storeRepository.findStoreBySearchValue(valueSearch, pageable);
            if (listStore.getTotalElements() != 0){
                return listStore.stream().map(t -> {
                    StoreDomain storeDomain = new StoreDomain();
                    storeDomain.setId(StringUtils.convertObjectToString(t.getId()));
                    storeDomain.setName(t.getName());
                    storeDomain.setAvatar(t.getAvatar());
                    storeDomain.setAddress(t.getAddress());
                    storeDomain.setPhone(t.getPhone());
                    storeDomain.setDistance(calculateDistanceUtils.getDistanceOfOnlyOneStore(userAppId, t.getId()));
                    return storeDomain;
                }).collect(Collectors.toList());
            }
        } else {
            Page<FoodEntity> listFood = foodRepository.findFoodBySearchValue(valueSearch, pageable);
            if (listFood.getTotalElements() != 0){
                return listFood.stream().map(t -> {
                    FoodDomain domain = new FoodDomain();
                    domain.setId(StringUtils.convertObjectToString(t.getId()));
                    domain.setName(t.getName());
                    domain.setFoodTypeId(StringUtils.convertObjectToString(t.getFoodTypeId()));
                    domain.setStoreId(StringUtils.convertObjectToString(t.getStoreId()));
                    domain.setStoreName(storeRepository.findById(t.getStoreId()).orElse(null).getName());
                    domain.setSummaryRating(StringUtils.convertObjectToString(t.getSummaryRating()));
                    domain.setAvatar(t.getAvatar());
                    domain.setPrice(StringUtils.convertObjectToString(t.getPrice()));
                    domain.setDiscountPercent(t.getDiscountPercent());
                    domain.setOriginalPrice(t.getOriginalPrice());
                    domain.setDistance(distanceMap.get(t.getStoreId()));
                    return domain;
                }).collect(Collectors.toList());
            }
        }
        return new ArrayList<>();
    }

    public List<StoreDomain> geListNearFood(String userApp) {
        Long userAppId = StringUtils.convertStringToLongOrNull(userApp);
        if (userAppId == null) {
            throw new CustomException(Error.PARAMETER_INVALID.getMessage()
                    , Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }
        UserAppEntity userAppEntity = userAppRepository.findById(userAppId).orElse(null);
        if (userAppEntity == null){
            throw new CustomException(Error.PARAMETER_INVALID.getMessage()
                    , Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }
        DeliveryAddressEntity deliveryAddressEntity = deliveryAddressRepository.findById(userAppEntity.getActiveAddressId()).orElse(null);
        if (deliveryAddressEntity == null){
            throw new CustomException(Error.PARAMETER_INVALID.getMessage()
                    , Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }
        Double userLatitude = deliveryAddressEntity.getLatitude();
        Double userLongitude = deliveryAddressEntity.getLongitude();
        List<StoreEntity> listStore = storeRepository.findAll().stream().limit(10).collect(Collectors.toList());

        Double distance;
        List<StoreDomain> listResult = new ArrayList<>();
        for (StoreEntity storeEntity : listStore){
            StoreDomain domain = new StoreDomain();
            domain.setId(StringUtils.convertObjectToString(storeEntity.getId()));
            domain.setName(storeEntity.getName());
            domain.setAddress(storeEntity.getAddress());
            domain.setPhone(storeEntity.getPhone());
            domain.setAvatar(storeEntity.getAvatar());
            distance = calculateDistanceUtils.getDistance(userLatitude, userLongitude, storeEntity.getLatitude(), storeEntity.getLongitude());
            domain.setDistance(distance);
            listResult.add(domain);
        }

        listResult.sort((t1, t2) -> t1.getDistance().compareTo(t2.getDistance()));
        return listResult;
    }

    public String addNewRatingForFood(String food, AddRatingDomain domain) {
        String success = "";
        Long foodId = StringUtils.convertStringToLongOrNull(food);
        Long userAppId = StringUtils.convertStringToLongOrNull(domain.getUserAppId());
        Long rating = StringUtils.convertStringToLongOrNull(domain.getRating());

        if (foodId == null || userAppId == null){
            throw new CustomException(Error.PARAMETER_INVALID.getMessage()
                    , Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }

        RatingEntity ratingEntity = new RatingEntity();
        ratingEntity.setUserAppId(userAppId);
        ratingEntity.setFoodId(foodId);
        ratingEntity.setRating(rating);
        ratingEntity.setComment(domain.getComment());
        ratingEntity.setDislikeNumber(0l);
        ratingEntity.setLikeNumber(0l);

        ratingRepository.save(ratingEntity);

        success = "success";
        return success;
    }
}
