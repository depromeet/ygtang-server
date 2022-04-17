package com.depromeet.inspiration.emailauth;

import com.depromeet.inspiration.member.MemberRepository;
import com.depromeet.inspiration.v1.member.dto.request.EmailAuthRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@EnableAsync
@RequiredArgsConstructor
public class EmailAuthService {
    private final JavaMailSender javaMailSender;
    private final EmailAuthRepository emailAuthRepository;

    @Transactional
    public void emailAuthentication(EmailAuthRequest request) {
        EmailAuth emailAuth = emailAuthRepository.save(
                EmailAuth.builder()
                        .email(request.getEmail())
                        .authToken(UUID.randomUUID().toString())
                        .expired(false)
                        .build());

        send(emailAuth.getEmail(), emailAuth.getAuthToken());
    }

    @Async
    public void send(String email, String authToken) {
        SimpleMailMessage smm = new SimpleMailMessage();
        smm.setTo(email);
        smm.setSubject("회원가입 이메일 인증");
        smm.setText("http://localhost:8080/sign/confirm-email?email=" + email + "&authToken=" + authToken);

        javaMailSender.send(smm);
    }
}
