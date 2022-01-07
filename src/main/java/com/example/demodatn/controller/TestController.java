package com.example.demodatn.controller;

import com.example.demodatn.domain.ResponseDataAPI;
import com.example.demodatn.service.ItemBasedFilteringServiceImpl;
import com.example.demodatn.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@CrossOrigin("*")
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private ItemBasedFilteringServiceImpl itemBasedFilteringService;

    @GetMapping("/recommend-system")
    private ResponseEntity<ResponseDataAPI> testRecommendSystem() {
//        itemBasedFilteringService.buildDifferencesMatrixAndPredict();
        return ResponseEntity.ok(ResponseDataAPI.builder().data(itemBasedFilteringService.handleFileCsv("1")
        ).build());
    }


//    @RequestMapping(value = "/admin", method = RequestMethod.GET)
//    public String adminPage(Model model, Principal principal) {
//
//        UserDetails loginedUser = (UserDetails) ((Authentication) principal).getPrincipal();
//
//        String userInfo = WebUtils.toString(loginedUser);
//        model.addAttribute("userInfo", userInfo);
//
//        return "adminPage";
//    }
//
////    @RequestMapping(value = "/login", method = RequestMethod.GET)
////    public String loginPage(Model model) {
////
////        return "loginPage";
////    }
//
//    @RequestMapping(value = "/logoutSuccessful", method = RequestMethod.GET)
//    public String logoutSuccessfulPage(Model model) {
//        model.addAttribute("title", "Logout");
//        return "logoutSuccessfulPage";
//    }
//
//    @RequestMapping(value = "/userInfo", method = RequestMethod.GET)
//    public String userInfo(Model model, Principal principal) {
//
//        // Sau khi user login thanh cong se co principal
//        String userName = principal.getName();
//
//        System.out.println("User Name: " + userName);
//
//        User loginedUser = (User) ((Authentication) principal).getPrincipal();
//
//        String userInfo = WebUtils.toString(loginedUser);
//        model.addAttribute("userInfo", userInfo);
//
//        return "userInfoPage";
//    }
//
//    @RequestMapping(value = "/403", method = RequestMethod.GET)
//    public String accessDenied(Model model, Principal principal) {
//
//        if (principal != null) {
//            User loginedUser = (User) ((Authentication) principal).getPrincipal();
//
//            String userInfo = WebUtils.toString(loginedUser);
//
//            model.addAttribute("userInfo", userInfo);
//
//            String message = "Hi " + principal.getName() //
//                    + "<br> You do not have permission to access this page!";
//            model.addAttribute("message", message);
//
//        }
//
//        return "403Page";
//    }

}
