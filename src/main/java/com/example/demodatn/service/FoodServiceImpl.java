package com.example.demodatn.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.demodatn.constant.*;
import com.example.demodatn.constant.Error;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
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

    @Autowired
    private MetadataRepository metadataRepository;

    @Autowired
    private FormatRatingUtils formatRatingUtils;

    @Autowired
    private TransactionItemRepository transactionItemRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private RatingImageRepository ratingImageRepository;

    public ResponseDataAPI getListFoodByFoodType(String userApp, String foodTypeStr, BasicRequest request){
        Long userAppId = StringUtils.convertStringToLongOrNull(userApp);
        ResponseDataAPI responseDataAPI = new ResponseDataAPI();
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
        Page<FoodEntity> foodEntitiesOrg = foodRepository.getListFoodByFoodType(foodType, searchValue , pageable);
        List<FoodDomain> responseList = new ArrayList<>();
        responseDataAPI.setTotalRows(foodEntitiesOrg.getTotalElements());
            responseList = foodEntitiesOrg.stream().map(t -> {
                        FoodDomain domain = new FoodDomain();
                        domain.setId(StringUtils.convertObjectToString(t.getId()));
                        domain.setName(t.getName());
                        domain.setFoodTypeId(StringUtils.convertObjectToString(t.getFoodTypeId()));
                        domain.setStoreId(StringUtils.convertObjectToString(t.getStoreId()));
                        domain.setStoreName(storeRepository.findById(t.getStoreId()).orElse(null).getName());
                        domain.setSummaryRating(StringUtils.convertObjectToString(t.getSummaryRating()));
                        domain.setAvatar(t.getAvatar());
                        domain.setIsBestSeller(t.getIsBestSeller());
                        domain.setTotalBuy(t.getTotalBuy());
                        domain.setPrice(StringUtils.convertObjectToString(t.getPrice()));
                        domain.setDiscountPercent(t.getDiscountPercent());
                        domain.setOriginalPrice(t.getOriginalPrice());
                        domain.setDistance(distanceMap.get(t.getStoreId()));
                        return domain;
                    }
            ).collect(Collectors.toList());

        responseDataAPI.setData(responseList);
        return responseDataAPI;
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
        StoreEntity storeEntity = storeRepository.findById(t.getStoreId()).orElse(null);
        String cartNote = cartEntity == null ? "" : cartEntity.getNote();
        FoodWithCommentDomain domain = new FoodWithCommentDomain();
        domain.setFoodId(StringUtils.convertObjectToString(t.getId()));
        domain.setFoodName(t.getName());
        domain.setLikeNumber(t.getLikeNumber() == null ? 0 : t.getLikeNumber());
        domain.setFoodTypeId(StringUtils.convertObjectToString(t.getFoodTypeId()));
        domain.setStoreId(StringUtils.convertObjectToString(t.getStoreId()));
        domain.setStoreName(storeEntity.getName());
        domain.setSummaryRating(t.getSummaryRating() == null ? "0" : StringUtils.convertObjectToString(t.getSummaryRating()));
        domain.setAvatar(t.getAvatar());
        domain.setPrice(StringUtils.convertObjectToString(t.getPrice()));
        domain.setDiscountPercent(t.getDiscountPercent());
        domain.setOriginalPrice(t.getOriginalPrice());
        domain.setStoreAddress(storeEntity.getAddress());
        domain.setIsBestSeller(t.getIsBestSeller());
        domain.setTotalBuy(t.getTotalBuy());
        domain.setDistance(calculateDistanceUtils.getDistanceOfOnlyOneStore(userAppId, t.getStoreId()));
        FavouriteEntity favouriteEntity = favouriteRepository.findByUserAppIdAndItemIdAndType(userAppId, foodId, FavouriteType.FOOD.getValue());
        if (favouriteEntity != null){
            domain.setIsFavourite(1);
        } else {
            domain.setIsFavourite(0);
        }
        domain.setNote(cartNote);

        //get list recommend and same food
        domain.setListRecommendSameFood(this.geListRecommendFoodDetail(userApp, foodId));

        List<RatingEntity> listRatingIds = ratingRepository.findAllByFoodId(foodId);
        List<CommentDomain> listComments = new ArrayList<>();
        domain.setNumberOfVote(StringUtils.convertObjectToString(listRatingIds.size()));
        if (!CollectionUtils.isEmpty(listRatingIds)){
            domain.setNumberOfVote(StringUtils.convertObjectToString(listRatingIds.size()));
            listComments = listRatingIds.stream().filter(entity -> !StringUtils.isEmpty(entity.getComment()))
                    .sorted((t1, t2) -> t2.getCreatedDate().compareTo(t1.getCreatedDate()))
                    .map(ratingEntity -> {
                CommentDomain commentDomain = new CommentDomain();
                UserAppEntity userAppEntity = userAppRepository.getById(ratingEntity.getUserAppId());
                List<FoodEntity> listLikeFood = foodRepository.getListLikeFoodComment(userAppEntity.getId(), storeEntity.getId());
                if (!CollectionUtils.isEmpty(listLikeFood)){
                    commentDomain.setListLikeFood(
                            listLikeFood.stream().filter(ff -> !foodId.equals(ff.getId()))
                            .limit(3)
                            .map(aa -> {
                                FoodCommentLikeDomain likeDomain = new FoodCommentLikeDomain();
                                likeDomain.setFoodId(aa.getId());
                                likeDomain.setFoodName(aa.getName());
                                return likeDomain;
                            }).collect(Collectors.toList())
                    );
                } else {
                    commentDomain.setListLikeFood(new ArrayList<>());
                }
                commentDomain.setId(StringUtils.convertObjectToString(ratingEntity.getId()));
                commentDomain.setUserAppName(userAppEntity.getUsername());
                commentDomain.setRating(StringUtils.convertObjectToString(ratingEntity.getRating()));
                commentDomain.setComment(ratingEntity.getComment());
                commentDomain.setUserAvatar(userAppEntity.getAvatar());
                String createDate = DateTimeUtils.convertDateToStringOrEmpty(ratingEntity.getCreatedDate(), DateTimeUtils.YYYYMMDDhhmm);
                commentDomain.setCreatedDate(createDate);
                List<RatingImageEntity> listImageRating = ratingImageRepository.findAllByRatingId(ratingEntity.getId());
                if (CollectionUtils.isEmpty(listImageRating)){
                    commentDomain.setListImage(new ArrayList<>());
                } else {
                    commentDomain.setListImage(listImageRating.stream().map(a -> a.getImageUrl()).collect(Collectors.toList()));
                }
                commentDomain.setLikeNumber(ratingEntity.getLikeNumber() == null ? "0" : StringUtils.convertObjectToString(ratingEntity.getLikeNumber()));
                commentDomain.setDislikeNumber(ratingEntity.getDislikeNumber() == null ? "0" : StringUtils.convertObjectToString(ratingEntity.getDislikeNumber()));
                return commentDomain;
            }).collect(Collectors.toList());
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
                        foodDomain.setIsBestSeller(t.getIsBestSeller());
                        foodDomain.setTotalBuy(t.getTotalBuy());
                        foodDomain.setDistance(distance);
                        return foodDomain;
                    })
                    .collect(Collectors.toList()));

            if (!CollectionUtils.isEmpty(domain.getListFood())){
                listResult.add(domain);
            }
        }
        return listResult;
    }

    public ResponseDataAPI getAllBySearchValue(String valueSearchOriginal, String typeSearchStr, Integer offset, String userApp){
        ResponseDataAPI responseDataAPI = new ResponseDataAPI();
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
            responseDataAPI.setTotalRows(listStore.getTotalElements());
            if (listStore.getTotalElements() != 0){
                List<StoreDomain> listStoreResult = listStore.stream().map(t -> {
                    StoreDomain storeDomain = new StoreDomain();
                    storeDomain.setId(StringUtils.convertObjectToString(t.getId()));
                    storeDomain.setName(t.getName());
                    storeDomain.setAvatar(t.getAvatar());
                    storeDomain.setAddress(t.getAddress().substring(0, t.getAddress().length() - 26));
                    storeDomain.setPhone(t.getPhone());
                    storeDomain.setSummaryRating(formatRatingUtils.formatRatingOneNumber(t.getSummaryRating()));
                    List<RatingEntity> listRatingIds = ratingRepository.getListRatingOfStore(t.getId());
                    storeDomain.setNumberOfRating(listRatingIds.size());
                    storeDomain.setDistance(calculateDistanceUtils.getDistanceOfOnlyOneStore(userAppId, t.getId()));
                    return storeDomain;
                }).collect(Collectors.toList());
                responseDataAPI.setData(listStoreResult);
            } else {
                responseDataAPI.setData(new ArrayList<>());
            }
        } else {
            Page<FoodEntity> listFood = foodRepository.findFoodBySearchValue(valueSearch, pageable);
            responseDataAPI.setTotalRows(listFood.getTotalElements());
            if (listFood.getTotalElements() != 0){
                List<FoodDomain> listFoodResult = listFood.stream().map(t -> {
                    FoodDomain domain = new FoodDomain();
                    domain.setId(StringUtils.convertObjectToString(t.getId()));
                    domain.setName(t.getName());
                    domain.setFoodTypeId(StringUtils.convertObjectToString(t.getFoodTypeId()));
                    domain.setStoreId(StringUtils.convertObjectToString(t.getStoreId()));
                    domain.setStoreName(storeRepository.findById(t.getStoreId()).orElse(null).getName());
                    domain.setSummaryRating(StringUtils.convertObjectToString(formatRatingUtils.formatRatingOneNumber(t.getSummaryRating())));
                    domain.setAvatar(t.getAvatar());
                    domain.setIsBestSeller(t.getIsBestSeller());
                    domain.setTotalBuy(t.getTotalBuy());
                    domain.setPrice(StringUtils.convertObjectToString(t.getPrice()));
                    domain.setDiscountPercent(t.getDiscountPercent());
                    domain.setOriginalPrice(t.getOriginalPrice());
                    domain.setDistance(distanceMap.get(t.getStoreId()));
                    return domain;
                }).collect(Collectors.toList());
                responseDataAPI.setData(listFoodResult);
            } else {
                responseDataAPI.setData(new ArrayList<>());
            }
        }
        return responseDataAPI;
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
            if (storeEntity.getSummaryRating() == null){
                domain.setSummaryRating(0.0);
            } else {
                domain.setSummaryRating(formatRatingUtils.formatRatingOneNumber(storeEntity.getSummaryRating()));
            }
            List<RatingEntity> listRatingIds = ratingRepository.getListRatingOfStore(storeEntity.getId());
            domain.setNumberOfRating(listRatingIds.size());
            listResult.add(domain);
        }

        listResult.sort((t1, t2) -> t1.getDistance().compareTo(t2.getDistance()));
        return listResult;
    }

    public String addNewRatingForFood(String food, AddRatingDomain domain, MultipartFile[] files) {
        String success = "";
        Long foodId = StringUtils.convertStringToLongOrNull(food);
        Long userAppId = StringUtils.convertStringToLongOrNull(domain.getUserAppId());
        Long rating = StringUtils.convertStringToLongOrNull(domain.getRating());

        if (foodId == null || userAppId == null){
            throw new CustomException(Error.PARAMETER_INVALID.getMessage()
                    , Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }

        FoodEntity foodEntity = foodRepository.findById(foodId).orElse(null);
        if (foodEntity == null){
            throw new CustomException("Món ăn không tồn tại"
                    , "Món ăn không tồn tại", HttpStatus.BAD_REQUEST);
        }

        RatingEntity ratingEntity = new RatingEntity();
        ratingEntity.setUserAppId(userAppId);
        ratingEntity.setFoodId(foodId);
        ratingEntity.setRating(rating.equals(0L) ? null : rating);
        ratingEntity.setComment(domain.getComment());
        ratingEntity.setDislikeNumber(0l);
        ratingEntity.setLikeNumber(0l);

        ratingEntity = ratingRepository.save(ratingEntity);

        if (files != null){
            Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                    "cloud_name", "djifhw3lo",
                    "api_key", "992726224781494",
                    "api_secret", "Tol4roEhAhgOJ3NaNsnAyWDDrD0",
                    "secure", true));
            List<RatingImageEntity> listRatingImage = new ArrayList<>();
            RatingEntity finalRatingEntity = ratingEntity;
            Arrays.asList(files).stream().forEach(file -> {
                Path filepath = Path.of("imageupload.jpg");

                String imageUrl = "";
                try (OutputStream os = Files.newOutputStream(filepath)) {
                    os.write(file.getBytes());
                    Map uploadResult = cloudinary.uploader().upload(new File("imageupload.jpg"), ObjectUtils.emptyMap());
                    System.out.println("upload moi : " + uploadResult.get("url"));
                    imageUrl = (String) uploadResult.get("url");
                    RatingImageEntity ratingImageEntity = new RatingImageEntity();
                    ratingImageEntity.setRatingId(finalRatingEntity.getId());
                    ratingImageEntity.setImageUrl(imageUrl);
                    listRatingImage.add(ratingImageEntity);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            ratingImageRepository.saveAll(listRatingImage);
        }
        success = "success";

        List<RatingEntity> listRatingOfFood = ratingRepository.findAllByFoodId(foodId);
        if (!CollectionUtils.isEmpty(listRatingOfFood)) {
            Double totalRating = listRatingOfFood.stream().map(t -> t.getRating()).reduce(0l, (t1, t2) -> t1 + t2).doubleValue()/ listRatingOfFood.size();
            foodEntity.setSummaryRating(Math.round(totalRating*1000.0)/1000.0);
            foodRepository.save(foodEntity);
        }
        return success;
    }


    public List<FoodDomain> geListRecommendFood(String userApp){
        List<FoodDomain> result = new ArrayList<>();
        Long userAppId = StringUtils.convertStringToLongOrNull(userApp);
        if (userAppId == null){
            throw new CustomException(Error.PARAMETER_INVALID.getMessage()
                    , Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }
        UserAppEntity userAppEntity = userAppRepository.findById(userAppId).orElse(null);
        if (userAppEntity == null) {
            throw new CustomException(Error.PARAMETER_INVALID.getMessage()
                    , Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }
        Map<Long, Double> mapDistance = calculateDistanceUtils.getDistanceOfAllStores(userAppEntity);
        try {
            String linkData = metadataRepository.findByIdAndType(1l, MetadataType.DATA_RECOMMEND_FILE.getValue()).getValue();
            URL url = new URL(linkData);
            Scanner s = new Scanner(url.openStream());
            List<String> listLine = new ArrayList<>();
            while (s.hasNextLine()){
                listLine.add(s.nextLine());
            }

            for (String line : listLine){
                String[] listItem =line.split("\\|");
                Long userId = StringUtils.convertObjectToLongOrNull(listItem[0].substring(4));
                if (userAppId.equals(userId)){
                    List<String> listFoodStr = new ArrayList<>(Arrays.asList(listItem));
                    listFoodStr.remove(0);
                    List<Long> listFoodId = listFoodStr.stream()
                            .map(t -> StringUtils.convertStringToLongOrNull(t))
                            .filter(t -> {
                                FoodEntity foodEntity = foodRepository.findById(t).orElse(null);
                                if (foodEntity == null || foodEntity.getFoodTypeId().equals(7l)){
                                    return false;
                                } else {
                                    return true;
                                }
                            })
                            .collect(Collectors.toList());
                    List<Long> listRatingFoodOfUser = ratingRepository.findAllByUserAppId(userAppId) // list nay ko co lien quan toi food bi xoa
                            .stream().map(t -> t.getFoodId())
                            .distinct()
                            .collect(Collectors.toList());
                    List<Long> listWillShowFoodId = listFoodId.stream().filter(t -> !listRatingFoodOfUser.contains(t))
                            .limit(16)
                            .collect(Collectors.toList());

                    System.out.println("Chieu dai list : " + listFoodId.size());

                    //fill missing number
                    if (listWillShowFoodId.size() < 16){
                        int countListLength = listWillShowFoodId.size();
                            for (Long foodId : listFoodId){
                                if (countListLength == 16){
                                    break;
                                }
                                if (!listWillShowFoodId.contains(foodId)){
                                    FoodEntity foodEntity = foodRepository.findById(foodId).orElse(null);
                                    if (!foodEntity.getFoodTypeId().equals(7l)){
                                        listWillShowFoodId.add(foodId);
                                        countListLength ++;
                                    }
                                }
                            }

                    }

                    if (!CollectionUtils.isEmpty(listWillShowFoodId)){
                        for (Long foodId : listWillShowFoodId){
                            FoodEntity foodEntity = foodRepository.findById(foodId).orElse(null);
                            if (foodEntity == null){
                                throw new CustomException(Error.PARAMETER_INVALID.getMessage()
                                        , Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
                            }
                            FoodDomain foodDomain = new FoodDomain();
                            foodDomain.setId(StringUtils.convertObjectToString(foodEntity.getId()));
                            foodDomain.setName(foodEntity.getName());
                            foodDomain.setFoodTypeId(StringUtils.convertObjectToString(foodEntity.getFoodTypeId()));
                            foodDomain.setStoreId(StringUtils.convertObjectToString(foodEntity.getStoreId()));
                            StoreEntity storeEntity = storeRepository.findById(foodEntity.getStoreId()).orElse(null);
                            if (storeEntity == null){
                                throw new CustomException(Error.PARAMETER_INVALID.getMessage()
                                        , Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
                            }
                            foodDomain.setStoreName(storeEntity.getName());
                            foodDomain.setSummaryRating(StringUtils.convertObjectToString(formatRatingUtils.formatRatingOneNumber(foodEntity.getSummaryRating())));
                            foodDomain.setAvatar(foodEntity.getAvatar());
                            foodDomain.setIsBestSeller(foodEntity.getIsBestSeller());
                            foodDomain.setTotalBuy(foodEntity.getTotalBuy());
                            foodDomain.setPrice(foodEntity.getPrice().toString());
                            FavouriteEntity favouriteEntity = favouriteRepository.findByUserAppIdAndItemIdAndType(userAppId, foodEntity.getId(), FavouriteType.FOOD.getValue());
                            if (favouriteEntity != null){
                                foodDomain.setIsFavourite("1");
                            } else {
                                foodDomain.setIsFavourite("0");
                            }

                            foodDomain.setDiscountPercent(foodEntity.getDiscountPercent());
                            foodDomain.setOriginalPrice(foodEntity.getOriginalPrice());
                            foodDomain.setDistance(mapDistance.get(storeEntity.getId()));
                            result.add(foodDomain);
                        }
                    }
                }
            }
            if (!CollectionUtils.isEmpty(result)){
                return result;
            } else {// user ko co trong file
                List<FoodEntity> listRecommendFood = foodRepository.findAll().stream()
                        .filter(t -> !t.getFoodTypeId().equals(7l))
                        .sorted(Comparator.comparing(FoodEntity::getSummaryRating, Comparator.nullsLast(Comparator.reverseOrder())))
                        .limit(16)
                        .collect(Collectors.toList());

                List<FoodDomain> listResult = new ArrayList<>();
                for (FoodEntity foodEntity : listRecommendFood){
                    FoodDomain foodDomain = new FoodDomain();
                    foodDomain.setId(StringUtils.convertObjectToString(foodEntity.getId()));
                    foodDomain.setName(foodEntity.getName());
                    foodDomain.setFoodTypeId(StringUtils.convertObjectToString(foodEntity.getFoodTypeId()));
                    foodDomain.setStoreId(StringUtils.convertObjectToString(foodEntity.getStoreId()));
                    StoreEntity storeEntity = storeRepository.findById(foodEntity.getStoreId()).orElse(null);
                    if (storeEntity == null){
                        throw new CustomException(Error.PARAMETER_INVALID.getMessage()
                                , Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
                    }
                    foodDomain.setStoreName(storeEntity.getName());
                    foodDomain.setSummaryRating(StringUtils.convertObjectToString(formatRatingUtils.formatRatingOneNumber(foodEntity.getSummaryRating())));
                    foodDomain.setAvatar(foodEntity.getAvatar());
                    foodDomain.setPrice(foodEntity.getPrice().toString());
                    FavouriteEntity favouriteEntity = favouriteRepository.findByUserAppIdAndItemIdAndType(userAppId, foodEntity.getId(), FavouriteType.FOOD.getValue());
                    if (favouriteEntity != null){
                        foodDomain.setIsFavourite("1");
                    } else {
                        foodDomain.setIsFavourite("0");
                    }

                    foodDomain.setDiscountPercent(foodEntity.getDiscountPercent());
                    foodDomain.setOriginalPrice(foodEntity.getOriginalPrice());
                    foodDomain.setDistance(mapDistance.get(storeEntity.getId()));
                    foodDomain.setIsBestSeller(foodEntity.getIsBestSeller());
                    foodDomain.setTotalBuy(foodEntity.getTotalBuy());
                    listResult.add(foodDomain);
                }
                return  listResult;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<FoodDomain> geListSaleFood(String userApp) {
        List<FoodDomain> result = new ArrayList<>();
        Long userAppId = StringUtils.convertStringToLongOrNull(userApp);
        if (userAppId == null){
            throw new CustomException(Error.PARAMETER_INVALID.getMessage()
                    , Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }
        UserAppEntity userAppEntity = userAppRepository.findById(userAppId).orElse(null);
        if (userAppEntity == null) {
            throw new CustomException(Error.PARAMETER_INVALID.getMessage()
                    , Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }
        Map<Long, Double> mapDistance = calculateDistanceUtils.getDistanceOfAllStores(userAppEntity);
        List<FoodDomain> listFood = foodRepository.getListFoodOnSale().stream().limit(16).map(
                foodEntity -> {
                    FoodDomain foodDomain = new FoodDomain();
                    foodDomain.setId(StringUtils.convertObjectToString(foodEntity.getId()));
                    foodDomain.setName(foodEntity.getName());
                    foodDomain.setFoodTypeId(StringUtils.convertObjectToString(foodEntity.getFoodTypeId()));
                    foodDomain.setStoreId(StringUtils.convertObjectToString(foodEntity.getStoreId()));
                    StoreEntity storeEntity = storeRepository.findById(foodEntity.getStoreId()).orElse(null);
                    if (storeEntity == null){
                        throw new CustomException(Error.PARAMETER_INVALID.getMessage()
                                , Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
                    }
                    foodDomain.setStoreName(storeEntity.getName());
                    foodDomain.setSummaryRating(StringUtils.convertObjectToString(formatRatingUtils.formatRatingOneNumber(foodEntity.getSummaryRating())));
                    foodDomain.setAvatar(foodEntity.getAvatar());
                    foodDomain.setPrice(foodEntity.getPrice().toString());
                    FavouriteEntity favouriteEntity = favouriteRepository.findByUserAppIdAndItemIdAndType(userAppId, foodEntity.getId(), FavouriteType.FOOD.getValue());
                    if (favouriteEntity != null){
                        foodDomain.setIsFavourite("1");
                    } else {
                        foodDomain.setIsFavourite("0");
                    }

                    foodDomain.setDiscountPercent(foodEntity.getDiscountPercent());
                    foodDomain.setOriginalPrice(foodEntity.getOriginalPrice());
                    foodDomain.setDistance(mapDistance.get(storeEntity.getId()));
                    foodDomain.setIsBestSeller(foodEntity.getIsBestSeller());
                    foodDomain.setTotalBuy(foodEntity.getTotalBuy());

                    return foodDomain;
                }
        ).collect(Collectors.toList());
        return  listFood;
    }

    public ResponseDataAPI getAllUserTransaction(BasicRequest request) {
        ResponseDataAPI responseDataAPI = new ResponseDataAPI();
        List<String> listColumnSort = ColumnSortTransactionAdmin.getListColumnSortTransactionAdmin();
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

        Page<TransactionEntity> listTransaction = transactionRepository.getAllTransactionOfUser(searchValue, pageable);


        if (listTransaction.getTotalElements() == 0l){
            return responseDataAPI;
        }

        responseDataAPI.setTotalRows(listTransaction.getTotalElements());

        List<TransactionDomain> listResult = new ArrayList<>();

        Map<Integer, String> shipperStatusMap = new HashMap<>();
        shipperStatusMap.put(ShipperStatus.DANG_TIM_TAI_XE.getNumber(), ShipperStatus.DANG_TIM_TAI_XE.getName());
        shipperStatusMap.put(ShipperStatus.DANG_CHO_LAY_HANG.getNumber(), ShipperStatus.DANG_CHO_LAY_HANG.getName());
        shipperStatusMap.put(ShipperStatus.DANG_GIAO.getNumber(), ShipperStatus.DANG_GIAO.getName());
        shipperStatusMap.put(ShipperStatus.DA_GIAO_THANH_CONG.getNumber(), ShipperStatus.DA_GIAO_THANH_CONG.getName());

        for (TransactionEntity transaction : listTransaction){
            TransactionDomain domain = new TransactionDomain();
            domain.setId(StringUtils.convertObjectToString(transaction.getId()));
            domain.setComment(transaction.getDeliveryAddress());
            domain.setDistance(transaction.getDistance());
            domain.setPaymentMethod(transaction.getPaymentMethod());
            domain.setTotal(transaction.getTotal());
            domain.setStatus(shipperStatusMap.get(transaction.getStatus()));
            domain.setUserAppId(StringUtils.convertObjectToString(transaction.getUserAppId()));
            if (transaction.getShipperId() == null){
                domain.setShipperName("Chưa có");
            } else {
                UserAppEntity shipperEntity = userAppRepository.findById(transaction.getShipperId()).orElse(null);
                domain.setShipperName(shipperEntity.getFullName());
            }

            UserAppEntity userAppEntity = userAppRepository.findById(transaction.getUserAppId()).orElse(null);
            if (userAppEntity == null){
                throw new CustomException("User ko ton tai", "User ko ton tai", HttpStatus.BAD_REQUEST);
            }
            domain.setUserAppName(userAppEntity.getUsername() + " - " + userAppEntity.getPhone());
            Date date = transaction.getCreatedDate();
            String createDate = DateTimeUtils.convertDateToStringOrEmpty(date, DateTimeUtils.YYYYMMDDhhmm);
            domain.setCreateDate(createDate);
            List<TransactionItemEntity> listItem = transactionItemRepository.findAllByTransactionId(transaction.getId());

            StoreEntity storeEntity = storeRepository.findById(transaction.getStoreId()).orElse(null);
            if (storeEntity == null) {
                throw new CustomException("Store ko ton tai", "Store ko ton tai", HttpStatus.BAD_REQUEST);
            }
            domain.setStoreId(storeEntity.getId().toString());
            domain.setStoreName(storeEntity.getName());
            domain.setAddress(storeEntity.getAddress());
            domain.setStoreAvatar(storeEntity.getAvatar());
            List<TransactionItemDomain> listResponseItem = new ArrayList<>();
            for (TransactionItemEntity transactionItemEntity : listItem){
                TransactionItemDomain itemDomain = new TransactionItemDomain();
                itemDomain.setTransactionId(transaction.getId().toString());
                itemDomain.setFoodId(transactionItemEntity.getFoodId().toString());
                FoodEntity foodEntity = foodRepository.findById(transactionItemEntity.getFoodId()).orElse(null);
                if (foodEntity != null){
                    itemDomain.setAmount(transactionItemEntity.getAmount());
                    itemDomain.setNote(transactionItemEntity.getNote());
                    itemDomain.setPrice(transactionItemEntity.getPrice());
                    itemDomain.setDiscountPercent(transactionItemEntity.getDiscountPercent());
                    itemDomain.setOriginalPrice(transactionItemEntity.getOriginalPrice());
                    itemDomain.setFoodAvatar(foodEntity.getAvatar());
                    itemDomain.setFoodName(foodEntity.getName());
                } else {
                    itemDomain.setAmount(0);
                    itemDomain.setNote("");
                    itemDomain.setPrice(0l);
                    itemDomain.setDiscountPercent(0);
                    itemDomain.setOriginalPrice(0l);
                    itemDomain.setFoodAvatar("https://media.istockphoto.com/vectors/red-white-stamp-grunge-deleted-vector-id1174096245?s=612x612");
                    itemDomain.setFoodName("");
                }

                listResponseItem.add(itemDomain);
            }
            domain.setListItem(listResponseItem);
            listResult.add(domain);
        }
        responseDataAPI.setData(listResult);
        return responseDataAPI;
    }

    public List<FoodDomain> geListRecommendFoodDetail(String userApp, Long foodIdOriginal){
        List<FoodDomain> result = new ArrayList<>();
        FoodEntity foodOriginal = foodRepository.findById(foodIdOriginal).orElse(null);
        Long userAppId = StringUtils.convertStringToLongOrNull(userApp);
        if (userAppId == null || foodOriginal == null){
            throw new CustomException(Error.PARAMETER_INVALID.getMessage()
                    , Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }
        UserAppEntity userAppEntity = userAppRepository.findById(userAppId).orElse(null);
        if (userAppEntity == null) {
            throw new CustomException(Error.PARAMETER_INVALID.getMessage()
                    , Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }
        Map<Long, Double> mapDistance = calculateDistanceUtils.getDistanceOfAllStores(userAppEntity);
        try {
            String linkData = metadataRepository.findByIdAndType(1l, MetadataType.DATA_RECOMMEND_FILE.getValue()).getValue();
            URL url = new URL(linkData);
            Scanner s = new Scanner(url.openStream());
            List<String> listLine = new ArrayList<>();
            while (s.hasNextLine()){
                listLine.add(s.nextLine());
            }

            for (String line : listLine){
                String[] listItem =line.split("\\|");
                Long userId = StringUtils.convertObjectToLongOrNull(listItem[0].substring(4));
                if (userAppId.equals(userId)){
                    List<String> listFoodStr = new ArrayList<>(Arrays.asList(listItem));
                    listFoodStr.remove(0);
                    List<Long> listFoodId = listFoodStr.stream()
                            .map(t -> StringUtils.convertStringToLongOrNull(t))
                            .filter(a -> !foodIdOriginal.equals(a))
                            .filter(t -> foodRepository.findById(t).orElse(null) != null)
                            .collect(Collectors.toList());
                    List<Long> listRatingFoodOfUser = ratingRepository.findAllByUserAppId(userAppId) // list nay ko co lien quan toi food bi xoa
                            .stream().map(t -> t.getFoodId())
                            .distinct()
                            .collect(Collectors.toList());
                    List<Long> listWillShowFoodId = listFoodId.stream().filter(t -> !listRatingFoodOfUser.contains(t))
                            .filter(e -> {
                                FoodEntity foodEntity = foodRepository.findById(e).orElse(null);
                                if (foodEntity.getFoodTypeId().equals(foodOriginal.getFoodTypeId())){
                                    return true;
                                } else {
                                    return false;
                                }
                            })
                            .limit(10)
                            .collect(Collectors.toList());

                    System.out.println("Chieu dai list : " + listFoodId.size());

                    //fill missing number
                    if (listWillShowFoodId.size() < 10){
                        int countListLength = listWillShowFoodId.size();
                        for (Long foodId : listFoodId){
                            FoodEntity foodEntity = foodRepository.findById(foodId).orElse(null);
                            if (countListLength == 10){
                                break;
                            }
                            if (!listWillShowFoodId.contains(foodId) && foodOriginal.getFoodTypeId().equals(foodEntity.getFoodTypeId())){
                                listWillShowFoodId.add(foodId);
                                countListLength ++;
                            }
                        }

                    }

                    if (!CollectionUtils.isEmpty(listWillShowFoodId)){
                        for (Long foodId : listWillShowFoodId){
                            FoodEntity foodEntity = foodRepository.findById(foodId).orElse(null);
                            if (foodEntity == null){
                                throw new CustomException(Error.PARAMETER_INVALID.getMessage()
                                        , Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
                            }
                            FoodDomain foodDomain = new FoodDomain();
                            foodDomain.setId(StringUtils.convertObjectToString(foodEntity.getId()));
                            foodDomain.setName(foodEntity.getName());
                            foodDomain.setFoodTypeId(StringUtils.convertObjectToString(foodEntity.getFoodTypeId()));
                            foodDomain.setStoreId(StringUtils.convertObjectToString(foodEntity.getStoreId()));
                            StoreEntity storeEntity = storeRepository.findById(foodEntity.getStoreId()).orElse(null);
                            if (storeEntity == null){
                                throw new CustomException(Error.PARAMETER_INVALID.getMessage()
                                        , Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
                            }
                            foodDomain.setStoreName(storeEntity.getName());
                            foodDomain.setSummaryRating(StringUtils.convertObjectToString(formatRatingUtils.formatRatingOneNumber(foodEntity.getSummaryRating())));
                            foodDomain.setAvatar(foodEntity.getAvatar());
                            foodDomain.setIsBestSeller(foodEntity.getIsBestSeller());
                            foodDomain.setTotalBuy(foodEntity.getTotalBuy());
                            foodDomain.setPrice(foodEntity.getPrice().toString());
                            FavouriteEntity favouriteEntity = favouriteRepository.findByUserAppIdAndItemIdAndType(userAppId, foodEntity.getId(), FavouriteType.FOOD.getValue());
                            if (favouriteEntity != null){
                                foodDomain.setIsFavourite("1");
                            } else {
                                foodDomain.setIsFavourite("0");
                            }

                            foodDomain.setDiscountPercent(foodEntity.getDiscountPercent());
                            foodDomain.setOriginalPrice(foodEntity.getOriginalPrice());
                            foodDomain.setDistance(mapDistance.get(storeEntity.getId()));
                            result.add(foodDomain);
                        }
                    }
                }
            }
            if (!CollectionUtils.isEmpty(result)){
                return result;
            } else {// user ko co trong file
                List<FoodEntity> listRecommendFood = foodRepository.findAll().stream()
                        .sorted(Comparator.comparing(FoodEntity::getSummaryRating, Comparator.nullsLast(Comparator.reverseOrder())))
                        .filter(t -> t.getFoodTypeId().equals(foodOriginal.getFoodTypeId()))
                        .filter(a -> !foodIdOriginal.equals(a.getId()))
                        .limit(10)
                        .collect(Collectors.toList());

                List<FoodDomain> listResult = new ArrayList<>();
                for (FoodEntity foodEntity : listRecommendFood){
                    FoodDomain foodDomain = new FoodDomain();
                    foodDomain.setId(StringUtils.convertObjectToString(foodEntity.getId()));
                    foodDomain.setName(foodEntity.getName());
                    foodDomain.setFoodTypeId(StringUtils.convertObjectToString(foodEntity.getFoodTypeId()));
                    foodDomain.setStoreId(StringUtils.convertObjectToString(foodEntity.getStoreId()));
                    StoreEntity storeEntity = storeRepository.findById(foodEntity.getStoreId()).orElse(null);
                    if (storeEntity == null){
                        throw new CustomException(Error.PARAMETER_INVALID.getMessage()
                                , Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
                    }
                    foodDomain.setStoreName(storeEntity.getName());
                    foodDomain.setSummaryRating(StringUtils.convertObjectToString(formatRatingUtils.formatRatingOneNumber(foodEntity.getSummaryRating())));
                    foodDomain.setAvatar(foodEntity.getAvatar());
                    foodDomain.setPrice(foodEntity.getPrice().toString());
                    FavouriteEntity favouriteEntity = favouriteRepository.findByUserAppIdAndItemIdAndType(userAppId, foodEntity.getId(), FavouriteType.FOOD.getValue());
                    if (favouriteEntity != null){
                        foodDomain.setIsFavourite("1");
                    } else {
                        foodDomain.setIsFavourite("0");
                    }

                    foodDomain.setDiscountPercent(foodEntity.getDiscountPercent());
                    foodDomain.setOriginalPrice(foodEntity.getOriginalPrice());
                    foodDomain.setDistance(mapDistance.get(storeEntity.getId()));
                    foodDomain.setIsBestSeller(foodEntity.getIsBestSeller());
                    foodDomain.setTotalBuy(foodEntity.getTotalBuy());
                    listResult.add(foodDomain);
                }
                return  listResult;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
