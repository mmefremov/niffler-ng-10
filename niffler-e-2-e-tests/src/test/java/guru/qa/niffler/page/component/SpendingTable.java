package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideDriver;
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

    private final SearchField searchField;

    private final SelenideElement periodInput;

    private final SelenideElement deleteButton;

    private final SelenideElement popup;

    private final By editButton;

    private final By checkboxButton;

    private final ElementsCollection periodList;

    private final ElementsCollection spendingRows;

    public SpendingTable(SelenideDriver driver) {
        super(driver, driver.$("#spendings"));
        searchField = new SearchField(driver);
        periodInput = self.$("#period");
        deleteButton = self.$("#delete");
        popup = $("div[role='dialog']");
        editButton = By.cssSelector("button");
        checkboxButton = By.cssSelector(".PrivateSwitchBase-input");
        periodList = self.$$(".MuiMenu-list");
        spendingRows = self.$("tbody").$$("tr");
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
        return new EditSpendingPage(driver);
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
