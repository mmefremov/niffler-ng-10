package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;

@WebTest
public class SpendingTest {

    private static final Config CFG = Config.getInstance();

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

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.testData().password())
                .editSpending(spendDescription)
                .setNewSpendingDescription(newDescription)
                .save()
                .checkSnackbarText("Spending is edited successfully")
                .checkThatTableContains(newDescription);
    }

    @User
    @Test
    void addNewSpending(UserJson user) {
        String newDescription = RandomDataUtils.randomSentence(1);
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.testData().password())
                .addSpending()
                .setNewSpendingAmount(RandomDataUtils.randomInteger())
                .setNewSpendingCategory(RandomDataUtils.randomCategoryName())
                .setNewSpendingDescription(newDescription)
                .save()
                .checkSnackbarText("New spending is successfully created")
                .checkThatTableContains(newDescription);
    }
}
