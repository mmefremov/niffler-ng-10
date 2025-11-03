package guru.qa.niffler.test.fake;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.service.UsersDbClient;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Date;

@Disabled
class JdbcTest {

    @Test
    void txTest() {
        new SpendDbClient().createSpend(
                new SpendJson(
                        null,
                        new Date(),
                        new CategoryJson(
                                null,
                                "cat-name-tx-2",
                                "duck",
                                false
                        ),
                        CurrencyValues.RUB,
                        1000.0,
                        "spend-name-tx",
                        null
                ));
    }

    @Test
    void xaTxTest() {
        new UsersDbClient().createUser(
                new UserJson(
                        null,
                        "valentin",
                        null,
                        null,
                        null,
                        CurrencyValues.RUB,
                        null,
                        null,
                        null
                )
        );
    }
}
