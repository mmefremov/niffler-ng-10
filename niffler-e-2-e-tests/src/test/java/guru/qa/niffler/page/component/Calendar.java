package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class Calendar {

    private final SelenideElement self = $(".MuiInputBase-adornedEnd");

    private final SelenideElement calendarButton = self.find("button");

    private final SelenideElement dateCalendar = $(".MuiDateCalendar-root");

    private final SelenideElement currentYearMonth = dateCalendar.find(".MuiPickersCalendarHeader-label");

    private final SelenideElement yearViewButton = dateCalendar.find(".MuiPickersCalendarHeader-switchViewButton");

    private final SelenideElement previousMonthButton = dateCalendar.find("button[title='Previous month']");

    private final SelenideElement nextMonthButton = dateCalendar.find("button[title='Next month']");

    private final ElementsCollection yearCalendarList = dateCalendar.find(".MuiYearCalendar-root")
            .findAll("button");

    private final ElementsCollection dayCalendarList = dateCalendar.find(".MuiDayCalendar-monthContainer")
            .findAll("button");

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);

    @Step("Select date '{date}' in calendar")
    public Calendar selectDateInCalendar(Date date) {
        calendarButton.click();
        LocalDate dateToSelect = LocalDate.ofInstant(date.toInstant(), ZoneId.systemDefault());
        selectYear(dateToSelect.getYear());
        selectMonth(dateToSelect.getMonthValue());
        selectDay(dateToSelect.getDayOfMonth());
        return this;
    }

    private void selectYear(int year) {
        int currentYear = YearMonth.parse(currentYearMonth.text(), formatter).getYear();

        if (currentYear != year) {
            yearViewButton.click();
            yearCalendarList.find(text(String.valueOf(year)))
                    .scrollTo().shouldBe(visible)
                    .click();
        }
    }

    private void selectMonth(int month) {
        int currentMonth = YearMonth.parse(currentYearMonth.text(), formatter).getMonthValue();

        while (currentMonth != month) {
            if (currentMonth < month) {
                nextMonthButton.click();
                currentMonth++;
            } else {
                previousMonthButton.click();
                currentMonth--;
            }
        }
    }

    private void selectDay(int day) {
        dayCalendarList.find(text(String.valueOf(day)))
                .click();
    }
}
