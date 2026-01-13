package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.IOException;

@WebTest
class ProfileTest {

    @User(
            categories = @Category(archived = true)
    )
    @Test
    @DisplayName("Профиль содержит архивную категорию")
    void archivedCategoryShouldPresentInCategoriesList(UserJson user) {
        Selenide.open(LoginPage.URL, LoginPage.class)
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
        Selenide.open(LoginPage.URL, LoginPage.class)
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
    void nameShouldBeEditedInProfile(UserJson user) {
        String newName = RandomDataUtils.randomName();
        Selenide.open(LoginPage.URL, LoginPage.class)
                .login(user.username(), user.testData().password())
                .openProfile()
                .setNewName(newName)
                .saveChanges()
                .checkAlert("Profile successfully updated");
    }

    @User
    @ScreenShotTest("img/expected-avatar.png")
    @DisplayName("Смена аватара профиля")
    void avatarShouldBeUpdatedInProfile(UserJson user, BufferedImage expected) throws IOException {
        Selenide.open(LoginPage.URL, LoginPage.class)
                .login(user.username(), user.testData().password())
                .openProfile()
                .setNewAvatar("img/new-avatar.png")
                .saveChanges()
                .checkAlert("Profile successfully updated")
                .checkThatAvatarIsUpdated(expected);
    }
}
