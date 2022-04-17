package com.depromeet.inspiration.v1.member;

import com.depromeet.inspiration.emailauth.EmailAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {

    private final EmailAuthService emailAuthService;
}
