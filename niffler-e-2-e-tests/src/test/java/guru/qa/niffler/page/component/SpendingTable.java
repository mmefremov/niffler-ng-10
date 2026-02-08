package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.DataFilterValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.page.EditSpendingPage;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import javax.annotation.ParametersAreNonnullByDefault;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.codeborne.selenide.ClickOptions.usingJavaScript;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static guru.qa.niffler.condition.SpendConditions.containSpends;

@ParametersAreNonnullByDefault
public class SpendingTable extends BaseComponent<SpendingTable> {

    private final SearchField searchField = new SearchField();

    private final SelenideElement periodInput = self.$("#period");

    private final SelenideElement deleteButton = self.$("#delete");

    private final SelenideElement popup = $("div[role='dialog']");

    private final By editButton = By.cssSelector("button");

    private final By checkboxButton = By.cssSelector(".PrivateSwitchBase-input");

    private final ElementsCollection periodList = self.$$(".MuiMenu-list");

    private final ElementsCollection spendingRows = self.$("tbody").$$("tr");

    public SpendingTable() {
        super($("#spendings"));
    }

    @Step("Spends should be equal to expected ones")
    public void checkSpends(List<SpendJson> expectedSpends) {
        spendingRows.should(containSpends(expectedSpends));
    }

    public Map<String, BigDecimal> getSpendingCategoriesWithAmounts() {
        searchField.clearIfNotEmpty();
        return spendingRows.stream()
                .collect(Collectors.groupingBy(
                        row -> row.find("td", 1).text(),
                        Collectors.mapping(
                                row -> new BigDecimal(row.find("td", 2).text().split(" ")[0]),
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                        ))
                );
    }

    @Step("Select period")
    public SpendingTable selectPeriod(DataFilterValues period) {
        periodInput.click();
        periodList.find(text(period.name())).click();
        return this;
    }

    @Step("Edit spending '{description}'")
    public EditSpendingPage editSpending(String description) {
        searchSpendingByDescription(description);
        spendingRows.first().find(editButton).click();
        return new EditSpendingPage();
    }

    @Step("Delete spending '{description}'")
    public SpendingTable deleteSpending(String description) {
        searchSpendingByDescription(description);
        spendingRows.first().find(checkboxButton).click();
        deleteButton.click();
        popup.find(byText("Delete")).click(usingJavaScript());
        return this;
    }

    @Step("Search spending '{description}'")
    public SpendingTable searchSpendingByDescription(String description) {
        searchField.search(description);
        return this;
    }

    @Step("Check table contains spends")
    public SpendingTable checkTableContains(String... expectedSpends) {
        for (String spend : expectedSpends) {
            searchField.clearIfNotEmpty();
            searchField.search(spend);
            spendingRows.first().find("td", 3).shouldHave(text(spend));
        }
        return this;
    }

    @Step("Check table size is {expectedSize}")
    public SpendingTable checkTableSize(int expectedSize) {
        spendingRows.shouldHave(size(expectedSize));
        return this;
    }
}
