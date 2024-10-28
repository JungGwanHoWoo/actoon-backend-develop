package com.actoon.actoon.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.actoon.actoon.dto.ExceptionResponse;
import com.actoon.actoon.dto.UploadFileDto.UploadFileRequestDto;
import com.actoon.actoon.service.FileUploadService;
import com.actoon.actoon.service.JwtServiceImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/upload")
public class FileUploadController {

    @Autowired
    private FileUploadService fileService;

    @Autowired
    JwtServiceImpl jwtService;

//
//    @PostMapping(path = "/profile", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
//    public ResponseEntity<> setProfile(
//            @RequestPart(value = "name") String name,
//            @RequestPart(value = "file", required = false) MultipartFile file
//    ){
//        fileServicervice.createTeam(name, file);
//        return ResponseEntity.status(200).body();
//    }
    @PostMapping(path = "/webtoon-file", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> storeWebtoonFiles(@ModelAttribute UploadFileRequestDto files) throws IOException {
        System.out.println("FILE CHECK : " + files.getFile());

        try {
            Map<String, Object> fileInfo = fileService.storeWebtoonFile(files);

            return ResponseEntity.status(200).body(fileInfo);
        } catch (Exception e) {
            System.out.println("EXCEPTION : " + e.getMessage());
            ExceptionResponse errors = new ExceptionResponse(400, e.getMessage());
            return ResponseEntity.status(400).body(errors);
        }
        //return null;
    }

    @PostMapping(path = "/noticeBoard-file", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> storeNoticeBoardFiles(@ModelAttribute UploadFileRequestDto uploadFiles) throws IOException {
        System.out.println("FILE CHECK : " + uploadFiles.getFiles());

        try {
            List<Integer> fileIds = new ArrayList<>(); // fileId 리스트 생성

            for (MultipartFile file : uploadFiles.getFiles()) {
                Map<String, Object> fileInfo = fileService.storeNoticeBoardFile(file);
                fileIds.add((Integer) fileInfo.get("fileId")); // 각 fileId를 리스트에 추가
            }

            Map<String, Object> response = new HashMap<>();
            response.put("fileIds", fileIds); // 모든 fileId를 포함한 응답 생성

            return ResponseEntity.status(200).body(response);
        } catch (Exception e) {
            System.out.println("EXCEPTION : " + e.getMessage());
            ExceptionResponse errors = new ExceptionResponse(400, e.getMessage());
            return ResponseEntity.status(400).body(errors);
        }
    }

    // method 추가
    @GetMapping(value = "/download")
    //return : Resource
    public ResponseEntity<?> serveFile(@RequestParam(value = "filename") String filename) {

        try {
            Resource file = fileService.loadAsResource(filename);

            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + file.getFilename() + "\"").body(file);
        } catch (Exception e) {
            System.out.println("EXCEPTION : " + e.getMessage());
            ExceptionResponse errors = new ExceptionResponse(400, e.getMessage());
            return ResponseEntity.status(400).body(errors);
        }

    }

    @GetMapping("/{filename}")
    public ResponseEntity<Resource> serveImgFile(@PathVariable String filename) {
        try {
            Resource file = fileService.loadAsResource(filename); // 저장된 파일 불러오기
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
                    .body(file);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping(path = "/profile", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> storeProfile(@RequestHeader HttpHeaders headers, @ModelAttribute UploadFileRequestDto file) throws IOException {
        try {
            String token = headers.get("Authorization").get(0).substring(7);
            Integer userId = jwtService.extractUserId(token);

            Map<String, Object> fileInfo = fileService.storeUserProfileFile(file, userId);
            return ResponseEntity.status(200).body(fileInfo);
        } catch (Exception e) {
            System.out.println("EXCEPTION : " + e.getMessage());
            ExceptionResponse errors = new ExceptionResponse(400, e.getMessage());
            return ResponseEntity.status(400).body(errors);
        }
    }

//    @PostMapping("/test")
//    public ResponseEntity<?> uploadFile(){
//
//
//
//        return ResponseEntity.ok().body("");
//    }
}
