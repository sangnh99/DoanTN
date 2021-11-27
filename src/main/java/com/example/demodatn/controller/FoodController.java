package com.example.demodatn.controller;

import com.example.demodatn.domain.BasicRequest;
import com.example.demodatn.domain.ResponseDataAPI;
import com.example.demodatn.service.FoodServiceImpl;
import io.swagger.annotations.ApiParam;
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
                                                             @RequestParam(value = "offset", required = false) @ApiParam(value = "offset", example = "0") Integer offset,
                                                             @RequestParam(value = "limit", required = false) @ApiParam(value = "limit", example = "10") Integer limit,
                                                             @RequestParam(value = "column_sort", required=false) @ApiParam(value = "columnSort", example = "TITLE") String columnSort,
                                                             @RequestParam(value = "type_sort", required=false) @ApiParam(value = "typeSort", example = "ASC") String typeSort,
                                                             @RequestParam(value = "search_value", required=false) @ApiParam(value = "searchValue", example = "") String searchValue
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

    @GetMapping("/{food_id}")
    public ResponseEntity<ResponseDataAPI> getFoodDetail(@PathVariable("food_id") String foodId){
        return ResponseEntity.ok(ResponseDataAPI.builder().data(foodService.getFoodDetail(foodId)).build());
    }
}
