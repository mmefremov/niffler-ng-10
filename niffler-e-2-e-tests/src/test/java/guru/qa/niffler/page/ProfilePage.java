package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.utils.ScreenDiffResult;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ParametersAreNonnullByDefault
public class ProfilePage extends BasePage<ProfilePage> {

    public static final String URL = CFG.frontUrl() + "profile";

    private final SelenideElement avatarIcon;

    private final SelenideElement nameInput;

    private final SelenideElement uploadNewPictureInput;

    private final SelenideElement saveChangesButton;

    private final SelenideElement showArchivedCheckbox;

    private final ElementsCollection activeCategories;

    private final ElementsCollection archivedCategories;

    private final By archiveCategoryButtonSelector;

    private final By unarchiveCategoryButtonSelector;

    private final SelenideElement archiveConfirmationButton;

    private final SelenideElement unarchiveConfirmationButton;

    public ProfilePage(SelenideDriver driver) {
        super(driver);
        avatarIcon = driver.$(".MuiAvatar-circular");
        nameInput = driver.$("#name");
        uploadNewPictureInput = driver.$("#image__input");
        saveChangesButton = driver.$("button[type='submit']");
        showArchivedCheckbox = driver.$(".PrivateSwitchBase-input");
        activeCategories = driver.$$x("//div[./div[contains(@class,'MuiChip-colorPrimary')]]");
        archivedCategories = driver.$$x("//div[./div[contains(@class,'MuiChip-colorDefault')]]");
        archiveCategoryButtonSelector = By.cssSelector("[aria-label='Archive category']");
        unarchiveCategoryButtonSelector = By.cssSelector("[aria-label='Unarchive category']");
        archiveConfirmationButton = driver.$x("//button[text()='Archive']");
        unarchiveConfirmationButton = driver.$x("//button[text()='Unarchive']");
    }

    @Nonnull
    @Step("Set name {name}")
    public ProfilePage setNewName(String name) {
        nameInput.setValue(name);
        return this;
    }

    @Nonnull
    @Step("Set new avatar")
    public ProfilePage setNewAvatar(String path) {
        uploadNewPictureInput.uploadFromClasspath(path);
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

    @Nonnull
    @Step("Check that the avatar is updated")
    public ProfilePage checkThatAvatarIsUpdated(BufferedImage expected) throws IOException {
        BufferedImage actual = ImageIO.read(avatarIcon.screenshot());
        assertFalse(new ScreenDiffResult(actual, expected));
        return this;
    }
}
