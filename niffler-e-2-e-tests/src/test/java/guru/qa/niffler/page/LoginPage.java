package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.ownText;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class LoginPage {

    private final SelenideElement usernameInput = $("#username");
    private final SelenideElement passwordInput = $("#password");
    private final SelenideElement submitButton = $("#login-button");
    private final SelenideElement registerButton = $("#register-button");
    private final SelenideElement formError = $(".form__error");

    @Nonnull
    @Step("Login as '{username}' with password '{password}'")
    public MainPage login(String username, String password) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        submitButton.click();
        return new MainPage();
    }

    @Nonnull
    @Step("Try to login as '{username}' with password '{password}'")
    public LoginPage tryToLogin(String username, String password) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        submitButton.click();
        return new LoginPage();
    }

    @Nonnull
    @Step("Click register button")
    public RegisterPage register() {
        registerButton.click();
        return new RegisterPage();
    }

    @Nonnull
    @Step("Check form error text is '{text}'")
    public LoginPage checkFormErrorText(String text) {
        formError.shouldHave(ownText(text));
        return this;
    }
}
