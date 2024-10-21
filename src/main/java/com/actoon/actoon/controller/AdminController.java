package com.actoon.actoon.controller;


import com.actoon.actoon.dto.*;
import com.actoon.actoon.dto.JwtAuthenticationResponse.*;
import com.actoon.actoon.dto.UserDto.*;
import com.actoon.actoon.service.FileUploadService;
import com.actoon.actoon.service.NonUserService;
import com.actoon.actoon.service.WebtoonService;
import com.actoon.actoon.service.interfaces.AuthenticationService;
import com.actoon.actoon.service.interfaces.JwtService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import com.actoon.actoon.dto.WebtoonRequestDto.*;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.naming.NoPermissionException;
import java.sql.SQLException;
import java.util.*;

@RequestMapping("/admin") // admin
@RestController
@Validated
public class AdminController {

    NonUserService nonUserService;
    WebtoonService webtoonService;
    AuthenticationService userService;

    FileUploadService fileService;

    JwtService jwtService;



    public AdminController(NonUserService nonUserService, WebtoonService webtoonService, AuthenticationService userService,
                           FileUploadService fileService, JwtService jwtService){
        this.nonUserService = nonUserService;
        this.webtoonService = webtoonService;
        this.userService = userService;
        this.fileService = fileService;
        this.jwtService = jwtService;
    }

    @PostMapping("/signin")
    public ResponseEntity<IssueJwtAuthentication> adminSignin(@RequestBody SignInDto signInDto){
        IssueJwtAuthentication authenticatedDto = userService.signin(signInDto);
        return ResponseEntity.status(200).body(authenticatedDto);
    }

//    @PostMapping("/signup")
//    public ResponseEntity<BasicResponse> adminSignup(SignUpDto signUpDto) throws NoPermissionException {
//        userService.signup(signUpDto);
//        return ResponseEntity.status(200).body(new BasicResponse(HttpStatus.OK, "어드민 로그인 처리되었습니다."));
//    }

    @GetMapping("/non-users-list") // Get으로 추후 변경
    public ResponseEntity<?> registerList(@RequestParam(required = false, defaultValue="0", value="pageNo") @Min(0) int pageNo,
                                          @RequestParam(required = false, defaultValue="10", value="pageSize") @Min(1) int pageSize
    ) throws NoPermissionException {
            // param의 key는 백엔드가 정해준다.
//            if(bindingResult.hasErrors()){
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
//            }
            try {

                //NonUserDto.NonUserListRequestDto datas = new NonUserDto.NonUserListRequestDto(pageNo, pageSize);

                NonUserDto.NonUserListResponseDto nonUsers = nonUserService.getList(pageNo == 0 ? pageNo : pageNo - 1, pageSize);
                return ResponseEntity.status(200).body(nonUsers);
            }
            catch (Exception e){
                throw new ConstraintViolationException(
                        new HashSet<ConstraintViolation<?>>());
            }

    }

    @GetMapping("/webtoon-request-list")
    public ResponseEntity<WebtoonResponseDto.ReadResponseDto> readAll(@RequestParam(required = false, defaultValue="0", value="pageNo") @Min(value = 0, message = "0보다 같거나 커야합니다.") int pageNo,
                                                          @RequestParam(required = false, defaultValue="10", value="pageSize") @Min(value = 1, message = "페이지 사이즈에 정확한 값을 넣어주세요.") int pageSize) throws NoPermissionException, SQLException {
        //List<SimpleGrantedAuthority> authorities = (ArrayList<SimpleGrantedAuthority>) SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        
        WebtoonResponseDto.ReadResponseDto webtoonList = webtoonService.getAllWebtoons(pageNo == 0 ? pageNo : pageNo - 1, pageSize);
        return ResponseEntity.status(200).body(webtoonList);
    }

    @PatchMapping("/flag/{uuid}/{flag}")
    public ResponseEntity<WebtoonResponseDto> patch(@PathVariable("uuid") int uuid, @PathVariable("flag") boolean flag){
        webtoonService.changeWebtoonRequestFlag(uuid, flag);

        return ResponseEntity.status(200).body(new WebtoonResponseDto(HttpStatus.OK, "웹툰 요청 확인 상태 변경을 확인했습니다."));
    }

    // 실험 필요
    @PatchMapping("/change-progress/{uuid}")
    public ResponseEntity<?> changeProgressState(@PathVariable("uuid") int uuid, @RequestBody WebtoonStateChangeDto changeDto){

        try {
            Map<String, Object> map;

            String progress = changeDto.getProgressState();
            map = webtoonService.changeProgressState(uuid, progress);

            return ResponseEntity.status(200).body(map);
        }
        catch (Exception e){
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @PostMapping(value = "/complete-files/{uuid}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    //return : Resource
    public ResponseEntity<?> uploadCompleteFile(@PathVariable("uuid") int webtoonId, @ModelAttribute UploadFileDto.UploadCompleteFileRequestDto file) {

        try {
            //Resource file = fileService.loadAsResource();
            Map<String, Object> completeFiles = fileService.storeCompleteFile(webtoonId, file);

            return ResponseEntity.status(201).body(completeFiles);
        }
        catch (Exception e){
            System.out.println("EXCEPTION : " + e.getMessage());
            ExceptionResponse errors = new ExceptionResponse(400, e.getMessage());
            return ResponseEntity.status(400).body(errors);
        }
    }



//    // 파일 업로드
//    @PostMapping("/upload/{uuid}") // webtoonId
//    public ResponseEntity<?> uploadFiles(@RequestHeader HttpHeaders headers, @PathVariable("uuid") int webtoonId, @ModelAttribute FileDto.CompleteFileRequestDto files){
//
//        try{
//            String token = headers.get("Authorization").get(0).substring(7);
//            Integer userId = jwtService.extractUserId(token);
//            Map<String, Object> response = fileService.storeCompleteFile(files, userId, webtoonId);
//            return ResponseEntity.ok().body(response);
//        }
//        catch (Exception e){
//            ExceptionResponse response = new ExceptionResponse(400, e.getMessage());
//            return ResponseEntity.ok().body(response);
//        }
//    }

//    @PostMapping("/upload/{uuid}") // webtoonId
//    public ResponseEntity<?> uploadFiles(@RequestHeader HttpHeaders headers,
//                                         @PathVariable("uuid") int webtoonId,
//                                         @ModelAttribute FileDto.CompleteFileRequestDto files,
//                                         @RequestBody ){
//
//        try{
//            String token = headers.get("Authorization").get(0).substring(7);
//            Integer userId = jwtService.extractUserId(token);
//            Map<String, Object> response = fileService.storeCompleteFile(files, userId, webtoonId);
//            return ResponseEntity.ok().body(response);
//        }
//        catch (Exception e){
//            ExceptionResponse response = new ExceptionResponse(400, e.getMessage());
//            return ResponseEntity.ok().body(response);
//        }
//    }

}

