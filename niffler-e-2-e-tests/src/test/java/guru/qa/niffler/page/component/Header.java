package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.EditSpendingPage;
import guru.qa.niffler.page.FriendsPage;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.PeoplePage;
import guru.qa.niffler.page.ProfilePage;
import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@ParametersAreNonnullByDefault
public class Header {

    private final SelenideElement self = $("#root header");

    private final SelenideElement newSpendingButton = self.find("[href='/spending']");

    private final SelenideElement profileMenuButton = self.find("button[aria-label='Menu']");

    private final ElementsCollection menuList = $$("ul[role='menu'] li");

    @Step("Check header text")
    public void checkHeaderText() {
        self.$("h1").shouldHave(text("Niffler"));
    }

    @Step("Navigate to friends page")
    public FriendsPage toFriendsPage() {
        profileMenuButton.click();
        menuList.find(text("Friends")).click();
        return new FriendsPage();
    }

    @Step("Navigate to all people page")
    public PeoplePage toAllPeoplesPage() {
        profileMenuButton.click();
        menuList.find(text("All People")).click();
        return new PeoplePage();
    }

    @Step("Navigate to profile page")
    public ProfilePage toProfilePage() {
        profileMenuButton.click();
        menuList.find(text("Profile")).click();
        return new ProfilePage();
    }

    @Step("Sign out")
    public LoginPage signOut() {
        profileMenuButton.click();
        menuList.find(text("Sign out")).click();
        return new LoginPage();
    }

    @Step("Open add spending page")
    public EditSpendingPage addSpendingPage() {
        newSpendingButton.click();
        return new EditSpendingPage();
    }

    @Step("Navigate to main page")
    public MainPage toMainPage() {
        self.$("h1").click();
        return new MainPage();
    }
}
