package com.actoon.actoon.controller;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.naming.NoPermissionException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.actoon.actoon.dto.ExceptionResponse;
import com.actoon.actoon.dto.NoticeBoardRequestDTO.NoticeBoardCreateRequestDTO;
import com.actoon.actoon.dto.NoticeBoardRequestDTO.NoticeBoardUpdateRequestDTO;
import com.actoon.actoon.dto.NoticeBoardResponseDTO;
import com.actoon.actoon.dto.NoticeBoardResponseDTO.NoticeBoardStateResponseDto;
import com.actoon.actoon.dto.NoticeBoardResponseDTO.ReadResponseDto;
import com.actoon.actoon.service.AuthenticationServiceImpl;
import com.actoon.actoon.service.NoticeBoardService;
import com.actoon.actoon.service.interfaces.JwtService;

import jakarta.validation.constraints.Min;

@CrossOrigin
@RestController
@RequestMapping("/noticeboard")
public class NoticeBoardController {

    AuthenticationServiceImpl authService;
    NoticeBoardService noticeBoardService;
    JwtService jwtService;

    public NoticeBoardController(AuthenticationServiceImpl authService, NoticeBoardService noticeBoardService, JwtService jwtService) {
        this.authService = authService;
        this.noticeBoardService = noticeBoardService;
        this.jwtService = jwtService;
    }

    @GetMapping("")
    public ResponseEntity<ReadResponseDto> read(@RequestParam(required = false, defaultValue = "0", value = "pageNo") @Min(0) int pageNo,
            @RequestParam(required = false, defaultValue = "10", value = "pageSize") @Min(1) int pageSize)
            throws NoPermissionException, SQLException {

        NoticeBoardResponseDTO.ReadResponseDto noticeBoardList = noticeBoardService.getAllNoticeBoards(pageNo == 0 ? pageNo : pageNo - 1, pageSize);
        return ResponseEntity.status(200).body(noticeBoardList);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<?> read(@PathVariable("uuid") int uuid) {

        try {
            Map<String, Object> foundNoticeBoard = noticeBoardService.getNoticeBoard(uuid);
            return ResponseEntity.status(200).body(foundNoticeBoard);
        } catch (Exception e) {
            ExceptionResponse response = new ExceptionResponse(400, e.getMessage());
            return ResponseEntity.status(400).body(response);
        }
    }

    @PostMapping("")
    public ResponseEntity<?> create(@RequestHeader HttpHeaders headers, @RequestBody NoticeBoardCreateRequestDTO noticeBoardInfo) {
        try {
            System.out.println(noticeBoardInfo.toString());
            String token = headers.get("Authorization").get(0).substring(7);
            String email = jwtService.extractUserName(token);
            Map<String, Object> result = noticeBoardService.createNoticeBoard(email, noticeBoardInfo);

            return ResponseEntity.status(201).body(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();

            result.put("state", HttpStatus.BAD_REQUEST);
            result.put("message", e.getMessage());
            return ResponseEntity.status(400).body(result);
        }
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<?> update(
            @RequestHeader HttpHeaders headers,
            @PathVariable ("uuid") int noticeBoardId,
            @RequestBody NoticeBoardUpdateRequestDTO noticeBoardInfo) {
        try {
            System.out.println(noticeBoardInfo.toString());
            // JWT 토큰에서 Bearer 제거 후 이메일 추출
            String token = headers.get("Authorization").get(0).substring(7);
            String email = jwtService.extractUserName(token);

            // 서비스 메서드 호출
            Map<String, Object> result = noticeBoardService.updateNoticeBoard(email, noticeBoardId, noticeBoardInfo);

            return ResponseEntity.status(HttpStatus.OK).body(result);
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("state", HttpStatus.BAD_REQUEST);
            result.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<?> delete(@RequestHeader HttpHeaders headers, @PathVariable("uuid") int noticeBoardId) {
        try {
            String token = headers.get("Authorization").get(0).substring(7);
            jwtService.extractUserName(token);
            noticeBoardService.deleteNoticeBoard(noticeBoardId);

            return ResponseEntity.status(204).build();
        } catch (Exception e) {
            ExceptionResponse response = new ExceptionResponse(400, e.getMessage());
            return ResponseEntity.status(400).body(response);
        }
    }

    @GetMapping("/noticeboard-state")
    public ResponseEntity<?> getAllWebtoonState(@RequestHeader HttpHeaders headers) {

        try {
            String token = headers.get("Authorization").get(0).substring(7);
            Integer userId = jwtService.extractUserId(token);

            NoticeBoardStateResponseDto response = noticeBoardService.getNoticeBoardState(userId);

            return ResponseEntity.status(200).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
}
