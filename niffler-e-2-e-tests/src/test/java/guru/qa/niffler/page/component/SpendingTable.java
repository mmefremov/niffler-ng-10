package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.DataFilterValues;
import guru.qa.niffler.page.EditSpendingPage;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class SpendingTable {

    private final SelenideElement self = $("#spendings");

    private final SearchField searchField = new SearchField();

    private final SelenideElement periodInput = self.$("#period");

    private final SelenideElement deleteButton = self.$("##delete");

    private final By editButton = By.cssSelector("button");

    private final By checkboxButton = By.cssSelector(".PrivateSwitchBase-input");

    private final ElementsCollection periodList = self.$$(".MuiMenu-list");

    private final ElementsCollection spendingRows = self.$("tbody").$$("tr");

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
