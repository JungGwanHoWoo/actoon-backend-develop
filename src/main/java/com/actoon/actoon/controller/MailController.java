package com.actoon.actoon.controller;

import com.actoon.actoon.dto.BasicResponse;
import com.actoon.actoon.dto.MailDto;

import com.actoon.actoon.dto.MailDto.*;
import com.actoon.actoon.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/mail")
public class MailController {

    @Autowired
    private MailService mailService;


    // 인증번호 인증
    @PostMapping("/certNumber")
    public ResponseEntity<BasicResponse> emailAuthentication(@RequestBody EmailAuthDto emailDto){
        var result = mailService.certificateEmail(emailDto);

        //if(!result)
        //throw new IllegalStateException("");

        BasicResponse response = new BasicResponse(HttpStatus.OK, "인증이 완료되었습니다.");
        return ResponseEntity.status(202).body(response);
    }


    @PostMapping("/sendingMail")
    public ResponseEntity<BasicResponse> MailSend(@RequestBody MailDto emailDto){
        System.out.println(emailDto.getEmail());
        int number = mailService.sendMail(emailDto.getEmail());

        mailService.storeNumbertoRedis(emailDto);
        //String num = "" + number;

        //AuthenticateMailDto auth = new AuthenticateMailDto(email, number);
        BasicResponse response = new BasicResponse(HttpStatus.OK, "메일 전송이 완료되었습니다.");
        return ResponseEntity.status(200).body(response);
    }



    // 여기서 인증받도록

}