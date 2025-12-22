package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
abstract class BaseComponent<T extends BaseComponent<?>> {

    final SelenideElement self;

    BaseComponent(SelenideElement self) {
        this.self = self;
    }
}
