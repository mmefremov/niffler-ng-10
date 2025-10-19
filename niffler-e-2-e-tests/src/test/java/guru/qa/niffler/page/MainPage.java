package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class MainPage {

    private final SelenideElement profileMenuButton = $("[data-testid='PersonIcon']");
    private final SelenideElement profileLink = $x("//*[@href='/profile']/parent::li");
    private final SelenideElement friendsLink = $x("//*[@href='/people/friends']/parent::li");
    private final SelenideElement statistics = $("#stat");
    private final SelenideElement spendingTable = $("#spendings");

    public MainPage checkThatPageLoaded() {
        statistics.shouldBe(visible);
        spendingTable.should(visible);
        return this;
    }

    public MainPage openProfileMenu() {
        profileMenuButton.click();
        return this;
    }

    public ProfilePage openProfile() {
        profileLink.click();
        return new ProfilePage();
    }

    public FriendsPage openFriends() {
        friendsLink.click();
        return new FriendsPage();
    }

    public EditSpendingPage editSpending(String description) {
        spendingTable.$$("tbody tr").find(text(description)).$$("td").get(5).click();
        return new EditSpendingPage();
    }

    public MainPage checkThatTableContains(String description) {
        spendingTable.$$("tbody tr").find(text(description)).should(visible);
        return this;
    }
}
