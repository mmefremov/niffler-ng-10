package guru.qa.niffler.jupiter.extension;

import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.SelenideDriver;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ArgumentConverter;

public class BrowserConverter implements ArgumentConverter {

    @Override
    public Object convert(Object source, ParameterContext context) throws ArgumentConversionException {
        if (source instanceof Browser browser) {
            return new SelenideDriver(getConfig(browser));
        }
        throw new ArgumentConversionException("Wrong argument type");
    }

    private SelenideConfig getConfig(Browser browser) {
        return new SelenideConfig().browser(browser.name());
    }
}
