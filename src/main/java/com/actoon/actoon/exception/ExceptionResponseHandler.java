package com.actoon.actoon.exception;

import com.actoon.actoon.dto.ExceptionResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.NoPermissionException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ExceptionResponseHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionResponseHandler.class);
    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ExceptionResponse> handleSignatureException(SignatureException e) {
        LOGGER.info(e.getMessage());
        return ResponseEntity.status(403).body(new ExceptionResponse("토큰이 유효하지 않습니다.", HttpStatus.UNAUTHORIZED.value()));
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ExceptionResponse> handleMalformedJwtException() {
        return ResponseEntity.status(403).body(new ExceptionResponse("올바르지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED.value()));
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ExceptionResponse> handleExpiredJwtException() {
        return ResponseEntity.status(401).body(new ExceptionResponse("토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED.value()));
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ExceptionResponse> NullPointerExceptionHandler(NullPointerException e){ // 400
        ExceptionResponse response = ExceptionResponse.of(ErrorCode.POST_RESOUCE_NOT_FOUND);
        return ResponseEntity.status(404).body(response);
    }


    @ExceptionHandler(NoPermissionException.class)
    public ResponseEntity<ExceptionResponse> ForbiddenExceptionHandler(NoPermissionException e){
        LOGGER.info("Exeption Error :" + e.getMessage());
        ExceptionResponse response = ExceptionResponse.of(ErrorCode.PERMISSION_DENIED);
        return ResponseEntity.status(403).body(response);
    }
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> WrongInfoExceptionHandler(Exception e){
        LOGGER.info("Exeption Error :" + e.getMessage());
        ExceptionResponse response = ExceptionResponse.of(ErrorCode.INVALID_INPUT);
        return ResponseEntity.status(400).body(response);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ExceptionResponse> UserNotFoundExceptionHandler(Exception e){
        LOGGER.info("Exeption Error :" + e.getMessage());
        ExceptionResponse response = ExceptionResponse.of(ErrorCode.PERMISSION_DENIED);
        return ResponseEntity.status(403).body(response);
    }


    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ExceptionResponse> IllegalStateExceptionHandler(IllegalStateException e){ //409
        LOGGER.info("ERROR : "+e.getMessage());
        String ERROR_MESSAGE = e.getMessage();

        if(ERROR_MESSAGE.contains("이메일")){
            ExceptionResponse response = ExceptionResponse.of(ErrorCode.DUPLICATE_EMAIL);
            return ResponseEntity.status(400).body(response);
        }
        else if(ERROR_MESSAGE.contains("닉네임")){
            ExceptionResponse response = ExceptionResponse.of(ErrorCode.DUPLICATE_NICKNAME);
            return ResponseEntity.status(400).body(response);
        }
        else if(ERROR_MESSAGE.contains("유효하지")){
            ExceptionResponse response = ExceptionResponse.of(ErrorCode.INVALID_TOKEN);
            return ResponseEntity.status(403).body(response);
        }
        else if(ERROR_MESSAGE.contains("refresh")){
            ExceptionResponse response = ExceptionResponse.of(ErrorCode.REF_TOKEN_EXPIRED);
            return ResponseEntity.status(401).body(response);
        }
        else{
            ExceptionResponse response = new ExceptionResponse(400, e.getMessage());
            return ResponseEntity.status(401).body(response);
        }

    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> constraintViolationException(ConstraintViolationException ex) {
        Map<String, Object> errors = new HashMap<>();

        //PROPERTYPATH : registerList.pageNo

        ex.getConstraintViolations().forEach(cv -> {
            String key = cv.getPropertyPath().toString().split("\\.")[1];
            errors.put(key, cv.getInvalidValue());
        });

        errors.put("message", "파라미터에 비정상적인 값이 들어왔습니다. 확인해주세요.");

        Map<String, Map<String, Object>> result = new HashMap<>();
        result.put("errors", errors);

        return ResponseEntity.status(400).body(result);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> SQLExceptionHandler(SQLException e){
        LOGGER.info("Exeption Error :" + e.getMessage());
        LOGGER.info("SQL STATE :" + e.getSQLState());

        ExceptionResponse response = new ExceptionResponse(400, e.getSQLState());
        return ResponseEntity.status(400).body(response);
    }



}