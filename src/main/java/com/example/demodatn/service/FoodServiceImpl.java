package com.example.demodatn.service;

import com.example.demodatn.constant.ColumnSortFood;
import com.example.demodatn.constant.ConstantDefine;
import com.example.demodatn.constant.Error;
import com.example.demodatn.domain.*;
import com.example.demodatn.entity.FoodEntity;
import com.example.demodatn.entity.RatingEntity;
import com.example.demodatn.entity.UserAppEntity;
import com.example.demodatn.exception.CustomException;
import com.example.demodatn.repository.*;
import com.example.demodatn.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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

    public List<FoodDomain> getListFoodByFoodType(String foodTypeStr, BasicRequest request){
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
                    return domain;
                }
        ).collect(Collectors.toList());
        return responseList;
    }

    public FoodWithCommentDomain getFoodDetail(String food) {
        Long foodId = StringUtils.convertObjectToLongOrNull(food);
        if (foodId == null){
            throw new CustomException(Error.PARAMETER_INVALID.getMessage()
                    , Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }
        FoodEntity t = foodRepository.findById(foodId).orElse(null);
        if (t == null){
            throw new CustomException(Error.PARAMETER_INVALID.getMessage()
                    , Error.PARAMETER_INVALID.getCode(), HttpStatus.BAD_REQUEST);
        }
        FoodWithCommentDomain domain = new FoodWithCommentDomain();
        domain.setId(StringUtils.convertObjectToString(t.getId()));
        domain.setName(t.getName());
        domain.setFoodTypeId(StringUtils.convertObjectToString(t.getFoodTypeId()));
        domain.setStoreId(StringUtils.convertObjectToString(t.getStoreId()));
        domain.setStoreName(storeRepository.findById(t.getStoreId()).orElse(null).getName());
        domain.setSummaryRating(t.getSummaryRating() == null ? "0" : StringUtils.convertObjectToString(t.getSummaryRating()));
        domain.setAvatar(t.getAvatar());
        domain.setPrice(StringUtils.convertObjectToString(t.getPrice()));
        List<Long> listRatingIds = foodRatingRepository.getListRatingIdsFromFoodId(foodId);
        List<CommentDomain> listComments = new ArrayList<>();
        domain.setNumberOfVote("Chưa có lượt đánh giá");
        if (!CollectionUtils.isEmpty(listRatingIds)){
            domain.setNumberOfVote(StringUtils.convertObjectToString(listRatingIds.size()));
            listComments = listRatingIds.stream().map(ratingId -> {
                RatingEntity ratingEntity = ratingRepository.getById(ratingId);
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
}
