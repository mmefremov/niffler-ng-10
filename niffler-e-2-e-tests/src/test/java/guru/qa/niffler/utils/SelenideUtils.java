package guru.qa.niffler.utils;

import com.codeborne.selenide.Browsers;
import com.codeborne.selenide.SelenideConfig;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ParametersAreNonnullByDefault
public class SelenideUtils {

    @Nonnull
    public static SelenideConfig getChromeConfig() {
        return new SelenideConfig().browser(Browsers.CHROME);
    }
}
