package com.example.demodatn.service;

import com.example.demodatn.constant.ColumnSortFood;
import com.example.demodatn.constant.ConstantDefine;
import com.example.demodatn.constant.Error;
import com.example.demodatn.domain.BasicRequest;
import com.example.demodatn.domain.FoodDomain;
import com.example.demodatn.entity.FoodEntity;
import com.example.demodatn.exception.CustomException;
import com.example.demodatn.repository.FoodRepository;
import com.example.demodatn.repository.StoreRepository;
import com.example.demodatn.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class FoodServiceImpl {

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private StoreRepository storeRepository;

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

    public Object getFoodDetail(String food) {
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
}
