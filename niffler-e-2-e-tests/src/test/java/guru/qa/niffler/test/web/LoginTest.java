package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@WebTest
class LoginTest {

    @User
    @Test
    @DisplayName("Успешный вход пользователя")
    void mainPageShouldBeDisplayedAfterSuccessLogin(UserJson user) {
        Selenide.open(LoginPage.URL, LoginPage.class)
                .login(user.username(), user.testData().password())
                .checkThatPageLoaded();
    }

    @User
    @Test
    @DisplayName("Пользователь остается на странице входа при вводе неверных данных")
    void userShouldStayOnLoginPageAfterLoginWithBadCredentials(UserJson user) {
        String wrongPassword = RandomDataUtils.randomPassword();

        Selenide.open(LoginPage.URL, LoginPage.class)
                .tryToLogin(user.username(), wrongPassword)
                .checkFormErrorText("Bad credentials");
    }
}
