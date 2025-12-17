package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.SearchField;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.$x;

@ParametersAreNonnullByDefault
public class PeoplePage extends BasePage<PeoplePage> {

    private final SelenideElement allPeopleTab = $x("//a[@href='/people/all']");

    private final ElementsCollection allPeopleTable = $$("#all tr");

    private final By requestLabel = By.cssSelector(".MuiChip-label");

    private final SearchField searchField = new SearchField();

    @Nonnull
    @Step("Check that all people table contains waiting answer from friend '{friendName}'")
    public PeoplePage allPeoplesTableShouldContainWaitingAnswerFromFriend(String friendName) {
        allPeopleTab.click();
        searchField.search(friendName);
        allPeopleTable.find(text(friendName)).shouldBe(visible)
                .find(requestLabel).shouldBe(visible)
                .shouldHave(text("Waiting..."));
        return this;
    }
}
