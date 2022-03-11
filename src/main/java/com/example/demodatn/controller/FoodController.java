package com.example.demodatn.controller;

import com.example.demodatn.domain.AddRatingDomain;
import com.example.demodatn.domain.BasicRequest;
import com.example.demodatn.domain.ResponseDataAPI;
import com.example.demodatn.domain.VoteDomain;
import com.example.demodatn.entity.FoodEntity;
import com.example.demodatn.repository.FoodRepository;
import com.example.demodatn.repository.UserAppRepository;
import com.example.demodatn.service.FoodServiceImpl;
//import io.swagger.annotations.ApiParam;
import com.example.demodatn.service.ItemBasedFilteringServiceImpl;
import com.example.demodatn.util.CalculateDistanceUtils;
import com.example.demodatn.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/food")
public class FoodController {

    @Autowired
    private FoodServiceImpl foodService;

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private ItemBasedFilteringServiceImpl itemBasedFilteringService;

    @GetMapping("/{food_type}")
    public ResponseEntity<ResponseDataAPI> getFoodByFoodType(@PathVariable("food_type") String foodType,
                                                             @RequestParam(value = "user_app_id", required = false)  String userApp,
                                                             @RequestParam(value = "offset", required = false)  Integer offset,
                                                             @RequestParam(value = "limit", required = false)  Integer limit,
                                                             @RequestParam(value = "column_sort", required=false) String columnSort,
                                                             @RequestParam(value = "type_sort", required=false)  String typeSort,
                                                             @RequestParam(value = "search_value", required=false)  String searchValue
    ){

        BasicRequest request = new BasicRequest();
        if (offset == null || offset <= 1) {
            request.setOffset(0);
        } else {
            request.setOffset(offset - 1);
        }
        if (limit == null || limit < 1) {
            request.setLimit(10);
        } else {
            request.setLimit(limit);
        }
        request.setColumnSort(columnSort);
        request.setTypeSort(typeSort);
        request.setValueSearch(searchValue);
        ResponseDataAPI responseDataAPI = foodService.getListFoodByFoodType(userApp, foodType, request);
        return ResponseEntity.ok(ResponseDataAPI.builder().data(responseDataAPI.getData()).totalRows(responseDataAPI.getTotalRows()).build());
    }

    @GetMapping("/{food_id}/detail")
    public ResponseEntity<ResponseDataAPI> getFoodDetail(@PathVariable("food_id") String foodId
    , @RequestParam("user_app_id") String userApp){
        return ResponseEntity.ok(ResponseDataAPI.builder().data(foodService.getFoodDetail(foodId, userApp)).build());
    }

    @PostMapping("/vote")
    public ResponseEntity<ResponseDataAPI> updateVoteForFood(@RequestBody VoteDomain domain){
        foodService.updateVoteForFood(domain);
        return ResponseEntity.ok(ResponseDataAPI.builder().build());
    }

    @GetMapping("/{food_id}/store")
    public ResponseEntity<ResponseDataAPI> getAllFoodOfStoreByFoodId(@PathVariable("food_id") String foodId, @RequestParam(value = "user_app_id", required = false)  String userApp){
        return ResponseEntity.ok(ResponseDataAPI.builder().data(foodService.getAllFoodOfStoreByFoodId(foodId, userApp)).build());
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseDataAPI> getAllByValueSearch(
            @RequestParam("value_search") String valueSearch, @RequestParam("type_search") String typeSearch, @RequestParam("offset") Integer offset, @RequestParam(value = "user_app_id", required = false)  String userApp){
        if (offset == null || offset <= 1) {
            offset = 0;
        } else {
            offset = offset - 1;
        }
        ResponseDataAPI responseDataAPI = foodService.getAllBySearchValue(valueSearch, typeSearch, offset, userApp);
        return ResponseEntity.ok(ResponseDataAPI.builder().data(responseDataAPI.getData()).totalRows(responseDataAPI.getTotalRows()).message(typeSearch).build());
    }

    @GetMapping("/get-list-near-food")
    public ResponseEntity<ResponseDataAPI> getListNearFood(@RequestParam("user_app_id") String userAppId){
        return ResponseEntity.ok(ResponseDataAPI.builder().data(foodService.geListNearFood(userAppId)).build());
    }

//    @PostMapping("/{food_id}/add-rating")
//    public ResponseEntity<ResponseDataAPI> addNewRatingForFood(@PathVariable("food_id") String food, @RequestBody AddRatingDomain domain){
//        return ResponseEntity.ok(ResponseDataAPI.builder().data(foodService.addNewRatingForFood(food, domain)).build());
//    }
    @PostMapping("/{food_id}/add-rating")
    public ResponseEntity<ResponseDataAPI> addNewRatingForFood(@RequestParam(value = "files", required = false) MultipartFile[] files,
                                                               @RequestParam("user_app_id") String userAppId,
                                                               @RequestParam("rating") String rating,
                                                               @RequestParam("comment") String comment,
                                                               @RequestParam("food_id") String food) {
        AddRatingDomain domain = new AddRatingDomain();
        domain.setUserAppId(userAppId);
        domain.setRating(rating);
        domain.setComment(comment);
        return ResponseEntity.ok(ResponseDataAPI.builder().data(foodService.addNewRatingForFood(food, domain, files)).build());
    }

    @GetMapping("/get-list-recommend-food")
    public ResponseEntity<ResponseDataAPI> getListRecommendFood(@RequestParam("user_app_id") String userApp){
        return ResponseEntity.ok(ResponseDataAPI.builder().data(foodService.geListRecommendFood(userApp)).build());
    }

    @GetMapping("/get-list-sale-food")
    public ResponseEntity<ResponseDataAPI> getListSaleFood(@RequestParam("user_app_id") String userApp){
        return ResponseEntity.ok(ResponseDataAPI.builder().data(foodService.geListSaleFood(userApp)).build());
    }

    @GetMapping("/update-recommend-file")
    public ResponseEntity<ResponseDataAPI> updateRecommendFile(){
        itemBasedFilteringService.buildDifferencesMatrixAndPredict();
        return ResponseEntity.ok(ResponseDataAPI.builder().build());
    }

}
