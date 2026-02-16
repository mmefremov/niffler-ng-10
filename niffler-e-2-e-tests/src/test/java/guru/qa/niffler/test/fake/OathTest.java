package guru.qa.niffler.test.fake;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class OathTest {

    @Test
    @User
    @ApiLogin
    void ouathTest1(@Token String token, UserJson user) {
        log.info("user: {}", user);
        assertThat(token).isNotNull();
    }

    @Test
    @ApiLogin(username = "dima", password = "12345")
    void ouathTest2(@Token String token, UserJson user) {
        log.info("user: {}", user);
        assertThat(token).isNotNull();
    }
}
