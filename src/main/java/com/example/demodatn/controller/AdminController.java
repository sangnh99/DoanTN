package com.example.demodatn.controller;

import com.example.demodatn.domain.*;
import com.example.demodatn.service.FoodServiceImpl;
import com.example.demodatn.service.StoreServiceImpl;
import com.example.demodatn.service.TransactionServiceImpl;
import com.example.demodatn.service.UserAppServiceImpl;
import com.example.demodatn.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private FoodServiceImpl foodService;

    @Autowired
    private TransactionServiceImpl transactionService;

    @Autowired
    private UserAppServiceImpl userAppService;

    @Autowired
    private StoreServiceImpl storeService;

    @GetMapping("/transaction")
    public ResponseEntity<ResponseDataAPI> getAllUserTransaction(
            @RequestParam("value_search") String valueSearch, @RequestParam("type_sort") String typeSort, @RequestParam("offset") Integer offset, @RequestParam(value = "column_sort") String columnSort){
        BasicRequest request = new BasicRequest();
        if (offset == null || offset <= 1) {
            request.setOffset(0);
        } else {
            request.setOffset(offset - 1);
        }
        request.setLimit(8);
        request.setColumnSort(columnSort);
        request.setTypeSort(typeSort);
        request.setValueSearch(valueSearch);
        ResponseDataAPI responseDataAPI = foodService.getAllUserTransaction(request);
        return ResponseEntity.ok(ResponseDataAPI.builder().data(responseDataAPI.getData()).totalRows(responseDataAPI.getTotalRows()).build());
    }

    @PostMapping("/transaction/delete")
    public  ResponseEntity<ResponseDataAPI> deleteTransaction(@RequestBody DeleteTransactionDomain domain){
        transactionService.deleteUserTransaction(domain);
        return ResponseEntity.ok(ResponseDataAPI.builder().build());
    }
    @GetMapping("/user/list")
    public ResponseEntity<ResponseDataAPI> getAllUsers(@RequestParam("value_search") String valueSearch, @RequestParam("offset") String offsetStr){
        Integer offset = StringUtils.convertStringToIntegerOrNull(offsetStr);
        if (offset == null || offset <= 1) {
            offset = 0;
        } else {
            offset = offset - 1;
        }
        ResponseDataAPI responseDataAPI = userAppService.getAllUsersAdmin(valueSearch, offset);
        return ResponseEntity.ok(ResponseDataAPI.builder().data(responseDataAPI.getData()).totalRows(responseDataAPI.getTotalRows()).build());
    }

    @PostMapping("/user/lock")
    public ResponseEntity<ResponseDataAPI> lockUser(@RequestBody UserAppIdDomain domain){
        userAppService.lockUser(domain);
        return ResponseEntity.ok(ResponseDataAPI.builder().build());
    }

    @PostMapping("/user/unlock")
    public ResponseEntity<ResponseDataAPI> unlockUser(@RequestBody UserAppIdDomain domain){
        userAppService.unlockUser(domain);
        return ResponseEntity.ok(ResponseDataAPI.builder().build());
    }

    @PostMapping("/password")
    public ResponseEntity<ResponseDataAPI> changePasswordAdmin(@RequestBody ChangePasswordAdminDomain domain){
        userAppService.changePasswordAdmin(domain);
        return ResponseEntity.ok(ResponseDataAPI.builder().build());
    }

    @PostMapping("/store/create")
    public ResponseEntity<ResponseDataAPI> createNewStore(@RequestBody CreateNewStoreDomain domain){
        storeService.createNewStoreAdmin(domain);
        return ResponseEntity.ok(ResponseDataAPI.builder().build());
    }

    @GetMapping("/store")
    public ResponseEntity<ResponseDataAPI> getAllStoreAdmin(@RequestParam("offset") Integer offset, @RequestParam("value_search") String valueSearch){
        if (offset == null || offset <= 1) {
            offset = 0;
        } else {
            offset = offset - 1;
        }
        ResponseDataAPI responseDataAPI = storeService.getAllStoreAdmin(valueSearch, offset);

        return ResponseEntity.ok(ResponseDataAPI.builder().data(responseDataAPI.getData()).totalRows(responseDataAPI.getTotalRows()).build());
    }

    @GetMapping("/store/edit/{store_id}")
    public ResponseEntity<ResponseDataAPI> getInfoOfStoreAdmin(@PathVariable("store_id") String storeId){
        ResponseDataAPI responseDataAPI = storeService.getInfoOfStoreAdmin(storeId);
        return ResponseEntity.ok(ResponseDataAPI.builder().data(responseDataAPI.getData()).build());
    }

    @PostMapping("/store/edit/save")
    public ResponseEntity<ResponseDataAPI> editInfoStore(@RequestBody EditStoreDomain domain){
        storeService.editInfoStore(domain);
        return ResponseEntity.ok(ResponseDataAPI.builder().build());
    }

    @GetMapping("/store/edit/{store_id}/food")
    public ResponseEntity<ResponseDataAPI> getAllFoodOfStoreAdmin(@PathVariable("store_id") String storeId, @RequestParam("offset") Integer offset, @RequestParam("value_search") String valueSearch){
        if (offset == null || offset <= 1) {
            offset = 0;
        } else {
            offset = offset - 1;
        }
        ResponseDataAPI responseDataAPI = storeService.getAllFoodOfStoreAdmin(storeId, offset, valueSearch);
        return ResponseEntity.ok(ResponseDataAPI.builder().data(responseDataAPI.getData()).totalRows(responseDataAPI.getTotalRows()).build());
    }

    @PostMapping("/store/edit/add-food")
    public ResponseEntity<ResponseDataAPI> addNewFoodAdmin(@RequestBody CreateNewFoodEditStoreDomain domain){
        ResponseDataAPI responseDataAPI = storeService.createNewFoodEditStoreDomain(domain);
        return ResponseEntity.ok(ResponseDataAPI.builder().data(responseDataAPI.getData()).totalRows(responseDataAPI.getTotalRows()).build());
    }
    @PostMapping("/store/edit/edit-food")
    public ResponseEntity<ResponseDataAPI> editFoodAdmin(@RequestBody EditFoodDomain domain){
        ResponseDataAPI responseDataAPI = storeService.editFoodOfStore(domain);
        return ResponseEntity.ok(ResponseDataAPI.builder().data(responseDataAPI.getData()).totalRows(responseDataAPI.getTotalRows()).build());
    }

    @PostMapping("/store/delete/food/{food_id}")
    public ResponseEntity<ResponseDataAPI> deleteFoodOfStore(@PathVariable("food_id") String foodIdStr){
        ResponseDataAPI responseDataAPI = storeService.deleteFoodOfStore(foodIdStr);
        return ResponseEntity.ok(ResponseDataAPI.builder().data(responseDataAPI.getData()).totalRows(responseDataAPI.getTotalRows()).build());
    }
}
