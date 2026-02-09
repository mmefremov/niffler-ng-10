package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.condition.Color;
import guru.qa.niffler.model.Bubble;
import guru.qa.niffler.utils.ScreenDiffResult;
import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static guru.qa.niffler.condition.StatConditions.statBubbles;
import static guru.qa.niffler.condition.StatConditions.statBubblesInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ParametersAreNonnullByDefault
public class Statistics extends BaseComponent<Statistics> {

    private final SelenideElement chart;

    private final ElementsCollection bubbles;

    private final ElementsCollection legendTable;

    public Statistics(SelenideDriver driver) {
        super(driver, driver.$("#stat"));
        chart = self.find("#chart canvas");
        bubbles = driver.$$("#legend-container li");
        legendTable = driver.$$("#legend-container ul li");
    }

    @Step("Check the chart image")
    public void checkChartImage(BufferedImage expected) {
        Selenide.sleep(2000);
        BufferedImage actual = chart.screenshotAsImage();
        assertFalse(new ScreenDiffResult(actual, expected));
    }

    @Step("Check that stat bubbles contains colors: {expectedBubbles}")
    public void checkBubblesInAnyOrder(Bubble... expectedBubbles) {
        bubbles.should(statBubbles(expectedBubbles));
    }

    @Step("Check that stat bubbles contains bubble in any order")
    public void checkBubblesInAnyOrder(Map<String, BigDecimal> spendingSummary) {
        List<String> currentCategories = legendTable.stream()
                .map(legend -> legend.getText().split(" ")[0])
                .toList();
        Color[] colors = Color.values();
        Bubble[] expectedBubbles = spendingSummary.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> currentCategories.contains(entry.getKey()) ? entry.getKey() : "Archived",
                        Map.Entry::getValue,
                        (a, b) -> a.add(b),
                        LinkedHashMap::new)
                ).entrySet().stream()
                .map(entry -> {
                    Color color = colors[currentCategories.indexOf(entry.getKey())];
                    String text = "%s %s ₽".formatted(
                            entry.getKey(), entry.getValue().stripTrailingZeros().toPlainString());
                    return new Bubble(color, text);
                })
                .toArray(Bubble[]::new);
        bubbles.should(statBubblesInAnyOrder(expectedBubbles));
    }
}
