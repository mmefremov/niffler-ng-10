package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$x;
import static com.codeborne.selenide.Selenide.$x;

@ParametersAreNonnullByDefault
public class ProfilePage extends BasePage<ProfilePage> {

    public static String url = Config.getInstance().frontUrl() + "profile";

    private final SelenideElement nameInput = $("#name");

    private final SelenideElement saveChangesButton = $("button[type='submit']");

    private final SelenideElement showArchivedCheckbox = $(".PrivateSwitchBase-input");

    private final ElementsCollection activeCategories = $$x("//div[./div[contains(@class,'MuiChip-colorPrimary')]]");

    private final ElementsCollection archivedCategories = $$x("//div[./div[contains(@class,'MuiChip-colorDefault')]]");

    private final By archiveCategoryButtonSelector = By.cssSelector("[aria-label='Archive category']");

    private final By unarchiveCategoryButtonSelector = By.cssSelector("[aria-label='Unarchive category']");

    private final SelenideElement archiveConfirmationButton = $x("//button[text()='Archive']");

    private final SelenideElement unarchiveConfirmationButton = $x("//button[text()='Unarchive']");

    @Nonnull
    @Step("Set name {name}")
    public ProfilePage setNewName(String name) {
        nameInput.setValue(name);
        return this;
    }

    @Nonnull
    @Step("Save changes")
    public ProfilePage saveChanges() {
        saveChangesButton.click();
        return this;
    }

    @Nonnull
    @Step("Show archived categories")
    public ProfilePage showArchivedCategories() {
        if (!showArchivedCheckbox.isSelected()) {
            showArchivedCheckbox.click();
        }
        return this;
    }

    @Nonnull
    @Step("Check active category is displayed")
    public ProfilePage checkActiveCategoryIsDisplayed(String category) {
        activeCategories.find(text(category)).shouldBe(visible);
        return this;
    }

    @Nonnull
    @Step("Archive category")
    public ProfilePage archiveCategory(String category) {
        activeCategories.find(text(category))
                .find(archiveCategoryButtonSelector).click();
        archiveConfirmationButton.click();
        return this;
    }

    @Nonnull
    @Step("Check archived category is displayed")
    public ProfilePage checkArchivedCategoryIsDisplayed(String category) {
        archivedCategories.find(text(category)).shouldBe(visible);
        return this;
    }

    @Nonnull
    @Step("Unarchive category")
    public ProfilePage unarchiveCategory(String category) {
        archivedCategories.find(text(category))
                .find(unarchiveCategoryButtonSelector).click();
        unarchiveConfirmationButton.click();
        return this;
    }
}
