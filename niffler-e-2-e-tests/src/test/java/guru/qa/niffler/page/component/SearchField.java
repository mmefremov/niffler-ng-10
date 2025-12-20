package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.$;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@ParametersAreNonnullByDefault
public class SearchField extends BaseComponent<SearchField> {

    private final SelenideElement input = self.find("input");

    private final SelenideElement inputClearButton = self.find("button");

    public SearchField() {
        super($("form[class*='MuiBox-root']"));
    }

    @Step("Search with query '{query}'")
    public SearchField search(String query) {
        clearIfNotEmpty();
        input.setValue(query).pressEnter();
        return this;
    }

    @Step("Clear search field if not empty")
    public SearchField clearIfNotEmpty() {
        if (isNotEmpty(input.getValue())) {
            inputClearButton.click();
        }
        return this;
    }
}
