package com.example.demodatn.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserAppInfoResponseForgotPassDomain {
    private String token;
    private Long userAppId;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getUserAppId() {
        return userAppId;
    }

    public void setUserAppId(Long userAppId) {
        this.userAppId = userAppId;
    }
}
