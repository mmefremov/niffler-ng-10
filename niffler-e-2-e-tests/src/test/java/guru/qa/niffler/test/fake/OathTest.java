package guru.qa.niffler.test.fake;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.impl.AuthApiClient;
import guru.qa.niffler.utils.OauthUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled
@Slf4j
class OathTest {

    private final AuthApiClient authApi = new AuthApiClient();

    @Test
    @User
    void ouathTest(UserJson user) throws IOException {
        String codeVerifier = OauthUtils.generateCodeVerifier();
        String codeChallenge = OauthUtils.generateCodeChallenge(codeVerifier);
        authApi.authorize(codeChallenge);
        String code = authApi.login(user.username(), user.testData().password());
        String token = authApi.token(code, codeVerifier);
        log.info("token: {}", token);
        assertThat(token).isNotNull();
    }
}
