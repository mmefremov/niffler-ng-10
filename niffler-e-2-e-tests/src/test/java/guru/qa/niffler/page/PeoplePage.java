package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.$x;

public class PeoplePage {

    private final SelenideElement allPeopleTab = $x("//a[@href='/people/all']");
    private final SelenideElement searchInput = $("input[aria-label='search']");
    private final ElementsCollection allPeopleTable = $$("#all tr");
    private final By requestLabel = By.cssSelector(".MuiChip-label");

    public PeoplePage allPeoplesTableShouldContainWaitingAnswerFromFriend(String friendName) {
        allPeopleTab.click();
        searchInput.setValue(friendName).sendKeys(Keys.ENTER);
        allPeopleTable.find(text(friendName)).shouldBe(visible)
                .find(requestLabel).shouldBe(visible)
                .shouldHave(text("Waiting..."));
        return this;
    }
}
