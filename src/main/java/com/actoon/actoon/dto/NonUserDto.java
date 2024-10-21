package com.actoon.actoon.dto;


import com.actoon.actoon.domain.NonUser;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class NonUserDto {


    // registerDto
    @Setter
    @AllArgsConstructor
    public static class NonUserRegisterRequestDto{
        String email;
        String created_at;

        public NonUserRegisterRequestDto(NonUser nu){
            this.email = nu.getEmail();
            this.created_at = nu.getCreated_at();
        }

        @Builder
        public NonUser toEntity(){
            return NonUser
                    .builder()
                    .email(email)
                    .created_at(created_at)
                    .build();

        }
    }


    // 여기서 몇 번까지 enum인지 정보를 줘야하나?

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class NonUserListResponseDto{
        HttpStatus state;
        String message;

        int totalPages;
        long totalElements;
        int maximumElementsPerPage;

        boolean last;

        List<NonUserInfoDto> datas;

        public NonUserListResponseDto(HttpStatus state, String message, List<NonUserInfoDto> datas, int totalPages, long totalElements, int maximumElementsPerPage, boolean last){
            this.state = state;
            this.message = message;
            this.datas = datas;
            this.totalPages = totalPages;
            this.totalElements = totalElements;
            this.maximumElementsPerPage = maximumElementsPerPage;
            this.last = last;
        }

        public static NonUserListResponseDto of(NonUser nonUser){
            return NonUserListResponseDto.builder()
                    .state(HttpStatus.OK)
                    .message("비회원 리스트입니다.")
                    .build();
        }
    }

    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NonUserInfoDto{
        int uuid;
        String email;
        String created_at;


        public static NonUserInfoDto of(NonUser nonUser){
            return NonUserInfoDto.builder()
                    .uuid(nonUser.getUuid())
                    .email(nonUser.getEmail())
                    .created_at(nonUser.getCreated_at())
                    .build();
        }

    }



}
