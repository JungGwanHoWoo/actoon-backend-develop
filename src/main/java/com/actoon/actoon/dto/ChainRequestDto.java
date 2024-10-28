package com.actoon.actoon.dto;

import com.actoon.actoon.domain.Chain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class ChainRequestDto {

    @Getter
    @Setter
    @AllArgsConstructor
    public static class ChainCreateRequestDTO {

        int style;

        
        Integer fileId; 
        Integer boardId; 

        @Builder
        public Chain toEntity() {
            return Chain.builder()
                    .fileId(fileId)
                    .boardId(boardId)
                    .build();
        }

    }
}