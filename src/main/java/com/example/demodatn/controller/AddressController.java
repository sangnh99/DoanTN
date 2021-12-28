package com.example.demodatn.controller;

import com.example.demodatn.domain.AddAddressDomain;
import com.example.demodatn.domain.AddressDomain;
import com.example.demodatn.domain.ChangeActiveAddressDomain;
import com.example.demodatn.domain.ResponseDataAPI;
import com.example.demodatn.service.AddressServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/address")
public class AddressController {

    @Autowired
    private AddressServiceImpl addressService;

    @GetMapping("/{user_app_id}/get-address")
    public ResponseEntity<ResponseDataAPI> getListAddress(@PathVariable("user_app_id") String userApp){
        return ResponseEntity.ok(ResponseDataAPI.builder().data(addressService.getListAddressOfUser(userApp)).build());
    }

    @GetMapping("/{user_app_id}/get-active-address")
    public ResponseEntity<ResponseDataAPI> getActiveAddress(@PathVariable("user_app_id") String userApp){
        return ResponseEntity.ok(ResponseDataAPI.builder().data(addressService.getActiveAddress(userApp)).build());
    }

    @PostMapping("/{user_app_id}/active-address")
    public ResponseEntity<ResponseDataAPI> changeActiveAddress(@PathVariable("user_app_id") String userApp, @RequestBody ChangeActiveAddressDomain domain){
        return ResponseEntity.ok(ResponseDataAPI.builder().data(addressService.changeActiveAddress(userApp, domain)).build());
    }

    @PostMapping("/{user_app_id}/delete-address")
    public ResponseEntity<ResponseDataAPI> deleteAddress(@PathVariable("user_app_id") String userApp, @RequestBody ChangeActiveAddressDomain domain){
        return ResponseEntity.ok(ResponseDataAPI.builder().data(addressService.deleteAddress(userApp, domain)).build());
    }

    @PostMapping("/{user_app_id}/add-address")
    public ResponseEntity<ResponseDataAPI> addAddress(@PathVariable("user_app_id") String userApp, @RequestBody AddAddressDomain domain){
        return ResponseEntity.ok(ResponseDataAPI.builder().data(addressService.addNewAddress(userApp, domain)).build());
    }
}
