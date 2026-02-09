package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.not;

@ParametersAreNonnullByDefault
public class SearchField extends BaseComponent<SearchField> {

    private final SelenideElement input;

    private final SelenideElement inputClearButton;

    public SearchField(SelenideDriver driver) {
        super(driver, driver.$("form[class*='MuiBox-root']"));
        input = self.find("input");
        inputClearButton = self.find("button");
    }

    @Step("Search with query '{query}'")
    public SearchField search(String query) {
        clearIfNotEmpty();
        input.setValue(query).pressEnter();
        return this;
    }

    @Step("Clear search field if not empty")
    public SearchField clearIfNotEmpty() {
        if (input.is(not(empty))) {
            inputClearButton.click();
            input.shouldBe(empty);
        }
        return this;
    }
}
