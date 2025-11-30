package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.CollectionCondition.empty;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.$x;

@ParametersAreNonnullByDefault
public class FriendsPage {

    private final SelenideElement allPeopleTab = $x("//a[@href='/people/all']");
    private final ElementsCollection requestsTable = $$("#requests tr");
    private final ElementsCollection friendsTable = $$("#friends tr");
    private final By acceptButton = By.xpath(".//button[text()='Accept']");
    private final By declineButton = By.xpath(".//button[text()='Decline']");
    private final By unfriendButton = By.xpath(".//button[text()='Unfriend']");

    @Nonnull
    public FriendsPage requestsTableShouldContainIncomeFriend(String friendName) {
        SelenideElement friendRow = requestsTable.find(text(friendName)).shouldBe(visible);
        friendRow.find(acceptButton).shouldBe(visible);
        friendRow.find(declineButton).shouldBe(visible);
        return this;
    }

    @Nonnull
    public FriendsPage friendsTableShouldContainFriend(String friendName) {
        friendsTable.find(text(friendName)).shouldBe(visible)
                .find(unfriendButton).shouldBe(visible);
        return this;
    }

    @Nonnull
    public FriendsPage friendsTableShouldBeEmpty() {
        friendsTable.shouldBe(empty);
        return this;
    }

    @Nonnull
    public PeoplePage openPeopleTab() {
        allPeopleTab.click();
        return new PeoplePage();
    }
}
