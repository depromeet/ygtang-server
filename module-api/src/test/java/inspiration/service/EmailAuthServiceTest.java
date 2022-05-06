package inspiration.service;

import inspiration.emailauth.EmailAuthRepository;
import inspiration.emailauth.EmailAuthService;
import inspiration.exception.ConflictRequestException;
import inspiration.exception.PostNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailAuthServiceTest {

    @Mock
    private EmailAuthRepository emailAuthRepository;

    @InjectMocks
    private EmailAuthService emailAuthService;

    @Test
    @DisplayName("이메일 중복으로 인해 인증 링크를 보내는 것에 실패한다.")
    public void sendEmailIfConflict() {

        String email = "test123@gmail.com";

        when(emailAuthRepository.existsByEmail(email)).thenReturn(true);

        assertThrows(ConflictRequestException.class, () -> emailAuthService.sendEmail(email));

        verify(emailAuthRepository, atLeastOnce()).existsByEmail("test123@gmail.com");
    }
}
