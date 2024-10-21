package com.actoon.actoon.controller;

import com.actoon.actoon.dto.ExceptionResponse;
import com.actoon.actoon.dto.WebtoonRequestDto.*;
import com.actoon.actoon.dto.WebtoonResponseDto;
import com.actoon.actoon.dto.WebtoonResponseDto.*;
import com.actoon.actoon.service.AuthenticationServiceImpl;
import com.actoon.actoon.service.WebtoonService;
import com.actoon.actoon.service.interfaces.JwtService;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.NoPermissionException;
import java.util.HashMap;
import java.util.Map;


@CrossOrigin
@RestController
@RequestMapping("/webtoon")
//@PreAuthorize("isAuthenticated() and (( #user.name == principal.name ) or hasRole('ROLE_USER'))")
public class WebtoonController {

    AuthenticationServiceImpl authService;
    WebtoonService webtoonService;

    JwtService jwtService;

    public WebtoonController(AuthenticationServiceImpl authService, WebtoonService webtoonService, JwtService jwtService){
        this.authService = authService;
        this.webtoonService = webtoonService;
        this.jwtService = jwtService;
    }

    @GetMapping("")
    public ResponseEntity<ReadResponseDto> read(@RequestHeader HttpHeaders headers,
                                                @RequestParam(required = false, defaultValue="0", value="pageNo") @Min(0) int pageNo,
                                                @RequestParam(required = false, defaultValue="10", value="pageSize") @Min(1) int pageSize,
                                                @RequestParam(required = false, value="unreadSorted") boolean unreadSorted,
                                                @RequestParam(required = false, value="filter") String filter
                                                ) throws NoPermissionException {
        //boolean의 기본은 false

        String token = headers.get("Authorization").get(0).substring(7);
        Integer userId = jwtService.extractUserId(token);

        System.out.println("USERID 입니다.");
        System.out.println(userId);

        WebtoonResponseDto.ReadResponseDto webtoonList = webtoonService.getAllWebtoons(pageNo == 0 ? pageNo : pageNo - 1, pageSize, unreadSorted, filter, userId);
        return ResponseEntity.status(200).body(webtoonList);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<?> read(@PathVariable("uuid") int uuid){

        try {
            Map<String, Object> foundWebtoon = webtoonService.getWebtoon(uuid);
            return ResponseEntity.status(200).body(foundWebtoon);
        }
        catch (Exception e){
            ExceptionResponse response = new ExceptionResponse(400, e.getMessage());
            return ResponseEntity.status(400).body(response);
        }
    }

    @PostMapping("")
    public ResponseEntity<?> create(@RequestHeader HttpHeaders headers, @RequestBody WebtoonCreateRequestDto webtoonInfo){
        try {
            System.out.println(webtoonInfo.toString());
            String token = headers.get("Authorization").get(0).substring(7);
            String email = jwtService.extractUserName(token);
            Map<String, Object> result = webtoonService.createWebtoon(email, webtoonInfo);

            return ResponseEntity.status(201).body(result);
        }
        catch(Exception e){
            System.out.println("CREATE :" + e.getMessage());
            Map<String, Object> result = new HashMap<>();

            result.put("state", HttpStatus.BAD_REQUEST);
            result.put("message", e.getMessage());
            return ResponseEntity.status(400).body(result);
        }
    }


    @PatchMapping("/{uuid}")
    public ResponseEntity<WebtoonResponseDto> patch(@PathVariable("uuid") int webtoonId) throws NoPermissionException {

        webtoonService.patchWebtoon(webtoonId);
        return ResponseEntity.status(200).body(new WebtoonResponseDto(HttpStatus.OK, "읽음 처리가 완료되었습니다."));
    }


    @GetMapping("/webtoon-state")
    public ResponseEntity<?> getAllWebtoonState(@RequestHeader HttpHeaders headers){

        try {
            String token = headers.get("Authorization").get(0).substring(7);
            Integer userId = jwtService.extractUserId(token);

            WebtoonStateResponseDto response = webtoonService.getWebtoonState(userId);

            return ResponseEntity.status(200).body(response);
        }
        catch (Exception e){
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }


}
