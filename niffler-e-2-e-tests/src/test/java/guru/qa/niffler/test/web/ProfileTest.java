package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@WebTest
class ProfileTest {

    private static final Config CFG = Config.getInstance();

    @User(
            categories = @Category(archived = true)
    )
    @Test
    @DisplayName("Профиль содержит архивную категорию")
    void archivedCategoryShouldPresentInCategoriesList(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.testData().password())
                .openProfile()
                .showArchivedCategories()
                .checkArchivedCategoryIsDisplayed(user.testData().categories().getFirst().name())
                .unarchiveCategory(user.testData().categories().getFirst().name())
                .checkActiveCategoryIsDisplayed(user.testData().categories().getFirst().name());
    }

    @User(
            categories = @Category(archived = false)
    )
    @Test
    @DisplayName("Профиль содержит активную категорию")
    void activeCategoryShouldPresentInCategoriesList(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.testData().password())
                .openProfile()
                .checkActiveCategoryIsDisplayed(user.testData().categories().getFirst().name())
                .archiveCategory(user.testData().categories().getFirst().name())
                .showArchivedCategories()
                .checkArchivedCategoryIsDisplayed(user.testData().categories().getFirst().name());
    }

    @User
    @Test
    @DisplayName("Редактирование имени профиля")
    void editProfileName(UserJson user) {
        String newName = RandomDataUtils.randomName();
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.testData().password())
                .openProfile()
                .setNewName(newName)
                .saveChanges()
                .returnToMainPage()
                .openProfile()
                .checkUserName(newName);
    }
}
