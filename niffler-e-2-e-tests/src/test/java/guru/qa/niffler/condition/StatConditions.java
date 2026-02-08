package guru.qa.niffler.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.WebElementsCondition;
import guru.qa.niffler.model.Bubble;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static com.codeborne.selenide.CheckResult.accepted;
import static com.codeborne.selenide.CheckResult.rejected;

@ParametersAreNonnullByDefault
public class StatConditions {

    @Nonnull
    public static WebElementCondition statBubbles(Bubble expectedBubble) {
        return new WebElementCondition("bubble " + expectedBubble) {
            @NotNull
            @Override
            public CheckResult check(Driver driver, WebElement element) {
                final String color = element.getCssValue("background-color");
                final String text = element.getText();
                return new CheckResult(
                        expectedBubble.color().rgba.equals(color) && expectedBubble.text().equals(text),
                        color
                );
            }
        };
    }

    @Nonnull
    public static WebElementsCondition statBubbles(Bubble... expectedBubbles) {
        return new WebElementsCondition() {

            private final List<Bubble> expectedBubbleList = List.of(expectedBubbles);

            @NotNull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                if (expectedBubbleList.isEmpty()) {
                    throw new IllegalArgumentException("No expected bubbles given");
                }
                if (expectedBubbleList.size() != elements.size()) {
                    String message = String.format("List size mismatch (expected: %s, actual: %s)",
                                                   expectedBubbleList.size(), elements.size());
                    return rejected(message, elements);
                }
                List<Bubble> actualBubbleList = elements.stream()
                        .map(element -> {
                            String rgba = element.getCssValue("background-color");
                            return new Bubble(Color.getByRgba(rgba), element.getText());
                        })
                        .toList();
                boolean passed = true;
                for (int i = 0; i < expectedBubbleList.size(); i++) {
                    Bubble actualBubble = actualBubbleList.get(i);
                    Bubble expectedBubble = expectedBubbleList.get(i);
                    passed = actualBubble.equals(expectedBubble);
                }
                if (!passed) {
                    String message = String.format(
                            "List bubbles mismatch (expected: %s, actual: %s)",
                            expectedBubbleList, actualBubbleList
                    );
                    return rejected(message, actualBubbleList);
                }
                return accepted();
            }

            @Override
            public @NonNull String toString() {
                return expectedBubbleList.toString();
            }
        };
    }

    @Nonnull
    public static WebElementsCondition statBubblesInAnyOrder(Bubble... expectedBubbles) {
        return new WebElementsCondition() {

            private final List<Bubble> expectedBubbleList = List.of(expectedBubbles);

            @NotNull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                if (expectedBubbleList.isEmpty()) {
                    throw new IllegalArgumentException("No expected bubbles given");
                }
                if (expectedBubbleList.size() != elements.size()) {
                    String message = String.format("List size mismatch (expected: %s, actual: %s)",
                                                   expectedBubbleList.size(), elements.size());
                    return rejected(message, elements);
                }
                List<Bubble> actualBubbleList = elements.stream()
                        .map(element -> {
                            String rgba = element.getCssValue("background-color");
                            return new Bubble(Color.getByRgba(rgba), element.getText());
                        })
                        .toList();
                boolean passed = actualBubbleList.containsAll(expectedBubbleList);
                if (!passed) {
                    String message = String.format(
                            "Actual bubbles should contain expected bubbles in any order (expected: %s, actual: %s)",
                            expectedBubbleList, actualBubbleList
                    );
                    return rejected(message, actualBubbleList);
                }
                return accepted();
            }

            @Override
            public @NonNull String toString() {
                return expectedBubbleList.toString();
            }
        };
    }

    @Nonnull
    public static WebElementsCondition statBubblesContains(Bubble... expectedBubbles) {
        return new WebElementsCondition() {

            private final List<Bubble> expectedBubbleList = List.of(expectedBubbles);

            @NotNull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                if (expectedBubbleList.isEmpty()) {
                    throw new IllegalArgumentException("No expected bubbles given");
                }
                List<Bubble> actualBubbleList = elements.stream()
                        .map(element -> {
                            String rgba = element.getCssValue("background-color");
                            return new Bubble(Color.getByRgba(rgba), element.getText());
                        })
                        .toList();
                boolean passed = actualBubbleList.containsAll(expectedBubbleList);
                if (!passed) {
                    String message = String.format(
                            "Actual bubbles should contain all expected bubbles (expected: %s, actual: %s)",
                            expectedBubbleList, actualBubbleList
                    );
                    return rejected(message, actualBubbleList);
                }
                return accepted();
            }

            @Override
            public @NonNull String toString() {
                return expectedBubbleList.toString();
            }
        };
    }
}
