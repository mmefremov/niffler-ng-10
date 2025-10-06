package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.ownText;
import static com.codeborne.selenide.Selenide.$;

public class LoginPage {
  private final SelenideElement usernameInput = $("#username");
  private final SelenideElement passwordInput = $("#password");
  private final SelenideElement submitButton = $("#login-button");
  private final SelenideElement registerButton = $("#register-button");
  private final SelenideElement formError = $(".form__error");

  public MainPage login(String username, String password) {
    usernameInput.val(username);
    passwordInput.val(password);
    submitButton.click();
    return new MainPage();
  }

  public LoginPage tryToLogin(String username, String password) {
    usernameInput.setValue(username);
    passwordInput.setValue(password);
    submitButton.click();
    return new LoginPage();
  }

  public RegisterPage register() {
    registerButton.click();
    return new RegisterPage();
  }

  public LoginPage checkFormErrorText(String text) {
    formError.shouldHave(ownText(text));
    return this;
  }
}
