package com.example.demodatn.controller;
import com.example.demodatn.constant.ConstantDefine;
import com.example.demodatn.domain.PaypalPriceDomain;
import com.example.demodatn.domain.ResponseDataAPI;
import com.example.demodatn.service.PaypalService;
import com.example.demodatn.service.PaypalServiceImpl;
import com.example.demodatn.util.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;

@CrossOrigin("*")
@RestController
@RequestMapping("/paypal")
public class PaypalController {

    @Autowired
    PaypalService service;

    @Autowired
    private PaypalServiceImpl paypalService;

    public static final String SUCCESS_URL = "http://localhost:8081/handle-paypal";
    public static final String CANCEL_URL = "http://localhost:8081/payment";

    @PostMapping("/pay")
    public ResponseEntity<ResponseDataAPI> postPayment(@RequestBody PaypalPriceDomain domain) {
        String redirectUrl = "";
        String description =  RandomStringUtils.randomAlphabetic(7);
        Double priceInUSD = (double) Math.round(domain.getTotalPrice().doubleValue()/23000*100)/100;
        try {
            Payment payment = service.createPayment(priceInUSD, ConstantDefine.USD, "paypal",
                    "sale", description, CANCEL_URL,
                    SUCCESS_URL);
            System.out.println(payment);
            for (Links link : payment.getLinks()) {
                if (link.getRel().equals("approval_url")) {
                     redirectUrl = link.getHref();
                }
            }

        } catch (PayPalRESTException e) {

            e.printStackTrace();
        }
        return ResponseEntity.ok(ResponseDataAPI.builder().data(redirectUrl).build());
    }

    @GetMapping("/show-payment-result")
    public ResponseEntity<ResponseDataAPI> showPaymentResult(@RequestParam("user_app_id") String userAppId, @RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId){
        String result = "";
        try {
            Payment payment = service.executePayment(paymentId, payerId);
            System.out.println(payment.toJSON());
            if (payment.getState().equals("approved")) {
                result = paypalService.handlePaypalSuccess(userAppId);
            }
        } catch (PayPalRESTException e) {
            System.out.println(e.getMessage());
        }
        return ResponseEntity.ok(ResponseDataAPI.builder().data(result).build());
    }
}

