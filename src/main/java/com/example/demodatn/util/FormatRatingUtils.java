package com.example.demodatn.util;

import org.springframework.stereotype.Component;

@Component
public class FormatRatingUtils {
    public Double formatRatingOneNumber(Double rating){
        return Math.round(rating*10.0)/10.0;
    }
}
