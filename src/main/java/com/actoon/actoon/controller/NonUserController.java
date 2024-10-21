package com.actoon.actoon.controller;

import com.actoon.actoon.dto.BasicResponse;
import com.actoon.actoon.dto.NonUserDto.*;
import com.actoon.actoon.service.NonUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/non-users")
public class NonUserController {

    @Autowired
    NonUserService nonUserService;
//
//    public NonUserController(NonUserService nonUserService){
//        this.nonUserService = nonUserService;
//    }


    @PostMapping("/register")
    public BasicResponse register(@RequestBody NonUserRegisterRequestDto nonUser){
        System.out.println("REGISTERs");
        nonUserService.register(nonUser);
        return new BasicResponse(HttpStatus.OK, "등록 성공했습니다.");
    }





}
