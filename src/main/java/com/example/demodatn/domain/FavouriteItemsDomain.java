package com.example.demodatn.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class FavouriteItemsDomain {
    private List<FoodDomain> listFoods;
    private List<StoreDomain> listStores;

    public List<FoodDomain> getListFoods() {
        return listFoods;
    }

    public void setListFoods(List<FoodDomain> listFoods) {
        this.listFoods = listFoods;
    }

    public List<StoreDomain> getListStores() {
        return listStores;
    }

    public void setListStores(List<StoreDomain> listStores) {
        this.listStores = listStores;
    }
}
