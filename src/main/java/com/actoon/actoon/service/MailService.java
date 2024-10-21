package com.actoon.actoon.service;

import com.actoon.actoon.domain.EmailAuth;
import com.actoon.actoon.dto.BasicResponse;
import com.actoon.actoon.dto.MailDto;
import com.actoon.actoon.dto.MailDto.*;
import com.actoon.actoon.exception.ErrorCode;
import com.actoon.actoon.repository.EmailRedisRepository;
import com.actoon.actoon.service.interfaces.JwtService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


// 메일 요청 로직 처리하는 서비스 로직 코드
@Service
@RequiredArgsConstructor
public class MailService {

    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private JwtService jwtService;

    private static final String senderEmail= "";
    private static int number;

    @Autowired
    private EmailRedisRepository emailAuthRepository;

    public static void createNumber(){
        number = (int)(Math.random() * (90000)) + 100000;// (int) Math.random() * (최댓값-최소값+1) + 최소값
    }

    // 이메일에 담길 내용을 생성하는 로직
    public MimeMessage createMail(String mail){
        createNumber();
        MimeMessage message = javaMailSender.createMimeMessage();

        // 레디스에 number를 저장하고, 사용자가 입력한 문자열과 비교한다.
        try {
            message.setFrom(senderEmail);
            message.setRecipients(MimeMessage.RecipientType.TO, mail);
            message.setSubject("이메일 인증");
            String body = "";
            body += "<h3>" + "요청하신 인증 번호입니다." + "</h3>";
            body += "<h1>" + number + "</h1>";
            body += "<h3>" + "감사합니다." + "</h3>";
            message.setText(body,"UTF-8", "html");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return message;
    }

    // 이메일에 담길 내용을 생성하는 로직
    public MimeMessage CreateMailWithChangePassword(String mail, String url){

        MimeMessage message = javaMailSender.createMimeMessage();

        // 토큰 생성
        //String token = jwtService.generateToken(user, 1000 * 60 * 60 * 24 * 7, userId); //

        //String link = "https://actoon.kr/password/reset?token=" + token;

        //
        try {
            message.setFrom(senderEmail);
            message.setRecipients(MimeMessage.RecipientType.TO, mail);
            message.setSubject("비밀번호 변경 요청으로 보내는 메일입니다.");
            String body = "";
            body += "<h3>" + "링크를 눌러 비밀번호를 변경하세요." + "</h3>";
            body += "<h1>" + number + "</h1>";
            body += "<h3>" + "만일 사용자가 보낸 요청이 아니라면, 즉시 관리자에게 문의하세요." + "</h3>";
            message.setText(body,"UTF-8", "html");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return message;
    }






    // 사용자가 입력한 숫자와의 검증을 위해 생성한 번호를 저장하는 로직
    public void storeNumbertoRedis(MailDto req){
        //redisService.addAuth(new emailAuthDto(email, number));

        String email = req.getEmail();
        int uuid = req.getUuid();

        EmailAuth auth = new EmailAuth(uuid, email, number, false);

        emailAuthRepository.save(auth);
    }

    // 서버가 생성한 번호와 사용자가 입력한 번호가 동일한지를 체크하는 로직
    @Transactional
    public BasicResponse certificateEmail(EmailAuthDto authDto){

        int sentNumber = authDto.getNumber();
        int key = authDto.getUuid();

        var authObj = emailAuthRepository.findById(key)
                .orElseThrow(() -> new AccessDeniedException(ErrorCode.ACC_TOKEN_EXPIRED.getMessage()));

        int realNumber = authObj.getNumber();

        if(sentNumber != realNumber)
            throw new AuthenticationServiceException(ErrorCode.PERMISSION_DENIED.getMessage());

        authObj.setAuthenticated(true);
        emailAuthRepository.save(authObj);

        return new BasicResponse(HttpStatus.OK, "인증이 성공적으로 완료되었습니다.");
    }

    // 사용자에게 메일을 전달하는 로직
    public int sendMail(String mail){
        MimeMessage message = createMail(mail);
        javaMailSender.send(message);

        return number;
    }


}