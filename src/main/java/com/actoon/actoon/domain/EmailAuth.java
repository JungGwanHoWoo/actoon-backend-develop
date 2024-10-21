package com.actoon.actoon.domain;

//import jakarta.persistence.Id;
import org.springframework.data.annotation.Id;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Setter
@ToString
@NoArgsConstructor
@RedisHash(value = "emailAuth", timeToLive = 60 * 5) // 10분으로 설정, 이후 배포할 때는 5분으로 조절
@Builder
public class EmailAuth {

    @Id
    private int uuid;
    private String email;
    private int number;
    private boolean isAuthenticated;

    public EmailAuth(int uuid, String email, int number){
        this.uuid = uuid;
        this.email = email;
        this.number = number;
    }

    public EmailAuth(int uuid, String email, int number, boolean isAuthenticated){
        this.uuid = uuid;
        this.email = email;
        this.number = number;
        this.isAuthenticated = isAuthenticated;
    }
}



