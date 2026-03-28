package guru.qa.niffler.test.grpc;

import com.google.protobuf.Empty;
import guru.qa.niffler.grpc.CalculateRequest;
import guru.qa.niffler.grpc.Currency;
import guru.qa.niffler.grpc.CurrencyValues;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.within;

class CurrencyGrpcTest extends BaseGrpcTest {

    @Test
    @DisplayName("All currencies list")
    void allCurrenciesListTest() {
        var allCurrenciesList = CURRENCY_BLOCKING_STUB.getAllCurrencies(Empty.getDefaultInstance()).getAllCurrenciesList();
        assertThat(allCurrenciesList).hasSize(4)
                .extracting(Currency::getCurrency)
                .containsExactly(CurrencyValues.RUB, CurrencyValues.KZT, CurrencyValues.EUR, CurrencyValues.USD);
    }

    @Test
    @DisplayName("All currencies unknown fields")
    void allCurrenciesResponseUnknownFieldsTest() {
        var response = CURRENCY_BLOCKING_STUB.getAllCurrencies(Empty.getDefaultInstance());
        assertThat(response.getUnknownFields().asMap()).isEmpty();
    }

    @DisplayName("Calculate rate response")
    @ParameterizedTest(name = "[{0}] {1} should be equal to [{2}] {3}")
    @CsvSource({
            "100 , USD ,      100 , USD",
            "100 , RUB ,      100 , RUB",
            "100 , EUR ,      100 , EUR",
            "100 , KZT ,      100 , KZT",
            "100 , USD ,  6666.67 , RUB",
            "100 , USD , 47619.05 , KZT",
            "100 , USD ,    92.59 , EUR",
            "100 , RUB ,      1.5 , USD",
            "100 , RUB ,   714.29 , KZT",
            "100 , RUB ,     1.39 , EUR",
            "100 , EUR ,     7200 , RUB",
            "100 , EUR , 51428.57 , KZT",
            "100 , EUR ,      108 , USD",
            "100 , KZT ,       14 , RUB",
            "100 , KZT ,     0.21 , USD",
            "100 , KZT ,     0.19 , EUR"
    })
    void calculateRateResponseTest(double amount, CurrencyValues spendCurrency,
                                   double expectedCalculatedAmount, CurrencyValues desiredCurrency) {
        var request = CalculateRequest.newBuilder()
                .setAmount(amount)
                .setSpendCurrency(spendCurrency)
                .setDesiredCurrency(desiredCurrency)
                .build();
        var calculateResponse = CURRENCY_BLOCKING_STUB.calculateRate(request);
        assertThat(calculateResponse.getCalculatedAmount()).isCloseTo(expectedCalculatedAmount, within(0.01));
    }
}
