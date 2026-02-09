package guru.qa.niffler.test.web;

import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.NonStaticBrowserExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.utils.RandomDataUtils;
import guru.qa.niffler.utils.SelenideUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

class LoginTest {

    @RegisterExtension
    private static final NonStaticBrowserExtension nonStaticBrowserExtension = new NonStaticBrowserExtension();

    private static final SelenideDriver driver = new SelenideDriver(SelenideUtils.getChromeConfig());

    @User
    @Test
    @DisplayName("Успешный вход пользователя")
    void mainPageShouldBeDisplayedAfterSuccessLogin(UserJson user) {
        nonStaticBrowserExtension.addDriver(driver);
        driver.open(LoginPage.URL);

        new LoginPage(driver)
                .login(user.username(), user.testData().password())
                .checkThatPageLoaded();
    }

    @User
    @Test
    @DisplayName("Пользователь остается на странице входа при вводе неверных данных")
    void userShouldStayOnLoginPageAfterLoginWithBadCredentials(UserJson user) {
        String wrongPassword = RandomDataUtils.randomPassword();

        nonStaticBrowserExtension.addDriver(driver);
        driver.open(LoginPage.URL);

        new LoginPage(driver)
                .tryToLogin(user.username(), wrongPassword)
                .checkFormErrorText("Bad credentials");
    }
}
