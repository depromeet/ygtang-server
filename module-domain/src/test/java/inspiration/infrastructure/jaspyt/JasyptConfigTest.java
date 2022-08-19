package inspiration.infrastructure.jaspyt;

import com.ulisesbocchio.jasyptspringboot.encryptor.DefaultLazyEncryptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Run > Edit Configurations > Configuration > Environment variables > 'JASYPT_ENCRYPTOR_PASSWORD={암호화키}' 입력
 */
@Disabled("필요할 때만 사용하기 위해 disabled 처리함")
@SpringBootTest
class JasyptConfigTest {

    @Autowired
    private ConfigurableEnvironment configurableEnvironment;

    @Value("${jasypt.encryptor.password}")
    private String jasyptEncryptorPassword;

    private DefaultLazyEncryptor encryptor;

    @BeforeEach
    void setUp() throws Exception {
        System.out.println(jasyptEncryptorPassword);
        if (StringUtils.isBlank(jasyptEncryptorPassword)) {
            throw new Exception("jasypt.encryptor.password must not be null, empty or blank.");
        }
        encryptor = new DefaultLazyEncryptor(configurableEnvironment);
    }

    @Test
    void testForEncryption() {
        String source = "string want to encrypt";
        String encrypted = encryptor.encrypt(source);
        System.out.println("source: " + source);
        System.out.println("encrypted: " + encrypted);
    }

    @Test
    void testForDecryption() {
        // 암호화 되지 않은 문자열을 넣으면 복호화시 에러 발생함
        String source = "string want to decrypt";
        String decrypted = encryptor.decrypt(source);
        System.out.println("source: " + source);
        System.out.println("encrypted: " + decrypted);
    }
}