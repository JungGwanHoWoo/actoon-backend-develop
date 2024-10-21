package com.actoon.actoon.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.actoon.actoon.domain.User;
import com.actoon.actoon.repository.UserRepository;

import jakarta.annotation.PostConstruct;

// 더미 데이터 생성하는 class
@Component
public class ProductInitializer {
    private UserRepository userRepository;
    private PasswordEncryptFactory passwordFactory;

    private String password = "123";
    private String email = "admin@temp.com";
    public ProductInitializer(UserRepository userRepository, PasswordEncryptFactory passwordFactory) {
        this.userRepository = userRepository;
        this.passwordFactory = passwordFactory;
    }

    // 프로그램이 실행되면, 자동으로 init 메서드가 실행되도록 구축
    @PostConstruct
    public void init() throws IllegalStateException{

        // email = 어드민 이메일 입력
        // password = 어드민 패스워드 입력

        if(email.isEmpty() || password.isEmpty()) {
            throw new IllegalStateException("[ADMIN] admin 정보가 없습니다.");
        }

        //admin 이메일 입력
        var isExists = userRepository.findByEmail(email);
        if(isExists.isPresent())
            return;

        String encodedPassword = passwordFactory.encode(password);

        User ad =
                User.builder()
                        // 어드민 이메일 입력
                        .email(email)
                        .password(encodedPassword)
                        .role(Role.ROLE_ADMIN)
                .build();


        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();
        String now_dt = format.format(now);

        ad.setCreated_at(now_dt);

        userRepository.save(ad);
    }
}