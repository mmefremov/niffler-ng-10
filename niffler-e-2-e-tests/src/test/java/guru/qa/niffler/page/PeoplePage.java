package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.SearchField;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;

@ParametersAreNonnullByDefault
public class PeoplePage extends BasePage<PeoplePage> {

    public static final String URL = CFG.frontUrl() + "people/all";

    private final SelenideElement allPeopleTab;

    private final ElementsCollection allPeopleTable;

    private final By requestLabel;

    private final SearchField searchField;

    public PeoplePage(SelenideDriver driver) {
        super(driver);
        allPeopleTab = driver.$x("//a[@href='/people/all']");
        allPeopleTable = driver.$$("#all tr");
        requestLabel = By.cssSelector(".MuiChip-label");
        searchField = new SearchField(driver);
    }

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
