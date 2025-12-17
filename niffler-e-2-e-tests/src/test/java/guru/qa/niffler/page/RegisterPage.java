package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.ownText;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class RegisterPage {

    private final SelenideElement usernameInput = $("#username");
    private final SelenideElement passwordInput = $("#password");
    private final SelenideElement passwordSubmitInput = $("#passwordSubmit");
    private final SelenideElement registerButton = $("#register-button");
    private final SelenideElement signInButton = $(".form_sign-in");
    private final SelenideElement formError = $(".form__error");

    @Nonnull
    @Step("Set username '{username}'")
    public RegisterPage setUsername(String username) {
        usernameInput.setValue(username);
        return this;
    }

    @Nonnull
    @Step("Set password '{password}'")
    public RegisterPage setPassword(String password) {
        passwordInput.setValue(password);
        return this;
    }

    @Nonnull
    @Step("Set password confirmation '{password}'")
    public RegisterPage setPasswordSubmit(String password) {
        passwordSubmitInput.setValue(password);
        return this;
    }

    @Nonnull
    @Step("Click register button")
    public RegisterPage signUp() {
        registerButton.click();
        return this;
    }

    @Nonnull
    @Step("Click sign-in button")
    public LoginPage singIn() {
        signInButton.click();
        return new LoginPage();
    }

    @Nonnull
    @Step("Check form error text '{error}'")
    public RegisterPage checkFormErrorText(String error) {
        formError.shouldHave(ownText(error));
        return this;
    }
}
