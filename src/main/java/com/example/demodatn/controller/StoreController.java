package com.example.demodatn.controller;

import com.example.demodatn.domain.ResponseDataAPI;
import com.example.demodatn.service.StoreServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/store")
public class StoreController {

    @Autowired
    private StoreServiceImpl storeService;

    @GetMapping("/{store_id}")
    public ResponseEntity<ResponseDataAPI> getStoreDetail(@PathVariable("store_id") String store, @RequestParam("user_app_id") String userApp){
        return ResponseEntity.ok(ResponseDataAPI.builder().data(storeService.getStoreDetail(store, userApp)).build());
    }


}
