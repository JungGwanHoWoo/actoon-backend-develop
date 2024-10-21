package com.actoon.actoon.dto;

import com.actoon.actoon.domain.User;
import lombok.*;
import org.springframework.http.HttpStatus;


@ToString
@Getter
@Setter
public class UserDto {


    @Getter
    @Setter
    public static class UserInfo{
        private String email;
        private String nickname;
        private String profile;

        @Builder
        public User toEntity(){
            User currentUser =
                    User
                    .builder()
                            .email(email)
                            .nickname(nickname)
                            .profile(profile)
                            .build();

            return currentUser;
        }
    }

    @Getter
    @Setter
    public static class SignUpDto{
        private int uuid; // 어뷰징 방지를 위한 uuid
        private String password;
        private String nickname;
        private String email;
        private String birthday; // DATE
        private String profile;
        private String createdAt; // DATE

        // dto 에서 entity로 변환
        @Builder
        public User toEntity(){
            User currentUser = User
                    .builder()
                    .password(password)
                    .email(email)
                    .nickname(nickname)
                    .birthday(birthday)
                    .profile(profile)
                    .build();

            return currentUser;
        }
    }

    @Getter
    @Setter
    public static class SignInDto{

        private String email;
        private String password;

        @Builder
        public User toEntity(){
            User currentUser = User
                    .builder()
                    .email(email)
                    .password(password)
                    .build();

            return currentUser;
        }
    }


    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChangeNicknameRequestDto{
        private String nickname;
    }


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChangeNicknameResponseDto{
        private HttpStatus state;
        private String message;
        private String changedNickname;
    }

}
