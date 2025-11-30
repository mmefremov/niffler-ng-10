package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
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
    public MainPage checkThatPageLoaded() {
        statistics.shouldBe(visible);
        spendingTable.should(visible);
        return this;
    }

    @Nonnull
    public MainPage openProfileMenu() {
        profileMenuButton.click();
        return this;
    }

    @Nonnull
    public ProfilePage openProfile() {
        profileLink.click();
        return new ProfilePage();
    }

    @Nonnull
    public FriendsPage openFriends() {
        friendsLink.click();
        return new FriendsPage();
    }

    @Nonnull
    public EditSpendingPage editSpending(String description) {
        searchInput.setValue(description).sendKeys(Keys.ENTER);
        spendingTable.$$("tbody tr").find(text(description)).$$("td").get(5).click();
        return new EditSpendingPage();
    }

    @Nonnull
    public MainPage checkThatTableContains(String description) {
        spendingTable.$$("tbody tr").find(text(description)).should(visible);
        return this;
    }
}
