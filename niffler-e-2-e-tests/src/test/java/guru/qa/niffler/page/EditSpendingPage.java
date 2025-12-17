package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.Calendar;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Date;

import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class EditSpendingPage extends BasePage<EditSpendingPage> {

    private final SelenideElement amountInput = $("#amount");

    private final SelenideElement categoryInput = $("#category");

    private final SelenideElement descriptionInput = $("#description");

    private final SelenideElement saveBtn = $("#save");

    private final Calendar calendar = new Calendar();

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
    public MainPage save() {
        saveBtn.click();
        return new MainPage();
    }
}
