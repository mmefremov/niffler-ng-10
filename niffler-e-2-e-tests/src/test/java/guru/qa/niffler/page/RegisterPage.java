package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.ownText;
import static com.codeborne.selenide.Selenide.$;

public class RegisterPage {

    private final SelenideElement usernameInput = $("#username");
    private final SelenideElement passwordInput = $("#password");
    private final SelenideElement passwordSubmitInput = $("#passwordSubmit");
    private final SelenideElement registerButton = $("#register-button");
    private final SelenideElement signInButton = $(".form_sign-in");
    private final SelenideElement formError = $(".form__error");

    public RegisterPage setUsername(String username) {
        usernameInput.setValue(username);
        return this;
    }

    public RegisterPage setPassword(String password) {
        passwordInput.setValue(password);
        return this;
    }

    public RegisterPage setPasswordSubmit(String password) {
        passwordSubmitInput.setValue(password);
        return this;
    }

    public RegisterPage signUp() {
        registerButton.click();
        return this;
    }

    public LoginPage singIn() {
        signInButton.click();
        return new LoginPage();
    }

    public RegisterPage checkFormErrorText(String error) {
        formError.shouldHave(ownText(error));
        return this;
    }
}
