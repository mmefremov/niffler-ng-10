package guru.qa.niffler.test.gql;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.StatQuery;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CurrencyValues;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;

import static guru.qa.type.CurrencyValues.EUR;
import static guru.qa.type.CurrencyValues.RUB;
import static org.assertj.core.api.Assertions.not;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

public class StatGraphQlTest extends BaseGraphQlTest {

    @User
    @Test
    @ApiLogin
    void statTest(@Token String bearerToken) {
        final ApolloCall<StatQuery.Data> currenciesCall = apolloClient.query(StatQuery.builder()
                                                                                     .filterCurrency(null)
                                                                                     .statCurrency(null)
                                                                                     .filterPeriod(null)
                                                                                     .build())
                .addHttpHeader("authorization", bearerToken);

        final ApolloResponse<StatQuery.Data> response = Rx2Apollo.single(currenciesCall).blockingGet();
        final StatQuery.Data data = response.dataOrThrow();
        StatQuery.Stat result = data.stat;
        Assertions.assertEquals(
                0.0,
                result.total
        );
    }

    @User(
            categories = {
                    @Category(name = "Category 1"),
                    @Category(name = "Category 2"),
                    @Category(name = "Archived category 1", archived = true),
                    @Category(name = "Archived category 2", archived = true),
            },
            spendings = {
                    @Spending(category = "Category 1", description = "desc", amount = 10_000, currency = CurrencyValues.RUB),
                    @Spending(category = "Category 2", description = "desc", amount = 20_000, currency = CurrencyValues.EUR),
                    @Spending(category = "Archived category 1", description = "desc", amount = 30_000, currency = CurrencyValues.USD),
                    @Spending(category = "Archived category 2", description = "desc", amount = 40_000, currency = CurrencyValues.KZT)
            }
    )
    @Test
    @ApiLogin
    void checkStatQueryWithArchivedCategories(@Token String bearerToken) {
        var call = apolloClient.query(StatQuery.builder().build())
                .addHttpHeader("authorization", bearerToken);

        var response = Rx2Apollo.single(call).blockingGet();
        var data = response.dataOrThrow();
        StatQuery.Stat stat = data.stat;
        assertSoftly(softly -> {
            softly.assertThat(stat.currency).as("currency").isEqualTo(RUB);
            softly.assertThat(stat.total).as("total").isEqualTo(3455600.0);
            List<StatQuery.StatByCategory> statByCategories = stat.statByCategories;
            softly.assertThat(statByCategories).as("categories").hasSize(3)
                    .as("categories sorting")
                    .filteredOn("categoryName", not("Archived"))
                    .isSortedAccordingTo(Comparator.comparingDouble((StatQuery.StatByCategory s) -> s.sum).reversed());
            softly.assertThat(statByCategories.getLast().categoryName).as("last category").isEqualTo("Archived");
            softly.assertThat(statByCategories.getLast().sum).as("archived sum").isEqualTo(2005600.0);
        });
    }

    @User(categories = @Category(name = "Category"),
            spendings = {
                    @Spending(category = "Category", description = "desc", amount = 10_000, currency = CurrencyValues.RUB),
                    @Spending(category = "Category", description = "desc", amount = 20_000, currency = CurrencyValues.EUR),
                    @Spending(category = "Category", description = "desc", amount = 30_000, currency = CurrencyValues.USD),
                    @Spending(category = "Category", description = "desc", amount = 40_000, currency = CurrencyValues.KZT)
            })
    @Test
    @ApiLogin
    void checkStatQueryWithFilterCurrency(@Token String bearerToken) {
        var call = apolloClient.query(StatQuery.builder()
                                              .filterCurrency(EUR)
                                              .build())
                .addHttpHeader("authorization", bearerToken);

        var response = Rx2Apollo.single(call).blockingGet();
        var data = response.dataOrThrow();
        StatQuery.Stat stat = data.stat;
        assertSoftly(softly -> {
            softly.assertThat(stat.currency).as("currency").isEqualTo(RUB);
            softly.assertThat(stat.total).as("total").isEqualTo(1440000.0);
            softly.assertThat(stat.statByCategories).as("categories").hasSize(1);
        });
    }

    @User(
            categories = @Category(name = "Category"),
            spendings = {
                    @Spending(category = "Category", description = "desc", amount = 10_000, currency = CurrencyValues.RUB),
                    @Spending(category = "Category", description = "desc", amount = 20_000, currency = CurrencyValues.EUR),
                    @Spending(category = "Category", description = "desc", amount = 30_000, currency = CurrencyValues.USD),
                    @Spending(category = "Category", description = "desc", amount = 40_000, currency = CurrencyValues.KZT)
            }
    )
    @Test
    @ApiLogin
    void checkStatQueryWithStatCurrency(@Token String bearerToken) {
        var call = apolloClient.query(StatQuery.builder()
                                              .statCurrency(EUR)
                                              .build())
                .addHttpHeader("authorization", bearerToken);

        var response = Rx2Apollo.single(call).blockingGet();
        var data = response.dataOrThrow();
        StatQuery.Stat stat = data.stat;
        assertSoftly(softly -> {
            softly.assertThat(stat.currency).as("currency").isEqualTo(EUR);
            softly.assertThat(stat.total).as("total").isEqualTo(47994.45);
            softly.assertThat(stat.statByCategories).as("categories").hasSize(1);
        });
    }

}
