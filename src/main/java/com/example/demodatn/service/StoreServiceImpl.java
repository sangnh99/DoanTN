package com.example.demodatn.service;

import com.example.demodatn.constant.Error;
import com.example.demodatn.constant.FavouriteType;
import com.example.demodatn.domain.*;
import com.example.demodatn.entity.*;
import com.example.demodatn.exception.CustomException;
import com.example.demodatn.repository.*;
import com.example.demodatn.util.CalculateDistanceUtils;
import com.example.demodatn.util.DateTimeUtils;
import com.example.demodatn.util.FormatRatingUtils;
import com.example.demodatn.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
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

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private RatingImageRepository ratingImageRepository;


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
            listComments = listRatingIds.stream().filter(entity -> !StringUtils.isEmpty(entity.getComment()))
                    .sorted((t1, t2) -> t2.getCreatedDate().compareTo(t1.getCreatedDate()))
                    .map(ratingEntity -> {
                CommentDomain commentDomain = new CommentDomain();
                UserAppEntity userAppEntity = userAppRepository.getById(ratingEntity.getUserAppId());
                FoodEntity foodEntity = foodRepository.findById(ratingEntity.getFoodId()).orElse(null);
                commentDomain.setId(StringUtils.convertObjectToString(ratingEntity.getId()));
                commentDomain.setFoodId(StringUtils.convertObjectToString(foodEntity.getId()));
                commentDomain.setFoodName(foodEntity.getName());
                commentDomain.setUserAvatar(userAppEntity.getAvatar());
                List<RatingImageEntity> listImageRating = ratingImageRepository.findByRatingId(ratingEntity.getId());
                if (CollectionUtils.isEmpty(listImageRating)){
                    commentDomain.setListImage(new ArrayList<>());
                } else {
                    commentDomain.setListImage(listImageRating.stream().map(a -> a.getImageUrl()).collect(Collectors.toList()));
                }
                String createDate = DateTimeUtils.convertDateToStringOrEmpty(ratingEntity.getCreatedDate(), DateTimeUtils.YYYYMMDDhhmm);
                commentDomain.setCreatedDate(createDate);
                commentDomain.setUserAppName(userAppEntity.getUsername());
                commentDomain.setRating(StringUtils.convertObjectToString(ratingEntity.getRating()));
                commentDomain.setComment(ratingEntity.getComment());
                commentDomain.setLikeNumber(ratingEntity.getLikeNumber() == null ? "0" : StringUtils.convertObjectToString(ratingEntity.getLikeNumber()));
                commentDomain.setDislikeNumber(ratingEntity.getDislikeNumber() == null ? "0" : StringUtils.convertObjectToString(ratingEntity.getDislikeNumber()));
                return commentDomain;
            }).collect(Collectors.toList());
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
            foodDomain.setIsBestSeller(t.getIsBestSeller());
            foodDomain.setTotalBuy(t.getTotalBuy());
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
                        foodDomain.setIsBestSeller(t.getIsBestSeller());
                        foodDomain.setTotalBuy(t.getTotalBuy());
                        foodDomain.setDiscountPercent(t.getDiscountPercent());
                        foodDomain.setOriginalPrice(t.getOriginalPrice());
                        foodDomain.setPrice(StringUtils.convertObjectToString(t.getPrice()));
                        foodDomain.setDistance(distance);
                        return foodDomain;
                    })
                    .collect(Collectors.toList()));
            if (!CollectionUtils.isEmpty(domain.getListFood())){
                listResult.add(domain);
            }
        }
        storeDetailDomain.setListSubFoodType(listResult);
        return storeDetailDomain;
    }

    public ResponseDataAPI createNewStoreAdmin(CreateNewStoreDomain domain) {
        ResponseDataAPI responseDataAPI = new ResponseDataAPI();
        List<String> listStoreName = storeRepository.findAll().stream().map(t -> t.getName()).collect(Collectors.toList());
        if (listStoreName.contains(domain.getName())){
            throw new CustomException("Tên của cửa hàng đã bị trùng, vui lòng chọn tên khác !", "Tên của cửa hàng đã bị trùng, vui lòng chọn tên khác !", HttpStatus.BAD_REQUEST);
        }

        Map<String, Long> listConvertFoodType = new HashMap<>();
        listConvertFoodType.put("Cơm", 1l);
        listConvertFoodType.put("Bún/phở", 2l);
        listConvertFoodType.put("Đồ ăn vặt/ăn nhanh", 3l);
        listConvertFoodType.put("Đặt sản", 4l);
        listConvertFoodType.put("Healthy", 5l);
        listConvertFoodType.put("Đồ uống", 6l);
        listConvertFoodType.put("Khác", 7l);



        StoreEntity storeEntity = new StoreEntity();
        storeEntity.setName(domain.getName());
        storeEntity.setAddress(domain.getAddressSave());
        storeEntity.setPhone(domain.getPhone());
        storeEntity.setAvatar(domain.getAvatar());
        storeEntity.setLatitude(StringUtils.convertStringToDoubleOrNull(domain.getLat()));
        storeEntity.setLongitude(StringUtils.convertStringToDoubleOrNull(domain.getLng()));
        storeEntity.setOpenTime(domain.getOpentime());
        storeEntity.setPriceRange(domain.getPricerange());

        storeEntity = storeRepository.save(storeEntity);

        List<String> listSubFoodTypeName = domain.getListSubFood().stream().distinct().collect(Collectors.toList());

        List<SubFoodTypeEntity> listSubFoodType = new ArrayList<>();
        for (String subTypeName : listSubFoodTypeName){
            SubFoodTypeEntity subFoodTypeEntity = new SubFoodTypeEntity();
            subFoodTypeEntity.setStoreId(storeEntity.getId());
            subFoodTypeEntity.setName(subTypeName);
            subFoodTypeEntity.setParentId(null);
            listSubFoodType.add(subFoodTypeEntity);
        }

        subFoodTypeRepository.saveAll(listSubFoodType);

        List<FoodEntity> listFood = new ArrayList<>();
        for (CreateNewFoodDomain createNewFoodDomain : domain.getListNewFood()){
            FoodEntity foodEntity = new FoodEntity();
            foodEntity.setName(createNewFoodDomain.getName());
            foodEntity.setFoodTypeId(listConvertFoodType.get(createNewFoodDomain.getFoodType()));
            SubFoodTypeEntity subFoodTypeEntity = subFoodTypeRepository.findByStoreIdAndName(storeEntity.getId(), createNewFoodDomain.getSubFoodType());
            if (subFoodTypeEntity == null){
                throw new CustomException("Sub food type ko tim thay", "Sub food type ko tim thay", HttpStatus.BAD_REQUEST);
            }
            foodEntity.setSubFoodTypeId(subFoodTypeEntity.getId());
            foodEntity.setOriginalPrice(StringUtils.convertStringToLongOrNull(createNewFoodDomain.getPrice()));
            foodEntity.setDiscountPercent(StringUtils.convertStringToIntegerOrNull(createNewFoodDomain.getDiscountPercent()));

            if (foodEntity.getDiscountPercent() == null){
                foodEntity.setDiscountPercent(0);
            }

            Double priceDouble = Math.ceil( (double) foodEntity.getOriginalPrice() / 100 * (100 - foodEntity.getDiscountPercent()) / 1000);
            Long price = priceDouble.longValue() * 1000;

            foodEntity.setPrice(price);
            foodEntity.setAvatar(createNewFoodDomain.getAvatar());
            foodEntity.setStoreId(storeEntity.getId());
            foodEntity.setIsBestSeller(0);
            foodEntity.setTotalBuy(0);

            if (foodEntity.getDiscountPercent().equals(0)){
                foodEntity.setDiscountPercent(null);
            }

            listFood.add(foodEntity);
        }

        foodRepository.saveAll(listFood);
        return responseDataAPI;
    }

    public ResponseDataAPI getAllStoreAdmin(String valueSearch, Integer offset) {
        Sort sort = Sort.by(Sort.Order.desc("id"));
        Pageable pageable = PageRequest.of(offset, 8, sort);

        String searchValue = valueSearch.trim().toLowerCase(Locale.ROOT);

        Page<StoreEntity> pageAllStore = storeRepository.findStoreBySearchValue(searchValue, pageable);

        List<StoreDomain> listResult = pageAllStore.stream().map(storeEntity -> {
            StoreDomain domain = new StoreDomain();
            domain.setId(StringUtils.convertObjectToString(storeEntity.getId()));
            domain.setAvatar(storeEntity.getAvatar());
            domain.setPhone(storeEntity.getPhone());
            domain.setOpenTime(storeEntity.getOpenTime());
            domain.setSummaryRating(storeEntity.getSummaryRating());
            domain.setPriceRange(storeEntity.getPriceRange());
            domain.setAddress(storeEntity.getAddress());
            domain.setName(storeEntity.getName());
            //set number of rating
            domain.setNumberOfRating(null);
            return domain;
        }).collect(Collectors.toList());

        ResponseDataAPI responseDataAPI = new ResponseDataAPI();
        responseDataAPI.setTotalRows(pageAllStore.getTotalElements());
        responseDataAPI.setData(listResult);

        return responseDataAPI;
    }

    public ResponseDataAPI getInfoOfStoreAdmin(String storeIdStr) {
        ResponseDataAPI responseDataAPI = new ResponseDataAPI();
        Long storeId = StringUtils.convertObjectToLongOrNull(storeIdStr);
        if (storeId == null){
            throw new CustomException("store id bi sai", "store id bi sai", HttpStatus.BAD_REQUEST);
        }
        StoreEntity storeEntity = storeRepository.findById(storeId).orElse(null);
        if (storeEntity == null){
            throw new CustomException("store bi sai", "store bi sai", HttpStatus.BAD_REQUEST);
        }
        EditStoreDomain domain = new EditStoreDomain();
        domain.setId(StringUtils.convertObjectToString(storeEntity.getId()));
        domain.setName(storeEntity.getName());
        domain.setPhone(storeEntity.getPhone());
        domain.setOpentime(storeEntity.getOpenTime());
        domain.setPricerange(storeEntity.getPriceRange());
        domain.setAvatar(storeEntity.getAvatar());
        domain.setLat(StringUtils.convertObjectToString(storeEntity.getLatitude()));
        domain.setLng(StringUtils.convertObjectToString(storeEntity.getLongitude()));
        domain.setAddressSave(storeEntity.getAddress());

        List<SubFoodTypeEntity> listSubFood = subFoodTypeRepository.findAllByStoreId(storeId);

        domain.setListSubFood(listSubFood.stream().map(t -> t.getName()).collect(Collectors.toList()));

        responseDataAPI.setData(domain);
        return responseDataAPI;
    }

    public ResponseDataAPI getAllFoodOfStoreAdmin(String storeIdStr, Integer offset, String valueSearch) {
        ResponseDataAPI responseDataAPI = new ResponseDataAPI();
        Long storeId = StringUtils.convertObjectToLongOrNull(storeIdStr);
        if (storeId == null){
            throw new CustomException("store id bi sai", "store id bi sai", HttpStatus.BAD_REQUEST);
        }
        StoreEntity storeEntity = storeRepository.findById(storeId).orElse(null);
        if (storeEntity == null){
            throw new CustomException("store bi sai", "store bi sai", HttpStatus.BAD_REQUEST);
        }

        Sort sort = Sort.by(Sort.Order.desc("id"));
        Pageable pageable = PageRequest.of(offset, 8, sort);

        String searchValue = valueSearch.trim().toLowerCase(Locale.ROOT);

        Map<Long, String> listConvertFoodType = new HashMap<>();
        listConvertFoodType.put(1l, "Cơm");
        listConvertFoodType.put(2l, "Bún/phở");
        listConvertFoodType.put(3l, "Đồ ăn vặt/ăn nhanh");
        listConvertFoodType.put(4l, "Đặt sản");
        listConvertFoodType.put(5l, "Healthy");
        listConvertFoodType.put(6l, "Đồ uống");
        listConvertFoodType.put(7l, "Khác");

        Page<FoodEntity> listFoodOfStore = foodRepository.getAllFoodOfStoreAdmin(storeId, searchValue, pageable);
        List<EditFoodDomain> listResult = new ArrayList<>();
        if (listFoodOfStore.getTotalElements() != 0l){
            listResult = listFoodOfStore.stream().map(foodEntity -> {
                EditFoodDomain domain = new EditFoodDomain();
                domain.setId(StringUtils.convertObjectToString(foodEntity.getId()));
                domain.setName(foodEntity.getName());
                domain.setFoodType(listConvertFoodType.get(foodEntity.getFoodTypeId()));
                SubFoodTypeEntity subFoodTypeEntity = subFoodTypeRepository.findById(foodEntity.getSubFoodTypeId()).orElse(null);
                if (subFoodTypeEntity == null){
                    throw new CustomException("sub food typpe ko ton tai", "sub food typpe ko ton tai", HttpStatus.BAD_REQUEST);
                }
                domain.setSubFoodType(subFoodTypeEntity.getName());
                domain.setPrice(StringUtils.convertObjectToString(foodEntity.getOriginalPrice()));
                domain.setDiscountPercent(StringUtils.convertObjectToString(foodEntity.getDiscountPercent()) == null ? "0" : StringUtils.convertObjectToString(foodEntity.getDiscountPercent()));
                domain.setAvatar(foodEntity.getAvatar());
                return domain;
            }).collect(Collectors.toList());
        }

        responseDataAPI.setData(listResult);
        responseDataAPI.setTotalRows(listFoodOfStore.getTotalElements());
        return responseDataAPI;
    }

    public void editInfoStore(EditStoreDomain domain) {
        Long storeId = StringUtils.convertObjectToLongOrNull(domain.getId());
        if (storeId == null){
            throw new CustomException("Store id bi sai", "Store id bi sai", HttpStatus.BAD_REQUEST);
        }
        StoreEntity storeEntity = storeRepository.findById(storeId).orElse(null);
        if (storeEntity == null){
            throw new CustomException("Store ko ton tai", "Store ko ton tai", HttpStatus.BAD_REQUEST);
        }
        List<String> listStoreName = storeRepository.findAll().stream().filter(t -> !t.getId().equals(storeId)).map(t -> t.getName()).collect(Collectors.toList());
        if (listStoreName.contains(domain.getName())){
            throw new CustomException("Tên của cửa hàng đã bị trùng, vui lòng chọn tên khác !", "Tên của cửa hàng đã bị trùng, vui lòng chọn tên khác !", HttpStatus.BAD_REQUEST);
        }

        storeEntity.setName(domain.getName());
        storeEntity.setAddress(domain.getAddressSave());
        storeEntity.setPhone(domain.getPhone());
        storeEntity.setAvatar(domain.getAvatar());
        storeEntity.setLatitude(StringUtils.convertStringToDoubleOrNull(domain.getLat()));
        storeEntity.setLongitude(StringUtils.convertStringToDoubleOrNull(domain.getLng()));
        storeEntity.setOpenTime(domain.getOpentime());
        storeEntity.setPriceRange(domain.getPricerange());
        if (!CollectionUtils.isEmpty(domain.getListSubFood())){
            List<String> listSubFoodTypeName = domain.getListSubFood().stream().distinct().collect(Collectors.toList());

            List<SubFoodTypeEntity> listSubFoodType = new ArrayList<>();
            for (String subTypeName : listSubFoodTypeName){
                SubFoodTypeEntity subFoodTypeEntity = new SubFoodTypeEntity();
                subFoodTypeEntity.setStoreId(storeEntity.getId());
                subFoodTypeEntity.setName(subTypeName);
                subFoodTypeEntity.setParentId(null);
                listSubFoodType.add(subFoodTypeEntity);
            }

            subFoodTypeRepository.saveAll(listSubFoodType);
        }

        storeRepository.save(storeEntity);
    }

    public ResponseDataAPI createNewFoodEditStoreDomain(CreateNewFoodEditStoreDomain createNewFoodDomain) {
        Long storeId = StringUtils.convertStringToLongOrNull(createNewFoodDomain.getStoreId());
        if (storeId == null){
            throw new CustomException("Store id bi sai", "Store id bi sai", HttpStatus.BAD_REQUEST);
        }
        StoreEntity storeEntity = storeRepository.findById(storeId).orElse(null);
        if (storeEntity == null){
            throw new CustomException("Store ko ton tai", "Store ko ton tai", HttpStatus.BAD_REQUEST);
        }
        List<FoodEntity> listFoodOfStore = foodRepository.findAll();
        List<String> listFoodNameOfStore = new ArrayList<>();
        if (!CollectionUtils.isEmpty(listFoodOfStore)){
            listFoodNameOfStore = listFoodOfStore.stream().map(t -> t.getName()).collect(Collectors.toList());
        }
        if (listFoodNameOfStore.contains(createNewFoodDomain.getName())){
            throw new CustomException("Ten mon an da bi trung", "Ten mon an da bi trung", HttpStatus.BAD_REQUEST);
        }

        Map<String, Long> listConvertFoodType = new HashMap<>();
        listConvertFoodType.put("Cơm", 1l);
        listConvertFoodType.put("Bún/phở", 2l);
        listConvertFoodType.put("Đồ ăn vặt/ăn nhanh", 3l);
        listConvertFoodType.put("Đặt sản", 4l);
        listConvertFoodType.put("Healthy", 5l);
        listConvertFoodType.put("Đồ uống", 6l);
        listConvertFoodType.put("Khác", 7l);

        FoodEntity foodEntity = new FoodEntity();
        foodEntity.setName(createNewFoodDomain.getName());
        foodEntity.setFoodTypeId(listConvertFoodType.get(createNewFoodDomain.getFoodType()));
        SubFoodTypeEntity subFoodTypeEntity = subFoodTypeRepository.findByStoreIdAndName(storeEntity.getId(), createNewFoodDomain.getSubFoodType());
        if (subFoodTypeEntity == null){
            throw new CustomException("Sub food type ko tim thay", "Sub food type ko tim thay", HttpStatus.BAD_REQUEST);
        }
        foodEntity.setSubFoodTypeId(subFoodTypeEntity.getId());
        foodEntity.setOriginalPrice(StringUtils.convertStringToLongOrNull(createNewFoodDomain.getPrice()));
        foodEntity.setDiscountPercent(StringUtils.convertStringToIntegerOrNull(createNewFoodDomain.getDiscountPercent()));

        if (foodEntity.getDiscountPercent() == null){
            foodEntity.setDiscountPercent(0);
        }

        Double priceDouble = Math.ceil( (double) foodEntity.getOriginalPrice() / 100 * (100 - foodEntity.getDiscountPercent()) / 1000);
        Long price = priceDouble.longValue() * 1000;

        foodEntity.setPrice(price);
        foodEntity.setAvatar(createNewFoodDomain.getAvatar());
        foodEntity.setStoreId(storeEntity.getId());
        foodEntity.setIsBestSeller(0);
        foodEntity.setTotalBuy(0);
        if (foodEntity.getDiscountPercent().equals(0)){
            foodEntity.setDiscountPercent(null);
        }

        foodRepository.save(foodEntity);

        // return list food

        return getAllFoodOfStoreAdmin(createNewFoodDomain.getStoreId(), 0, "");
    }

    public ResponseDataAPI editFoodOfStore(EditFoodDomain domain) {
        Long foodId = StringUtils.convertObjectToLongOrNull(domain.getId());
        if (foodId == null){
            throw new CustomException("food id bi sai", "food id bi sai", HttpStatus.BAD_REQUEST);
        }

        FoodEntity foodEntity = foodRepository.findById(foodId).orElse(null);
        if (foodEntity == null){
            throw new CustomException("food id bi sai", "food id bi sai", HttpStatus.BAD_REQUEST);
        }

        StoreEntity storeEntity = storeRepository.findById(foodEntity.getStoreId()).orElse(null);
        if (storeEntity == null){
            throw new CustomException("store bi sai", "store bi sai", HttpStatus.BAD_REQUEST);
        }

        Map<String, Long> listConvertFoodType = new HashMap<>();
        listConvertFoodType.put("Cơm", 1l);
        listConvertFoodType.put("Bún/phở", 2l);
        listConvertFoodType.put("Đồ ăn vặt/ăn nhanh", 3l);
        listConvertFoodType.put("Đặt sản", 4l);
        listConvertFoodType.put("Healthy", 5l);
        listConvertFoodType.put("Đồ uống", 6l);
        listConvertFoodType.put("Khác", 7l);

        List<String> listFoodNameOfStore = foodRepository.findAllByStoreId(storeEntity.getId()).stream().filter(t -> !t.getId().equals(foodId)).map(t -> t.getName()).collect(Collectors.toList());
        if (listFoodNameOfStore.contains(domain.getName())){
            throw new CustomException("Tên của thức ăn đã bị trùng, vui lòng chọn tên khác !", "Tên của thức ăn đã bị trùng, vui lòng chọn tên khác !", HttpStatus.BAD_REQUEST);
        }

        foodEntity.setName(domain.getName());
        foodEntity.setFoodTypeId(listConvertFoodType.get(domain.getFoodType()));
        SubFoodTypeEntity subFoodTypeEntity = subFoodTypeRepository.findByStoreIdAndName(storeEntity.getId(), domain.getSubFoodType());
        if (subFoodTypeEntity == null){
            throw new CustomException("Sub food type ko tim thay", "Sub food type ko tim thay", HttpStatus.BAD_REQUEST);
        }
        foodEntity.setSubFoodTypeId(subFoodTypeEntity.getId());
        foodEntity.setOriginalPrice(StringUtils.convertStringToLongOrNull(domain.getPrice()));
        foodEntity.setDiscountPercent(StringUtils.convertStringToIntegerOrNull(domain.getDiscountPercent()));

        if (foodEntity.getDiscountPercent() == null){
            foodEntity.setDiscountPercent(0);
        }

        Double priceDouble = Math.ceil( (double) foodEntity.getOriginalPrice() / 100 * (100 - foodEntity.getDiscountPercent()) / 1000);
        Long price = priceDouble.longValue() * 1000;

        foodEntity.setPrice(price);
        foodEntity.setAvatar(domain.getAvatar());
        foodEntity.setStoreId(storeEntity.getId());

        if (foodEntity.getDiscountPercent().equals(0)){
            foodEntity.setDiscountPercent(null);
        }

        foodRepository.save(foodEntity);

        return getAllFoodOfStoreAdmin(storeEntity.getId().toString(), 0, "");
    }

    @Transactional
    public ResponseDataAPI deleteFoodOfStore(String foodIdStr) {
        Long foodId = StringUtils.convertStringToLongOrNull(foodIdStr);
        if (foodId == null){
            throw new CustomException("food id bi sai", "food id bi sai", HttpStatus.BAD_REQUEST);
        }
        FoodEntity foodEntity = foodRepository.findById(foodId).or(() -> {throw new CustomException("food id bi sai", "food id bi sai", HttpStatus.BAD_REQUEST);}).orElse(null);
        StoreEntity storeEntity = storeRepository.findById(foodEntity.getStoreId()).orElse(null);

        if (storeEntity == null){
            throw new CustomException("store id bi sai", "store id bi sai", HttpStatus.BAD_REQUEST);
        }
        cartRepository.deleteAllCartByFoodId(foodId);
        favouriteRepository.deleteAllFavouriteByFoodId(foodId);
        ratingRepository.deleteAllRatingByFoodId(foodId);
//        transactionItemRepository.deleteAllByFoodId(foodId);
        foodEntity.setIsDeleted(1);
        foodRepository.save(foodEntity);

        return getAllFoodOfStoreAdmin(storeEntity.getId().toString(), 0, "");
    }
}

