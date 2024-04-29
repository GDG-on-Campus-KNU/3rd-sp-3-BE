package gdsc.comunity.security.jwt;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ActiveProfiles("dev")
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
class RefreshTokenRepositoryTest {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    @DisplayName("userId로 RefreshToken 조회 테스트")
    void test() {
        //given
        String refreshTokenStr = "test";
        Long userId = 1L;
        RefreshToken refreshToken = RefreshToken.builder()
                .refreshToken(refreshTokenStr)
                .userId(userId)
                .build();
        refreshTokenRepository.save(refreshToken);

        //when
        Optional<RefreshToken> findRefreshToken = refreshTokenRepository.findByUserId(userId);

        //then
        assertTrue(findRefreshToken.isPresent());
        assertEquals(findRefreshToken.get().getRefreshToken(), refreshTokenStr);
    }
}