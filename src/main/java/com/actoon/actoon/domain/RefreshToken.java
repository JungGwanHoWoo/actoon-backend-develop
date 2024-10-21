package com.actoon.actoon.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;


//@RedisHash(value = "refreshToken", timeToLive = 60 * 60 * 24 * 7) // 7일
@RedisHash(value = "refreshToken", timeToLive = 60 * 60 * 5) // 배포 - 7일, 테스트 - 5분
@Builder
@NoArgsConstructor
@Setter
@Getter
public class RefreshToken {
    @Id
    private String refreshToken;
    private String accessToken;

    public RefreshToken(String refreshToken, String accessToken){
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
    }
}
