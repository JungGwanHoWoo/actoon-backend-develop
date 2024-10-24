package com.actoon.actoon.service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import static java.time.temporal.ChronoUnit.DAYS;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.naming.NoPermissionException;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.actoon.actoon.domain.RefreshToken;
import com.actoon.actoon.domain.User;
import com.actoon.actoon.dto.BasicResponse;
import com.actoon.actoon.dto.JwtAuthenticationRequest;
import com.actoon.actoon.dto.JwtAuthenticationResponse.*;
import com.actoon.actoon.dto.JwtAuthenticationResponse.IssueJwtAuthentication;
import com.actoon.actoon.dto.JwtAuthenticationResponse.ReissueJwtAuthenticaiton;
import com.actoon.actoon.dto.UserDto.*;
import com.actoon.actoon.dto.UserDto.ChangeNicknameResponseDto;
import com.actoon.actoon.dto.UserDto.SignInDto;
import com.actoon.actoon.dto.UserDto.SignUpDto;
import com.actoon.actoon.exception.ErrorCode;
import com.actoon.actoon.repository.EmailRedisRepository;
import com.actoon.actoon.repository.TokenRepository;
import com.actoon.actoon.repository.UserRepository;
import com.actoon.actoon.service.interfaces.AuthenticationService;
import com.actoon.actoon.util.PasswordEncryptFactory;
import com.actoon.actoon.util.Role;

import io.jsonwebtoken.security.SignatureException;

// signIn하면 호출된다.
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final PasswordEncryptFactory passwordEncryptFactory;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final JwtServiceImpl jwtService;
    private final EmailRedisRepository emailAuthRepository;


    public AuthenticationServiceImpl(PasswordEncryptFactory passwordEncryptFactory,
                                     UserRepository userRepository,
                                     TokenRepository tokenRepository,
                                     JwtServiceImpl jwtService,
                                     EmailRedisRepository emailAuthRepository
    ){
        this.passwordEncryptFactory = passwordEncryptFactory;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.jwtService = jwtService;
        this.emailAuthRepository = emailAuthRepository;
    }

    // 회원가입 로직
    @Override
    public BasicResponse signup(SignUpDto request) throws NoPermissionException {

        int uuid = request.getUuid(); // 어뷰징 방지를 위한 uuid
        var authObj = emailAuthRepository.findById(uuid)
                .orElseThrow(() -> new NoPermissionException(ErrorCode.REQUEST_EXPIRED.getMessage()));

        if(!authObj.isAuthenticated())
            throw new NoPermissionException(ErrorCode.PERMISSION_DENIED.getMessage());

        if(!(authObj.getEmail().equals(request.getEmail()))) // 이메일 검증
            throw new NoPermissionException(ErrorCode.PERMISSION_DENIED.getMessage());

        System.out.println("AUTH 통과");
        String email = request.getEmail();
        tokenRepository.findById(email);

        var emailChecking = userRepository.findByEmail(request.getEmail());
        var nicknameChecking = userRepository.findByNickname(request.getNickname());
        // 겹치는 아이디 검사
        if(emailChecking.isPresent())
            throw new IllegalStateException(ErrorCode.DUPLICATE_EMAIL.getMessage());
        if(nicknameChecking.isPresent())
            throw new IllegalStateException(ErrorCode.DUPLICATE_NICKNAME.getMessage());


        var user = User.builder()
                    .email(request.getEmail())
                    .password(passwordEncryptFactory.encode(request.getPassword()))
                    .nickname(request.getNickname())
                    .birthday(request.getBirthday())
                    .role(Role.ROLE_USER).build();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();
        String now_dt = format.format(now);

        user.setCreated_at(now_dt);
        System.out.println("DB에 들어가는 유저 정보 : " + user.toString());
        userRepository.save(user);
        return BasicResponse.builder().state(HttpStatus.CREATED).message("회원가입 성공").build();
    }

    // 로그인 로직
    @Override
    public IssueJwtAuthentication signin(SignInDto request) {

//        authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(request.getId(), request.getPassword()));

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException(ErrorCode.INVALID_INPUT.getMessage()));

        if (!passwordEncryptFactory.decode(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException(ErrorCode.INVALID_INPUT.getMessage());
        }

        int userId = user.getUuid();

        var accessToken = jwtService.generateToken(user, 1000 * 60 * 60 * 24 * 7, userId); // test - 20초 / 본 개발 - 20분
        var refreshToken = jwtService.generateToken(user, 1000 * 60 * 60 * 24 * 7, userId); // test - 5분, / 배포 - 7일

        return IssueJwtAuthentication.builder()
                                        .state(HttpStatus.OK)
                                        .message("로그인 성공")
                                        .accessToken(accessToken)
                                        .refreshToken(refreshToken)
                                        .build();
    }

    // 토큰 정보 DB에 저장
    public void storeToken(String accessToken, String refreshToken){

        RefreshToken tokenInfo = RefreshToken
                                    .builder()
                                    .refreshToken(refreshToken)
                                    .accessToken(accessToken)
                                    .build();

        tokenRepository.save(tokenInfo);
    }

    // 리프레쉬 토큰 만료 체크 및 액세스 토큰 만료 시 재발행하는 로직
    public ReissueJwtAuthenticaiton reissue(JwtAuthenticationRequest req){

        String accessToken = req.getAccessToken();
        String acceptedRefreshToken = req.getRefreshToken();

        // 만일 존재하지 않는 토큰이라면 리프레쉬 만료
        RefreshToken result =  tokenRepository.findById(acceptedRefreshToken)
                .orElseThrow(() -> new IllegalStateException(ErrorCode.REF_TOKEN_EXPIRED.getMessage()));

        if(!(accessToken.equals(result.getAccessToken()))) {
            throw new SignatureException(ErrorCode.INVALID_TOKEN.getMessage());
        }

        String email = jwtService.extractUserName(accessToken);
        System.out.println("JWT EXTRACT EMAIL : " + email);

        var user = userRepository.findByEmail(email).get();
        //var accessToken = jwtService.generateToken(user, 1000*60*20);
        var newAccessToken = jwtService.generateToken(user, 1000*60 * 60 * 24 * 7);

        return new ReissueJwtAuthenticaiton(HttpStatus.CREATED, "액세스 토큰을 재발급합니다.", newAccessToken);
    }

    @Transactional
    public ChangeNicknameResponseDto changeNickname(String nickname, int userId) {

        User user = userRepository.findById(userId).get();

        String last_change = user.getLast_change();
        System.out.println("CHANGE NICKNAME : " + nickname);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDate updated = LocalDate.parse(last_change, formatter);
        LocalDate now = LocalDate.now();

        long days = DAYS.between(updated, now);

        ChangeNicknameResponseDto response = new ChangeNicknameResponseDto();

        if (days <= 7) {
            response.setState(HttpStatus.BAD_REQUEST);
            response.setMessage("닉네임은 마지막 변경일 기준 7일 이내로는 변경이 불가능합니다.");
        } else {

            user.setNickname(nickname);
            response.setState(HttpStatus.OK);
            response.setMessage("닉네임이 변경되었습니다.");
            response.setChangedNickname(nickname);
        }

        return response;
    }

    public Map<String, Object> getAboutMe(int userId) {

    Map<String, Object> aboutMe = new HashMap<>();

    Optional<User> user = userRepository.findByUuid(userId);

    if (user.isEmpty()) {
        aboutMe.put("state", HttpStatus.BAD_REQUEST);
        aboutMe.put("message", "로그인 해주세요.");
        return aboutMe;
    }

    User curUser = user.get();

    String email = curUser.getEmail();
    String nickname = curUser.getNickname();
    String last_change = curUser.getLast_change();
    String profile = curUser.getProfile();

    // 수정된 포맷: 마이크로초까지 처리
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");

    // LocalDateTime으로 변경
    LocalDateTime updated = LocalDateTime.parse(last_change, formatter);
    LocalDateTime now = LocalDateTime.now();

    // 날짜 차이를 계산 (일 단위)
    long days = ChronoUnit.DAYS.between(updated, now);
    boolean changeable = days > 7;

    aboutMe.put("state", HttpStatus.OK);
    aboutMe.put("message", "내 정보를 불러왔습니다.");
    aboutMe.put("email", email);
    aboutMe.put("nickname", nickname);
    aboutMe.put("changeable", changeable);
    aboutMe.put("profile_url", profile);

    return aboutMe;
}
}