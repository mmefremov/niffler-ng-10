package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.ownText;

@ParametersAreNonnullByDefault
public class RegisterPage extends BasePage<RegisterPage> {

    public static final String URL = CFG.authUrl() + "register";

    private final SelenideElement usernameInput;

    private final SelenideElement passwordInput;

    private final SelenideElement passwordSubmitInput;

    private final SelenideElement registerButton;

    private final SelenideElement signInButton;

    private final SelenideElement formError;

    public RegisterPage(SelenideDriver driver) {
        super(driver);
        usernameInput = driver.$("#username");
        passwordInput = driver.$("#password");
        passwordSubmitInput = driver.$("#passwordSubmit");
        registerButton = driver.$("#register-button");
        signInButton = driver.$(".form_sign-in");
        formError = driver.$(".form__error");
    }

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
        return new LoginPage(driver);
    }

    @Nonnull
    @Step("Check form error text '{error}'")
    public RegisterPage checkFormErrorText(String error) {
        formError.shouldHave(ownText(error));
        return this;
    }
}
