package com.actoon.actoon.dto;

import com.actoon.actoon.exception.ErrorCode;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
////@ApiModel(value="에러 처리 클래스")
public class ExceptionResponse {

    private Integer status;
    private String message;

    @Builder
    public ExceptionResponse(String message, Integer status) {
        this.status = status;
        this.message = message;
    }

    public ExceptionResponse(ErrorCode errorCode) {
        this.status = errorCode.getStatus();
        this.message = errorCode.getMessage();
    }

    public String getMessage() {
        return message == null ? null : this.message;
    }

    public final void setMessage(String message) {
        if (message == null) {
            this.message = null;
        } else {
            this.message = message;
        }
    }

    public static ExceptionResponse of(ErrorCode errorCode) {
        return new ExceptionResponse(errorCode);
    }

    // 가변인자
    public static List<ExceptionResponse> more(ErrorCode...errorCode){
        List<ExceptionResponse> exList = new ArrayList<>();

        for(int i=0; i<errorCode.length; i++){
            exList.add(new ExceptionResponse(errorCode[i]));
        }

        return exList;
    }


}

