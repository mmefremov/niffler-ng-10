package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.service.AuthApiClient;
import guru.qa.niffler.util.FakerUtils;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(BrowserExtension.class)
class LoginTest {

  private static final Config CFG = Config.getInstance();
  private static String registeredUsername;
  private static String registeredPassword;

  @BeforeAll
  static void setup() throws IOException {
    registeredUsername = FakerUtils.getUserName();
    registeredPassword = FakerUtils.getPassword();
    var response = new AuthApiClient().register(registeredUsername, registeredPassword);
    assertThat(response.code()).isEqualTo(HttpStatus.CREATED_201);
  }

  @Test
  @DisplayName("Успешный вход пользователя")
  void mainPageShouldBeDisplayedAfterSuccessLogin() {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
            .login(registeredUsername, registeredPassword)
            .checkThatPageLoaded();
  }

  @Test
  @DisplayName("Пользователь остается на странице входа при вводе неверных данных")
  void userShouldStayOnLoginPageAfterLoginWithBadCredentials() {
    String wrongPassword = FakerUtils.getPassword();

    Selenide.open(CFG.frontUrl(), LoginPage.class)
            .tryToLogin(registeredUsername, wrongPassword)
            .checkFormErrorText("Bad credentials");
  }
}
