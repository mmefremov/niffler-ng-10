package guru.qa.niffler.test.web;

import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.NonStaticBrowserExtension;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.utils.RandomDataUtils;
import guru.qa.niffler.utils.SelenideUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.awt.image.BufferedImage;

public class SpendingTest {

    @RegisterExtension
    private static final NonStaticBrowserExtension nonStaticBrowserExtension = new NonStaticBrowserExtension();

    private static final SelenideDriver driver = new SelenideDriver(SelenideUtils.getChromeConfig());

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

        nonStaticBrowserExtension.addDriver(driver);
        driver.open(LoginPage.URL);

        new LoginPage(driver)
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
        nonStaticBrowserExtension.addDriver(driver);
        driver.open(LoginPage.URL);

        new LoginPage(driver)
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
    void checkStatComponentTest(UserJson user, BufferedImage expected) {
        nonStaticBrowserExtension.addDriver(driver);
        driver.open(LoginPage.URL);

        new LoginPage(driver)
                .login(user.username(), user.testData().password())
                .checkThatPageLoaded()
                .checkSpends(user.testData().spendings())
                .checkChartImage(expected)
                .checkLegendList(user.testData().spendings());
    }

    @User(
            spendings = @Spending(
                    category = "Обучение",
                    description = "Обучение Advanced 2.0",
                    amount = 99990
            )
    )
    @ScreenShotTest("img/expected-stat.png")
    void checkStatComponentAfterEditingSpending(UserJson user, BufferedImage expected) {
        nonStaticBrowserExtension.addDriver(driver);
        driver.open(LoginPage.URL);

        new LoginPage(driver)
                .login(user.username(), user.testData().password())
                .checkThatPageLoaded()
                .checkSpends(user.testData().spendings())
                .editSpending("Обучение Advanced 2.0")
                .setNewSpendingAmount(79990)
                .saveSpending()
                .checkChartImage(expected)
                .checkLegendList();
    }

    @User(
            spendings = {
                    @Spending(
                            category = "Обучение архив",
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
    void checkStatComponentAfterDeletingSpending(UserJson user, BufferedImage expected) {
        nonStaticBrowserExtension.addDriver(driver);
        driver.open(LoginPage.URL);

        new LoginPage(driver)
                .login(user.username(), user.testData().password())
                .checkThatPageLoaded()
                .checkSpends(user.testData().spendings())
                .deleteSpending("Обучение Advanced 1.0")
                .checkChartImage(expected)
                .checkLegendList();
    }

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
    void checkStatComponentForArchivedSpendingTest(UserJson user, BufferedImage expected) {
        nonStaticBrowserExtension.addDriver(driver);
        driver.open(LoginPage.URL);

        new LoginPage(driver)
                .login(user.username(), user.testData().password())
                .checkThatPageLoaded()
                .checkSpends(user.testData().spendings())
                .checkChartImage(expected)
                .checkLegendList(user.testData().spendings());
    }

    @User(
            categories = @Category(
                    name = "Обучение архив",
                    archived = true
            ),
            spendings = {
                    @Spending(
                            category = "Обучение архив",
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
    @ScreenShotTest("img/expected-stat-several.png")
    void checkStatComponentForSeveralSpendingsTest(UserJson user, BufferedImage expected) {
        nonStaticBrowserExtension.addDriver(driver);
        driver.open(LoginPage.URL);

        new LoginPage(driver)
                .login(user.username(), user.testData().password())
                .checkThatPageLoaded()
                .checkSpends(user.testData().spendings())
                .checkChartImage(expected)
                .checkLegendList(user.testData().spendings());
    }
}
