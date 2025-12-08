package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.openqa.selenium.Keys;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

@ParametersAreNonnullByDefault
public class MainPage {

    private final SelenideElement profileMenuButton = $("[data-testid='PersonIcon']");
    private final SelenideElement profileLink = $x("//*[@href='/profile']/parent::li");
    private final SelenideElement friendsLink = $x("//*[@href='/people/friends']/parent::li");
    private final SelenideElement statistics = $("#stat");
    private final SelenideElement searchInput = $("input[aria-label='search']");
    private final SelenideElement spendingTable = $("#spendings");

    @Nonnull
    @Step("Check that page loaded")
    public MainPage checkThatPageLoaded() {
        statistics.shouldBe(visible);
        spendingTable.should(visible);
        return this;
    }

    @Nonnull
    @Step("Open profile menu")
    public MainPage openProfileMenu() {
        profileMenuButton.click();
        return this;
    }

    @Nonnull
    @Step("Open profile")
    public ProfilePage openProfile() {
        profileLink.click();
        return new ProfilePage();
    }

    @Nonnull
    @Step("Open friends")
    public FriendsPage openFriends() {
        friendsLink.click();
        return new FriendsPage();
    }

    @Nonnull
    @Step("Edit spending")
    public EditSpendingPage editSpending(String description) {
        searchInput.setValue(description).sendKeys(Keys.ENTER);
        spendingTable.$$("tbody tr").find(text(description)).$$("td").get(5).click();
        return new EditSpendingPage();
    }

    @Nonnull
    @Step("Check that table contains '{description}'")
    public MainPage checkThatTableContains(String description) {
        spendingTable.$$("tbody tr").find(text(description)).should(visible);
        return this;
    }
}
