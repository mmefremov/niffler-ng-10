package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.service.AuthApiClient;
import guru.qa.niffler.util.FakerUtils;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(BrowserExtension.class)
class RegistrationTest {

    private static final Config CFG = Config.getInstance();

    @Test
    @DisplayName("Успешная регистрация нового пользователя")
    void shouldRegisterNewUser() {
        String username = FakerUtils.getUserName();
        String password = FakerUtils.getPassword();

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .register()
                .setUsername(username)
                .setPassword(password)
                .setPasswordSubmit(password)
                .signUp()
                .singIn()
                .login(username, password)
                .checkThatPageLoaded();
    }

    @Test
    @DisplayName("Ошибка при попытке регистрации существующего пользователя")
    void shouldNotRegisterUserWithExistingUsername() throws IOException {
        String username = FakerUtils.getUserName();
        String password = FakerUtils.getPassword();
        var response = new AuthApiClient().register(username, password);
        assertThat(response.code()).isEqualTo(HttpStatus.CREATED_201);

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .register()
                .setUsername(username)
                .setPassword(password)
                .setPasswordSubmit(password)
                .signUp()
                .checkFormErrorText("Username `%s` already exists".formatted(username));

    }

    @Test
    @DisplayName("Ошибка при несовпадении паролей")
    void shouldShowErrorIfPasswordAndConfirmPasswordAreNotEqual() {
        String username = FakerUtils.getUserName();

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .register()
                .setUsername(username)
                .setPassword(FakerUtils.getPassword())
                .setPasswordSubmit(FakerUtils.getPassword())
                .signUp()
                .checkFormErrorText("Passwords should be equal");
    }
}
