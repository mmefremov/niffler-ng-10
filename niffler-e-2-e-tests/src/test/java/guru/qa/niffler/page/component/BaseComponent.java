package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.visible;

@ParametersAreNonnullByDefault
abstract class BaseComponent<T extends BaseComponent<?>> {

    protected final SelenideDriver driver;

    final SelenideElement self;

    BaseComponent(SelenideDriver driver, SelenideElement self) {
        this.driver = driver;
        this.self = self;
    }

    public void shouldBeVisible() {
        self.shouldBe(visible);
    }
}
