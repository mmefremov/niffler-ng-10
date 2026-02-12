package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.AuthApi;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.service.RestClient;
import io.qameta.allure.Step;
import org.apache.commons.lang3.StringUtils;
import retrofit2.Response;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Objects;

@ParametersAreNonnullByDefault
public final class AuthApiClient extends RestClient {

    private final AuthApi authApi;

    public AuthApiClient() {
        super(CFG.authUrl(), true);
        this.authApi = create(AuthApi.class);
    }

    @Step("Register user '{username}'")
    public Response<Void> register(String username, String password) throws IOException {
        authApi.requestRegisterForm().execute();
        return authApi.register(
                username,
                password,
                password,
                ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN")
        ).execute();
    }

    @Step("Authorize the client")
    public Response<Void> authorize(String codeChallenge) throws IOException {
        return authApi.authorize(
                        "code",
                        "client",
                        "openid",
                        CFG.frontUrl() + "authorized",
                        codeChallenge,
                        "S256")
                .execute();
    }

    @Step("Login user with a password")
    public String login(String username, String password) throws IOException {
        var response = authApi.login(
                        username,
                        password,
                        ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN"))
                .execute();
        return StringUtils.substringAfter(response.raw().request().url().toString(), "code=");
    }

    @Step("Get an access token")
    public String token(String code, String codeVerifier) throws IOException {
        authApi.requestRegisterForm().execute();
        var response = authApi.token(
                        "client",
                        CFG.frontUrl() + "authorized",
                        "authorization_code",
                        code,
                        codeVerifier)
                .execute();
        return Objects.requireNonNull(response.body()).path("id_token").asText();
    }
}
