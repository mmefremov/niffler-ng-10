package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@WebTest
class ProfileTest {

    private static final Config CFG = Config.getInstance();
    private static final String REGISTERED_USERNAME = "duck";
    private static final String REGISTERED_PASSWORD = "12345";

    @User(
            username = REGISTERED_USERNAME,
            categories = @Category(archived = true)
    )
    @Test
    @DisplayName("Профиль содержит архивную категорию")
    void archivedCategoryShouldPresentInCategoriesList(CategoryJson category) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(REGISTERED_USERNAME, REGISTERED_PASSWORD)
                .openProfileMenu()
                .openProfile()
                .showArchivedCategories()
                .checkArchivedCategoryIsDisplayed(category.name())
                .unarchiveCategory(category.name())
                .checkActiveCategoryIsDisplayed(category.name());
    }

    @User(
            username = REGISTERED_USERNAME,
            categories = @Category
    )
    @Test
    @DisplayName("Профиль содержит активную категорию")
    void activeCategoryShouldPresentInCategoriesList(CategoryJson category) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(REGISTERED_USERNAME, REGISTERED_PASSWORD)
                .openProfileMenu()
                .openProfile()
                .checkActiveCategoryIsDisplayed(category.name())
                .archiveCategory(category.name())
                .showArchivedCategories()
                .checkArchivedCategoryIsDisplayed(category.name());
    }
}
