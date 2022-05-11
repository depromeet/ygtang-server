package inspiration.member;

import inspiration.enumeration.ExceptionType;
import inspiration.enumeration.RedisKey;
import inspiration.exception.EmailAuthenticatedTimeExpiredException;
import inspiration.exception.PostNotFoundException;
import inspiration.exception.UnauthorizedAccessRequestException;
import inspiration.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final RedisService redisService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void updatePassword(Long memberId, String confirmPassword, String password) {

        confirmPasswordCheck(confirmPassword, password);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new PostNotFoundException(ExceptionType.USER_NOT_EXISTS.getMessage()));

        String expiredKey = RedisKey.EAUTH_UPDATE_PASSWORD.getKey() + member.getEmail();

        if (redisService.getData(expiredKey) == null) {
            throw new EmailAuthenticatedTimeExpiredException();
        }

        member.updatePassword(passwordEncoder.encode(password));

        redisService.deleteData(expiredKey);
    }

    private void confirmPasswordCheck(String confirmPasswordCheck, String password) {

        if (!confirmPasswordCheck.equals(password)) {
            throw new PostNotFoundException(ExceptionType.PASSWORD_NOT_MATCHED.getMessage());
        }
    }

    public Member findById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(UnauthorizedAccessRequestException::new);
    }
}
