package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.SearchField;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.CollectionCondition.empty;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.$x;

@ParametersAreNonnullByDefault
public class FriendsPage extends BasePage<FriendsPage> {

    public static final String URL = CFG.frontUrl() + "people/friends";

    private final SelenideElement allPeopleTab = $x("//a[@href='/people/all']");

    private final ElementsCollection requestsTable = $$("#requests tr");

    private final ElementsCollection friendsTable = $$("#friends tr");

    private final By acceptButton = By.xpath(".//button[text()='Accept']");

    private final By declineButton = By.xpath(".//button[text()='Decline']");

    private final By unfriendButton = By.xpath(".//button[text()='Unfriend']");

    private final SelenideElement confirmDeclineButton = $(".MuiDialogActions-root button:nth-child(2)");

    private final SearchField searchField = new SearchField();

    @Nonnull
    @Step("Check that requests table contains income friend {friendName}")
    public FriendsPage requestsTableShouldContainIncomeFriend(String friendName) {
        searchField.search(friendName);
        SelenideElement friendRow = requestsTable.find(text(friendName)).shouldBe(visible);
        friendRow.find(acceptButton).shouldBe(visible);
        friendRow.find(declineButton).shouldBe(visible);
        return this;
    }

    @Nonnull
    @Step("Check that friends table contains friend '{friendName}'")
    public FriendsPage friendsTableShouldContainFriend(String friendName) {
        searchField.search(friendName);
        friendsTable.find(text(friendName)).shouldBe(visible)
                .find(unfriendButton).shouldBe(visible);
        return this;
    }

    @Nonnull
    @Step("Check that requests table is empty")
    public FriendsPage friendsTableShouldBeEmpty() {
        friendsTable.shouldBe(empty);
        return this;
    }

    @Nonnull
    @Step("Accept invitation from '{friendName}'")
    public FriendsPage acceptInvitationFrom(String friendName) {
        requestsTable.find(text(friendName))
                .find(acceptButton).click();
        return this;
    }

    @Nonnull
    @Step("Decline invitation from '{friendName}'")
    public FriendsPage declineInvitationFrom(String friendName) {
        requestsTable.find(text(friendName))
                .find(declineButton).click();
        confirmDeclineButton.click();
        searchField.clearIfNotEmpty();
        return this;
    }

    @Nonnull
    @Step("Open people tab")
    public PeoplePage openPeopleTab() {
        allPeopleTab.click();
        return new PeoplePage();
    }
}
