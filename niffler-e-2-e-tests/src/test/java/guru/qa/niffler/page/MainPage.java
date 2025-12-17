package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.Header;
import guru.qa.niffler.page.component.SpendingTable;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class MainPage extends BasePage<MainPage> {

    private final SelenideElement statistics = $("#stat");

    private final SpendingTable spendingTable = new SpendingTable();

    private final Header header = new Header();

    @Nonnull
    @Step("Check that page loaded")
    public MainPage checkThatPageLoaded() {
        statistics.shouldBe(visible);
        return this;
    }

    @Nonnull
    @Step("Open profile")
    public ProfilePage openProfile() {
        return header.toProfilePage();
    }

    @Nonnull
    @Step("Open friends")
    public FriendsPage openFriends() {
        return header.toFriendsPage();
    }

    @Nonnull
    @Step("Add a new spending")
    public EditSpendingPage addSpending() {
        return header.addSpendingPage();
    }

    @Nonnull
    @Step("Edit spending")
    public EditSpendingPage editSpending(String description) {
        return spendingTable.editSpending(description);
    }

    @Nonnull
    @Step("Check that table contains '{description}'")
    public MainPage checkThatTableContains(String description) {
        spendingTable.checkTableContains(description);
        return this;
    }
}
