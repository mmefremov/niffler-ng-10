package guru.qa.niffler.test.web;

import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.Browser;
import guru.qa.niffler.jupiter.extension.BrowserConverter;
import guru.qa.niffler.jupiter.extension.NonStaticBrowserExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.utils.RandomDataUtils;
import guru.qa.niffler.utils.SelenideUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.EnumSource;

class RegistrationTest {

    @RegisterExtension
    private static final NonStaticBrowserExtension nonStaticBrowserExtension = new NonStaticBrowserExtension();

    private static final SelenideDriver driver = new SelenideDriver(SelenideUtils.getChromeConfig());

    @ParameterizedTest
    @EnumSource(Browser.class)
    @DisplayName("Успешная регистрация нового пользователя")
    void shouldRegisterNewUser(@ConvertWith(BrowserConverter.class) SelenideDriver driver) {
        String username = RandomDataUtils.randomUsername();
        String password = RandomDataUtils.randomPassword();
        nonStaticBrowserExtension.addDriver(driver);
        driver.open(LoginPage.URL);

        new LoginPage(driver)
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
    @ParameterizedTest
    @EnumSource(Browser.class)
    @DisplayName("Ошибка при попытке регистрации существующего пользователя")
    void shouldNotRegisterUserWithExistingUsername(@ConvertWith(BrowserConverter.class) SelenideDriver driver, UserJson user) {
        driver.open(LoginPage.URL);
        nonStaticBrowserExtension.addDriver(driver);

        new LoginPage(driver)
                .register()
                .setUsername(user.username())
                .setPassword(user.testData().password())
                .setPasswordSubmit(user.testData().password())
                .signUp()
                .checkFormErrorText("Username `%s` already exists".formatted(user.username()));
    }

    @ParameterizedTest
    @EnumSource(Browser.class)
    @DisplayName("Ошибка при несовпадении паролей")
    void shouldShowErrorIfPasswordAndConfirmPasswordAreNotEqual(@ConvertWith(BrowserConverter.class) SelenideDriver driver) {
        String username = RandomDataUtils.randomUsername();
        nonStaticBrowserExtension.addDriver(driver);
        driver.open(LoginPage.URL);

        new LoginPage(driver)
                .register()
                .setUsername(username)
                .setPassword(RandomDataUtils.randomPassword())
                .setPasswordSubmit(RandomDataUtils.randomPassword())
                .signUp()
                .checkFormErrorText("Passwords should be equal");
    }
}
