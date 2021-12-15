package com.example.demodatn.controller;

import com.example.demodatn.domain.BasicRequest;
import com.example.demodatn.domain.ResponseDataAPI;
import com.example.demodatn.domain.VoteDomain;
import com.example.demodatn.service.FoodServiceImpl;
//import io.swagger.annotations.ApiParam;
import com.example.demodatn.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/food")
public class FoodController {

    @Autowired
    private FoodServiceImpl foodService;

    @GetMapping("/{food_type}")
    public ResponseEntity<ResponseDataAPI> getFoodByFoodType(@PathVariable("food_type") String foodType,
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
        return ResponseEntity.ok(ResponseDataAPI.builder().data(foodService.getListFoodByFoodType(foodType, request)).build());
    }

    @GetMapping("/{food_id}/detail")
    public ResponseEntity<ResponseDataAPI> getFoodDetail(@PathVariable("food_id") String foodId){
        return ResponseEntity.ok(ResponseDataAPI.builder().data(foodService.getFoodDetail(foodId)).build());
    }

    @PostMapping("/vote")
    public ResponseEntity<ResponseDataAPI> updateVoteForFood(@RequestBody VoteDomain domain){
        foodService.updateVoteForFood(domain);
        return ResponseEntity.ok(ResponseDataAPI.builder().build());
    }

    @GetMapping("/{food_id}/store")
    public ResponseEntity<ResponseDataAPI> getAllFoodOfStoreByFoodId(@PathVariable("food_id") String foodId){
        return ResponseEntity.ok(ResponseDataAPI.builder().data(foodService.getAllFoodOfStoreByFoodId(foodId)).build());
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseDataAPI> getAllByValueSearch(
            @RequestParam("value_search") String valueSearch, @RequestParam("type_search") String typeSearch, @RequestParam("offset") Integer offset){
        if (offset == null || offset <= 1) {
            offset = 0;
        } else {
            offset = offset - 1;
        }
        return ResponseEntity.ok(ResponseDataAPI.builder().data(foodService.getAllBySearchValue(valueSearch, typeSearch, offset)).message(typeSearch).build());
    }
}
