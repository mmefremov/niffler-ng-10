package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$x;
import static com.codeborne.selenide.Selenide.$x;

public class ProfilePage {

    private final SelenideElement showArchivedCheckbox = $(".PrivateSwitchBase-input");
    private final ElementsCollection activeCategories = $$x("//div[./div[contains(@class,'MuiChip-colorPrimary')]]");
    private final ElementsCollection archivedCategories = $$x("//div[./div[contains(@class,'MuiChip-colorDefault')]]");
    private final By archiveCategoryButtonSelector = By.cssSelector("[aria-label='Archive category']");
    private final By unarchiveCategoryButtonSelector = By.cssSelector("[aria-label='Unarchive category']");
    private final SelenideElement archiveConfirmationButton = $x("//button[text()='Archive']");
    private final SelenideElement unarchiveConfirmationButton = $x("//button[text()='Unarchive']");

    public ProfilePage showArchivedCategories() {
        if (!showArchivedCheckbox.isSelected()) {
            showArchivedCheckbox.click();
        }
        return this;
    }

    public ProfilePage checkActiveCategoryIsDisplayed(String category) {
        activeCategories.find(text(category)).shouldBe(visible);
        return this;
    }

    public ProfilePage archiveCategory(String category) {
        activeCategories.find(text(category))
                .find(archiveCategoryButtonSelector).click();
        archiveConfirmationButton.click();
        return this;
    }

    public ProfilePage checkArchivedCategoryIsDisplayed(String category) {
        archivedCategories.find(text(category)).shouldBe(visible);
        return this;
    }

    public ProfilePage unarchiveCategory(String category) {
        archivedCategories.find(text(category))
                .find(unarchiveCategoryButtonSelector).click();
        unarchiveConfirmationButton.click();
        return this;
    }
}
