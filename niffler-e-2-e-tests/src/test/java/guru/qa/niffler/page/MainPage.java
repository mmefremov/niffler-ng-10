package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.condition.Color;
import guru.qa.niffler.model.Bubble;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.page.component.SpendingTable;
import guru.qa.niffler.page.component.Statistics;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@ParametersAreNonnullByDefault
public class MainPage extends BasePage<MainPage> {

    public static final String URL = CFG.frontUrl() + "main";

    private final Statistics statistics;

    private final SpendingTable spendingTable;

    public MainPage(SelenideDriver driver) {
        super(driver);
        statistics = new Statistics(driver);
        spendingTable = new SpendingTable(driver);
    }

    @Nonnull
    @Step("Check that page loaded")
    public MainPage checkThatPageLoaded() {
        statistics.shouldBeVisible();
        spendingTable.shouldBeVisible();
        return this;
    }

    @Nonnull
    public MainPage checkSpends(List<SpendJson> expectedSpends) {
        spendingTable.checkSpends(expectedSpends);
        return this;
    }

    @Nonnull
    public MainPage checkChartImage(BufferedImage expected) {
        statistics.checkChartImage(expected);
        return this;
    }

    @Nonnull
    @Step("Check the legend list")
    public MainPage checkLegendList(List<SpendJson> spendings) {
        List<String> legendList = spendings.stream()
                .collect(Collectors.groupingBy(
                        (SpendJson spend) -> spend.category().archived() ? "Archived" : spend.category().name(),
                        Collectors.mapping(
                                (SpendJson spend) -> BigDecimal.valueOf(spend.amount()),
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))))
                .entrySet().stream()
                .sorted((e1, e2) -> {
                    if ("Archived".equals(e1.getKey())) {
                        return 1;
                    }
                    return e2.getValue().compareTo(e1.getValue());
                })
                .map(entry -> "%s %s ₽".formatted(
                        entry.getKey(), entry.getValue().stripTrailingZeros().toPlainString()))
                .toList();
        Color[] colors = Color.values();
        Bubble[] expectedBubbles = legendList.stream()
                .map(legend -> new Bubble(colors[legendList.indexOf(legend)], legend))
                .toArray(Bubble[]::new);
        statistics.checkBubblesInAnyOrder(expectedBubbles);
        return this;
    }

    @Nonnull
    @Step("Check the legend list")
    public MainPage checkLegendList() {
        var spendingSummary = spendingTable.getSpendingCategoriesWithAmounts();
        statistics.checkBubblesInAnyOrder(spendingSummary);
        return this;
    }

    @Nonnull
    @Step("Open profile")
    public ProfilePage openProfile() {
        return header.toProfilePage();
    }

    @Nonnull
    @Step("Open friends")
    public FriendsPage openFriends() {
        return header.toFriendsPage();
    }

    @Nonnull
    @Step("Add a new spending")
    public EditSpendingPage addSpending() {
        return header.addSpendingPage();
    }

    @Nonnull
    @Step("Edit spending")
    public EditSpendingPage editSpending(String description) {
        return spendingTable.editSpending(description);
    }

    @Nonnull
    @Step("Delete spending")
    public MainPage deleteSpending(String description) {
        spendingTable.deleteSpending(description);
        return this;
    }

    @Nonnull
    @Step("Check that table contains '{description}'")
    public MainPage checkThatTableContains(String description) {
        spendingTable.checkTableContains(description);
        return this;
    }
}
