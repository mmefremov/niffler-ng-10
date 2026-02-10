package guru.qa.niffler.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementsCondition;
import guru.qa.niffler.model.SpendJson;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.StringJoiner;

import static com.codeborne.selenide.CheckResult.accepted;
import static com.codeborne.selenide.CheckResult.rejected;

@ParametersAreNonnullByDefault
public class SpendConditions {

    @Nonnull
    public static WebElementsCondition containSpends(List<SpendJson> expectedSpendList) {
        return new WebElementsCondition() {

            private static final SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");

            private final List<String> expectedLines = expectedSpendList.stream()
                    .map(spendJson -> {
                        StringJoiner joiner = new StringJoiner(" ");
                        joiner.add(spendJson.category().name());
                        joiner.add(BigDecimal.valueOf(spendJson.amount()).stripTrailingZeros().toPlainString());
                        joiner.add(spendJson.currency().sign);
                        joiner.add(spendJson.description());
                        joiner.add(formatter.format(spendJson.spendDate()));
                        return joiner.toString();
                    })
                    .toList();

            @NotNull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                if (expectedSpendList.isEmpty()) {
                    throw new IllegalArgumentException("No expected bubbles given");
                }
                if (expectedSpendList.size() != elements.size()) {
                    String message = String.format("List size mismatch (expected: %s, actual: %s)",
                                                   expectedSpendList.size(), elements.size());
                    return rejected(message, elements);
                }
                List<String> actualLines = elements.stream()
                        .map(element -> {
                            List<WebElement> cells = element.findElements(By.cssSelector("td"));
                            StringJoiner joiner = new StringJoiner(" ");
                            joiner.add(cells.get(1).getText());
                            joiner.add(cells.get(2).getText());
                            joiner.add(cells.get(3).getText());
                            joiner.add(cells.get(4).getText());
                            return joiner.toString();
                        })
                        .toList();
                boolean passed = true;
                for (int i = 0; i < expectedLines.size(); i++) {
                    String expectedLine = expectedLines.get(i);
                    String actualLine = actualLines.get(i);
                    passed = expectedLine.equals(actualLine);
                }
                if (!passed) {
                    String message = String.format(
                            "List bubbles mismatch (expected: %s, actual: %s)", expectedLines, actualLines
                    );
                    return rejected(message, actualLines);
                }
                return accepted();
            }

            @Override
            public @NonNull String toString() {
                return expectedLines.toString();
            }
        };
    }
}
