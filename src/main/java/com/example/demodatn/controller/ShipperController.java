package com.example.demodatn.controller;

import com.example.demodatn.domain.*;
import com.example.demodatn.service.*;
import com.example.demodatn.util.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/shipper")
public class ShipperController {

    @Autowired
    private FoodServiceImpl foodService;

    @Autowired
    private TransactionServiceImpl transactionService;

    @Autowired
    private UserAppServiceImpl userAppService;

    @Autowired
    private StoreServiceImpl storeService;

    @Autowired
    private AdminServiceImpl adminService;

    @Autowired
    private ShipperServiceImpl shipperService;

    @GetMapping("/available-transactions")
    public ResponseEntity<ResponseDataAPI> getAllAvailableTransaction(@RequestParam("value_search") String valueSearch, @RequestParam("offset") String offsetStr){
        Integer offset = StringUtils.convertStringToIntegerOrNull(offsetStr);
        if (offset == null || offset <= 1) {
            offset = 0;
        } else {
            offset = offset - 1;
        }
        ResponseDataAPI responseDataAPI = shipperService.getAllAvailableTransaction(valueSearch, offset);
        return ResponseEntity.ok(ResponseDataAPI.builder().data(responseDataAPI.getData()).totalRows(responseDataAPI.getTotalRows()).build());
    }

    @PostMapping("/take-order")
    public ResponseEntity<ResponseDataAPI> shipperTakeOrder(@RequestBody ShipperTakeOrderDomain domain){
        shipperService.shipperTakeOrder(domain);
        return ResponseEntity.ok(ResponseDataAPI.builder().build());
    }

    @GetMapping("/get-current-order")
    public ResponseEntity<ResponseDataAPI> getCurrentOrderShipper(@RequestParam("shipper_id") String shipper){
        ResponseDataAPI responseDataAPI = shipperService.getCurrentOrderShipper(shipper);
        return ResponseEntity.ok(ResponseDataAPI.builder().data(responseDataAPI.getData()).build());
    }

    @PostMapping("/cancel-current-order")
    public ResponseEntity<ResponseDataAPI> cancelCurrentOrder(@RequestBody ShipperTakeOrderDomain domain){
        shipperService.cancelCurrentOrder(domain);
        return ResponseEntity.ok(ResponseDataAPI.builder().build());
    }

    @PostMapping("/continue-current-order")
    public ResponseEntity<ResponseDataAPI> continueCurrentOrder(@RequestBody ShipperTakeOrderDomain domain){
        shipperService.continueCurrentOrder(domain);
        return ResponseEntity.ok(ResponseDataAPI.builder().build());
    }

    @PostMapping("/finish-current-order")
    public ResponseEntity<ResponseDataAPI> finishCurrentOrder(@RequestBody ShipperTakeOrderDomain domain){
        shipperService.finishShipperOrder(domain);
        return ResponseEntity.ok(ResponseDataAPI.builder().build());
    }

    @GetMapping("/shipper-transactions")
    public ResponseEntity<ResponseDataAPI> getShipperTransaction(@RequestParam("shipper_id") String shipperId, @RequestParam("value_search") String valueSearch, @RequestParam("offset") String offsetStr){
        Integer offset = StringUtils.convertStringToIntegerOrNull(offsetStr);
        if (offset == null || offset <= 1) {
            offset = 0;
        } else {
            offset = offset - 1;
        }
        ResponseDataAPI responseDataAPI = shipperService.getShipperTransaction(shipperId, valueSearch, offset);
        return ResponseEntity.ok(ResponseDataAPI.builder().data(responseDataAPI.getData()).totalRows(responseDataAPI.getTotalRows()).build());
    }

    @GetMapping("/personal-info")
    public ResponseEntity<ResponseDataAPI> getPersonalInfoShipper(@RequestParam("shipper_id") String shipperId){
        ResponseDataAPI responseDataAPI =  shipperService.getPersonalInfoShipper(shipperId);
        return ResponseEntity.ok(ResponseDataAPI.builder().data(responseDataAPI.getData()).build());
    }

    @PostMapping("/password")
    public ResponseEntity<ResponseDataAPI> changePasswordShipper(@RequestBody ChangePasswordAdminDomain domain){
        shipperService.changePasswordShipper(domain);
        return ResponseEntity.ok(ResponseDataAPI.builder().build());
    }
}
