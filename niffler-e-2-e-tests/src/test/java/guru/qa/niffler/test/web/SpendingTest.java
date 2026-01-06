package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.IOException;

@WebTest
public class SpendingTest {

    @User(
            spendings = {
                    @Spending(
                            category = "Учеба",
                            amount = 89900,
                            currency = CurrencyValues.RUB,
                            description = "Обучение Niffler 2.0 юбилейный поток!"
                    )
            }
    )
    @Test
    void spendingDescriptionShouldBeEditedByTableAction(UserJson user) {
        final String spendDescription = user.testData().spendings().getFirst().description();
        final String newDescription = "Обучение Niffler Next Generation";

        Selenide.open(LoginPage.URL, LoginPage.class)
                .login(user.username(), user.testData().password())
                .editSpending(spendDescription)
                .setNewSpendingDescription(newDescription)
                .saveSpending()
                .checkAlert("Spending is edited successfully")
                .checkThatTableContains(newDescription);
    }

    @User
    @Test
    void addNewSpending(UserJson user) {
        String newDescription = RandomDataUtils.randomSentence(1);
        Selenide.open(LoginPage.URL, LoginPage.class)
                .login(user.username(), user.testData().password())
                .addSpending()
                .setNewSpendingAmount(RandomDataUtils.randomInteger())
                .setNewSpendingCategory(RandomDataUtils.randomCategoryName())
                .setNewSpendingDescription(newDescription)
                .saveSpending()
                .checkAlert("New spending is successfully created")
                .checkThatTableContains(newDescription);
    }

    @User(
            spendings = @Spending(
                    category = "Обучение",
                    description = "Обучение Advanced 2.0",
                    amount = 79990
            )
    )
    @ScreenShotTest("img/expected-stat.png")
    void checkStatComponentTest(UserJson user, BufferedImage expected) throws IOException {
        Selenide.open(LoginPage.URL, LoginPage.class)
                .login(user.username(), user.testData().password())
                .checkThatPageLoaded()
                .checkChartImage(expected);
    }

    @User(
            spendings = @Spending(
                    category = "Обучение",
                    description = "Обучение Advanced 2.0",
                    amount = 99990
            )
    )
    @ScreenShotTest("img/expected-stat.png")
    void checkStatComponentAfterEditingSpending(UserJson user, BufferedImage expected) throws IOException {
        Selenide.open(LoginPage.URL, LoginPage.class)
                .login(user.username(), user.testData().password())
                .checkThatPageLoaded()
                .editSpending("Обучение Advanced 2.0")
                .setNewSpendingAmount(79990)
                .saveSpending()
                .checkChartImage(expected)
                .checkLegendList();
    }

    @Disabled("IllegalStateException: This method needs a transaction for the calling thread and none exists")
    @User(
            spendings = {
                    @Spending(
                            category = "Обучение",
                            description = "Обучение Advanced 1.0",
                            amount = 79990
                    ),
                    @Spending(
                            category = "Обучение",
                            description = "Обучение Advanced 2.0",
                            amount = 79990
                    )
            }
    )
    @ScreenShotTest("img/expected-stat.png")
    void checkStatComponentAfterDeletingSpending(UserJson user, BufferedImage expected) throws IOException {
        Selenide.open(LoginPage.URL, LoginPage.class)
                .login(user.username(), user.testData().password())
                .checkThatPageLoaded()
                .deleteSpending("Обучение Advanced 1.0")
                .checkChartImage(expected)
                .checkLegendList();
    }

    @Disabled("IllegalStateException: This method needs a transaction for the calling thread and none exists")
    @User(
            categories = @Category(
                    name = "Обучение архив",
                    archived = true
            ),
            spendings = @Spending(
                    category = "Обучение архив",
                    description = "Обучение Advanced 2.0",
                    amount = 79990
            )
    )
    @ScreenShotTest("img/expected-stat-archived.png")
    void checkStatComponentForArchivedSpendingTest(UserJson user, BufferedImage expected) throws IOException {
        Selenide.open(LoginPage.URL, LoginPage.class)
                .login(user.username(), user.testData().password())
                .checkThatPageLoaded()
                .checkChartImage(expected)
                .checkLegendList();
    }
}
