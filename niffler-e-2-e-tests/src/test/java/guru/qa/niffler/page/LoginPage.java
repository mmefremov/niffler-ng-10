package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.ownText;

@ParametersAreNonnullByDefault
public class LoginPage extends BasePage<LoginPage> {

    public static final String URL = CFG.authUrl() + "login";

    private final SelenideElement usernameInput;

    private final SelenideElement passwordInput;

    private final SelenideElement submitButton;

    private final SelenideElement registerButton;

    private final SelenideElement formError;

    public LoginPage(SelenideDriver driver) {
        super(driver);
        usernameInput = driver.$("#username");
        passwordInput = driver.$("#password");
        submitButton = driver.$("#login-button");
        registerButton = driver.$("#register-button");
        formError = driver.$(".form__error");
    }

    @Nonnull
    @Step("Login as '{username}' with password '{password}'")
    public MainPage login(String username, String password) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        submitButton.click();
        return new MainPage(driver);
    }

    @Nonnull
    @Step("Try to login as '{username}' with password '{password}'")
    public LoginPage tryToLogin(String username, String password) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        submitButton.click();
        return new LoginPage(driver);
    }

    @Nonnull
    @Step("Click register button")
    public RegisterPage register() {
        registerButton.click();
        return new RegisterPage(driver);
    }

    @Nonnull
    @Step("Check form error text is '{text}'")
    public LoginPage checkFormErrorText(String text) {
        formError.shouldHave(ownText(text));
        return this;
    }
}
