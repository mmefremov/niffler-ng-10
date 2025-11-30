package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

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
    public MainPage login(String username, String password) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        submitButton.click();
        return new MainPage();
    }

    @Nonnull
    public LoginPage tryToLogin(String username, String password) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        submitButton.click();
        return new LoginPage();
    }

    @Nonnull
    public RegisterPage register() {
        registerButton.click();
        return new RegisterPage();
    }

    @Nonnull
    public LoginPage checkFormErrorText(String text) {
        formError.shouldHave(ownText(text));
        return this;
    }
}
