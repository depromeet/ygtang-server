package inspiration.auth;

import inspiration.ResultResponse;
import inspiration.auth.jwt.JwtProvider;
import inspiration.auth.request.LoginRequest;
import inspiration.domain.member.Member;
import inspiration.domain.member.MemberRepository;
import inspiration.domain.member.response.MemberInfoResponse;
import inspiration.enumeration.ExceptionType;
import inspiration.enumeration.ExpireTimeConstants;
import inspiration.exception.PostNotFoundException;
import inspiration.exception.RefreshTokenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    private final String issueToken = "refreshToken : ";

    @Transactional
    public ResultResponse<TokenResponse> login(LoginRequest request) {

        Member member = checkEmail(request.getEmail());
        verifyPassword(request.getPassword(), member.getPassword());

        TokenResponse tokenResponse = TokenResponse.builder()
                                                   .accessToken(resolveAccessToken(member.getId()))
                                                   .refreshToken(resolveRefreshToken(member.getId()))
                                                   .memberId(member.getId())
                                                   .accessTokenExpireDate(ExpireTimeConstants.accessTokenValidMillisecond)
                                                   .build();

        return ResultResponse.of(issueToken, tokenResponse);
    }

    private Member checkEmail(String email) {
        return memberRepository.findByEmailAndStatusIsNull(email)
                               .orElseThrow(() -> new PostNotFoundException(ExceptionType.EMAIl_NOT_FOUND.getMessage()));
    }

    private void verifyPassword(String requestPassword, String realPassword) {
        if (!passwordEncoder.matches(requestPassword, realPassword)) {
            throw new PostNotFoundException(ExceptionType.PASSWORD_NOT_MATCHED.getMessage());
        }
    }

    private String resolveAccessToken(Long memberId) {
        return jwtProvider.createAccessToken(memberId);
    }

    private String resolveRefreshToken(Long memberId) {
        return jwtProvider.createRefreshToken(memberId);
    }

    @Transactional
    public ResultResponse reissue(String refreshToken) {
        Long memberId = jwtProvider.resolveMemberId(refreshToken)
                                   .orElseThrow(RefreshTokenException::new);
        Member member = memberRepository.findById(memberId)
                                        .orElseThrow(() -> new PostNotFoundException(ExceptionType.MEMBER_NOT_FOUND.getMessage()));

        TokenResponse newTokenResponse = jwtProvider.createTokenDto(member.getId());

        return ResultResponse.of(issueToken, newTokenResponse);
    }

    @Transactional(readOnly = true)
    public MemberInfoResponse getUserInfo(Long memberId) {
        return memberRepository.findById(memberId)
                               .map(MemberInfoResponse::of)
                               .orElseThrow(() -> new PostNotFoundException(ExceptionType.USER_NOT_EXISTS.getMessage()));
    }
}
