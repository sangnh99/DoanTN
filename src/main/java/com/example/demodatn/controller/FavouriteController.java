package com.example.demodatn.controller;

import com.example.demodatn.domain.AddToFavouriteDomain;
import com.example.demodatn.domain.ResponseDataAPI;
import com.example.demodatn.service.FavouriteServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@RestController
@RequestMapping("/favourite")
public class FavouriteController {

    @Autowired
    private FavouriteServiceImpl favouriteService;

    @PostMapping("/add-to-favourite")
    public ResponseEntity<ResponseDataAPI> addToFavourite(@RequestBody AddToFavouriteDomain domain){
        favouriteService.AddToFavorite(domain);
        return ResponseEntity.ok(ResponseDataAPI.builder().build());
    }

    @PostMapping("/delete-favourite")
    public ResponseEntity<ResponseDataAPI> deleteFavourite(@RequestBody AddToFavouriteDomain domain){
        favouriteService.DeleteFromFavourite(domain);
        return ResponseEntity.ok(ResponseDataAPI.builder().data(favouriteService.getListFavouriteItems(domain.getUserAppId())).build());
    }

    @GetMapping("/list-favourite/{user_app_id}")
    public ResponseEntity<ResponseDataAPI> getListFavouriteItem(@PathVariable("user_app_id") String userApp){
        return ResponseEntity.ok(ResponseDataAPI.builder().data(favouriteService.getListFavouriteItems(userApp)).build());
    }
}
