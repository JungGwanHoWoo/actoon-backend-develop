package com.actoon.actoon.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

// 하나는 token, 하나는 email 인증 번호

@AllArgsConstructor
@NoArgsConstructor
public class TokenDto {

    String email;
    String refreshToken;
}
