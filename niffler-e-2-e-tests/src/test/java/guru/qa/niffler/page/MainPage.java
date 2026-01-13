package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.SpendingTable;
import guru.qa.niffler.utils.ScreenDiffResult;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ParametersAreNonnullByDefault
public class MainPage extends BasePage<MainPage> {

    public static final String URL = CFG.frontUrl() + "main";
    
    private final SelenideElement statistics = $("#stat");

    private final SelenideElement chart = $("#chart canvas");

    private final ElementsCollection legendTable = $$("#legend-container ul li");

    private final SpendingTable spendingTable = new SpendingTable();

    @Nonnull
    @Step("Check that page loaded")
    public MainPage checkThatPageLoaded() {
        statistics.shouldBe(visible);
        return this;
    }

    @Nonnull
    @Step("Check the chart image")
    public MainPage checkChartImage(BufferedImage expected) throws IOException {
        Selenide.sleep(2000);
        BufferedImage actual = ImageIO.read(chart.screenshot());
        assertFalse(new ScreenDiffResult(actual, expected));
        return this;
    }

    @Nonnull
    @Step("Check the legend list")
    public MainPage checkLegendList() {
        var spendingSummary = spendingTable.getSpendingCategoriesWithAmounts();
        double spendingsSum = spendingSummary.values().stream()
                .reduce(0.0, (a, b) -> a + b);

        for (SelenideElement legend : legendTable) {
            String[] legendParts = Objects.requireNonNull(legend.getText()).split(" ");
            String categoryName = legendParts[0];
            double categoryAmount = Double.valueOf(legendParts[1]);

            if (!"Archived".equals(categoryName)) {
                assertThat(spendingSummary).as("category name").containsKey(categoryName);
                assertThat(spendingSummary.get(categoryName)).as("category amount").isCloseTo(categoryAmount, within(0.01));
                spendingsSum -= categoryAmount;
            } else {
                assertThat(spendingSummary.get(categoryName)).as("category amount").isCloseTo(spendingsSum, within(0.01));
            }
        }
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
