package com.example.demodatn.util;

import org.springframework.stereotype.Component;

@Component
public class FormatRatingUtils {
    public Double formatRatingOneNumber(Double rating){
        if (rating == null){
            return 0.0;
        }
        return Math.round(rating*10.0)/10.0;
    }
}
