package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.Calendar;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Date;

@ParametersAreNonnullByDefault
public class EditSpendingPage extends BasePage<EditSpendingPage> {

    public static final String URL = CFG.frontUrl() + "spending";

    private final SelenideElement amountInput;

    private final SelenideElement categoryInput;

    private final SelenideElement descriptionInput;

    private final SelenideElement saveBtn;

    private final Calendar calendar;

    public EditSpendingPage(SelenideDriver driver) {
        super(driver);
        amountInput = driver.$("#amount");
        categoryInput = driver.$("#category");
        descriptionInput = driver.$("#description");
        saveBtn = driver.$("#save");
        calendar = new Calendar(driver);
    }

    @Nonnull
    @Step("Set new spending description")
    public EditSpendingPage setNewSpendingDescription(String description) {
        descriptionInput.setValue(description);
        return this;
    }

    @Nonnull
    @Step("Set new spending amount")
    public EditSpendingPage setNewSpendingAmount(long amount) {
        amountInput.setValue(String.valueOf(amount));
        return this;
    }

    @Nonnull
    @Step("Set new spending category")
    public EditSpendingPage setNewSpendingCategory(String category) {
        categoryInput.setValue(category);
        return this;
    }

    @Nonnull
    @Step("Set new spending date")
    public EditSpendingPage setNewSpendingDate(Date date) {
        calendar.selectDateInCalendar(date);
        return this;
    }

    @Nonnull
    @Step("Save spending")
    public MainPage saveSpending() {
        saveBtn.click();
        return new MainPage(driver);
    }
}
