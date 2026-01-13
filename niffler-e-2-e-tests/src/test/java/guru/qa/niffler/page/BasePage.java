package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.page.component.Header;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public abstract class BasePage<T extends BasePage<?>> {

    protected static final Config CFG = Config.getInstance();

    protected final Header header = new Header();

    protected final SelenideElement alert = $(".MuiAlert-message");

    @SuppressWarnings("unchecked")
    @Nonnull
    @Step("Check that alert message appears: {text}")
    public T checkAlert(String text) {
        alert.shouldHave(text(text));
        return (T) this;
    }
}
