package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
class RegistrationTest {

    @Test
    @DisplayName("Успешная регистрация нового пользователя")
    void shouldRegisterNewUser() {
        String username = RandomDataUtils.randomUsername();
        String password = RandomDataUtils.randomPassword();

        Selenide.open(LoginPage.URL, LoginPage.class)
                .register()
                .setUsername(username)
                .setPassword(password)
                .setPasswordSubmit(password)
                .signUp()
                .singIn()
                .login(username, password)
                .checkThatPageLoaded();
    }

    @User
    @Test
    @DisplayName("Ошибка при попытке регистрации существующего пользователя")
    void shouldNotRegisterUserWithExistingUsername(UserJson user) {
        Selenide.open(LoginPage.URL, LoginPage.class)
                .register()
                .setUsername(user.username())
                .setPassword(user.testData().password())
                .setPasswordSubmit(user.testData().password())
                .signUp()
                .checkFormErrorText("Username `%s` already exists".formatted(user.username()));
    }

    @Test
    @DisplayName("Ошибка при несовпадении паролей")
    void shouldShowErrorIfPasswordAndConfirmPasswordAreNotEqual() {
        String username = RandomDataUtils.randomUsername();

        Selenide.open(LoginPage.URL, LoginPage.class)
                .register()
                .setUsername(username)
                .setPassword(RandomDataUtils.randomPassword())
                .setPasswordSubmit(RandomDataUtils.randomPassword())
                .signUp()
                .checkFormErrorText("Passwords should be equal");
    }
}
