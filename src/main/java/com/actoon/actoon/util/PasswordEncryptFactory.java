package com.actoon.actoon.util;

import com.actoon.actoon.exception.ErrorCode;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;


// 비밀번호 암호화 로직을 처리하는 팩토리 클래스
@Component
public class PasswordEncryptFactory {

    private final BCryptPasswordEncoder passwordEncoder;

    public PasswordEncryptFactory(BCryptPasswordEncoder passwordEncoder){
        this.passwordEncoder = passwordEncoder;
    }

    // 패스워드 인코딩 로직
    public String encode(String password){
        return passwordEncoder.encode(password);
    }

    // 패스워드 디코딩 로직
    public boolean decode(String verified, String entered){
        if (!passwordEncoder.matches(verified, entered)) {
            throw new BadCredentialsException(ErrorCode.INVALID_INPUT.getMessage());
        }

        return true;
    }

}
