package com.actoon.actoon.controller;

import java.util.Map;

import javax.naming.NoPermissionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.actoon.actoon.dto.BasicResponse;
import com.actoon.actoon.dto.ExceptionResponse;
import com.actoon.actoon.dto.JwtAuthenticationRequest;
import com.actoon.actoon.dto.JwtAuthenticationResponse.*;
import com.actoon.actoon.dto.JwtAuthenticationResponse.IssueJwtAuthentication;
import com.actoon.actoon.dto.JwtAuthenticationResponse.ReissueJwtAuthenticaiton;
import com.actoon.actoon.dto.UserDto.*;
import com.actoon.actoon.dto.UserDto.ChangeNicknameRequestDto;
import com.actoon.actoon.dto.UserDto.ChangeNicknameResponseDto;
import com.actoon.actoon.dto.UserDto.SignInDto;
import com.actoon.actoon.dto.UserDto.SignUpDto;
import com.actoon.actoon.service.AuthenticationServiceImpl;
import com.actoon.actoon.service.JwtServiceImpl;
import com.actoon.actoon.service.MailService;

/*
    1. spring security는 모든 접근 주체에 대해 authentication을 생성한다.
    2. authentication은 securityContext에 저장
    3. SecurityContext는 SecurityContextHolder가 저장
    4. security session들을 SecurityContextHolder 메모리 저장소에 저장하고, 꺼내서 사용
 */


@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    AuthenticationServiceImpl authService;

    @Autowired
    JwtServiceImpl jwtService;

    @Autowired
    MailService mailService;

    @GetMapping("/api") // end-point, routing
    public String test(){
        return "TOMCAT TEST SUCCESS!!";
    }

    @PostMapping("/signin")
    public ResponseEntity<IssueJwtAuthentication> signin(@RequestHeader HttpHeaders headers, @RequestBody SignInDto user){

        System.out.println("SIGNIN : " + user.toString());
        System.out.println(user.toString());

        IssueJwtAuthentication response = authService.signin(user);

        String accessToken = response.getAccessToken();
        String refreshToken = response.getRefreshToken();

        authService.storeToken(accessToken, refreshToken); // 로그인할 때
//        ResponseCookie[] cookies = makeResponseSetCookie(accessToken, refreshToken);

        return ResponseEntity.status(200).body(response);
    }

    // uuid 꺼내서 검증
    @PostMapping("/signup")
    public ResponseEntity<BasicResponse> signup(@RequestBody SignUpDto user) throws NoPermissionException {
        BasicResponse response = authService.signup(user);
        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/reissuance")
    public ResponseEntity<ReissueJwtAuthenticaiton> reissueToken(@RequestHeader HttpHeaders headers, @RequestBody JwtAuthenticationRequest tokenReq){
        ReissueJwtAuthenticaiton response = authService.reissue(tokenReq);
        return ResponseEntity.status(201).body(response);
    }

//
//    @DeleteMapping("/logout")
//    public void logout(HttpServletRequest req, HttpServletResponse res) {
//        LogoutResponseDto dto = authService.logout(req);
//        res.addCookie(dto.getAccessToken());
//        res.addCookie(dto.getRefreshToken());
 //   }

    // 서버에 아직 안 올림, POST - Get으로 변경해야해!!!!!
    @GetMapping("/about-me")
    public ResponseEntity<?> aboutMe(@RequestHeader HttpHeaders headers) {
        try {
            String token = headers.get("Authorization").get(0).substring(7);
            Integer userId = jwtService.extractUserId(token);

            Map<String, Object> response = authService.getAboutMe(userId);

            return ResponseEntity.status(200).body(response);
        }
        catch (Exception e){
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @PatchMapping("/change-nickname")
    public ResponseEntity<?> changeNickname(@RequestHeader HttpHeaders headers, @RequestBody ChangeNicknameRequestDto nicknameDto) {

        try {
            String nickname = nicknameDto.getNickname();
            String token = headers.get("Authorization").get(0).substring(7);
            int userId = jwtService.extractUserId(token);

            ChangeNicknameResponseDto response = authService.changeNickname(nickname, userId);
            return ResponseEntity.status(200).body(response);
        }
        catch (Exception e){
            ExceptionResponse response = new ExceptionResponse(400, e.getMessage());
            return ResponseEntity.status(400).body(response);
        }
    }

    /*
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestHeader HttpHeaders headers){
        try{
            String token = headers.get("Authorization").get(0).substring(7);
            String email = jwtService.extractUserName(token); // email을 추출

            int userId = jwtService.extractUserId(token); // email을 추출

            String accessToken = jwtService.generateToken(user, 1000 * 60 * 60 * 24 * 7, userId);

            mailService.CreateMailWithChangePassword();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return ResponseEntity.status(400).body("");
        }
    }*/


}
