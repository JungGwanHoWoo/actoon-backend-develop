package com.actoon.actoon.service;

import com.actoon.actoon.repository.UserRepository;
import com.actoon.actoon.service.interfaces.UserService;
import com.actoon.actoon.util.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// 사용자 인증을 위해 구현한 서비스 로직
@Service
@RequiredArgsConstructor
public class UserAuthService implements UserService {
    private final UserRepository userRepository;

    // 사용자의 인증 및 권한 부여 정보를 검색
    public UserDetailsService userDetailsService() {

        // 사용자 정보를 제공하기 위해 구현하는 메서드
        // UserDetails : Spring security에서 사용자 정보를 담는 인터페이스
        return new UserDetailsService() {

            // DB에서 role을 확인하는군
            @Override
            public UserDetails loadUserByUsername(String username) {

                var currentUserInfo = userRepository.findByEmail(username)
                        .orElseThrow(() -> new UsernameNotFoundException(""));;

                String curRole = currentUserInfo.getRole().name();
                if(curRole.equals("ROLE_USER")){
                    return currentUserInfo;
                }

                currentUserInfo.setRole(Role.ROLE_ADMIN);
                return currentUserInfo;
            }
        };
    }

}