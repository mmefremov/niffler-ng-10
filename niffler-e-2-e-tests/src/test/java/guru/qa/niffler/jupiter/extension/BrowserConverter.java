package guru.qa.niffler.jupiter.extension;

import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.SelenideDriver;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ArgumentConverter;

public class BrowserConverter implements ArgumentConverter {

    @Override
    public Object convert(Object source, ParameterContext context) throws ArgumentConversionException {
        if (source instanceof String) {
            return new SelenideDriver(getConfig(String.valueOf(source)));
        }
        throw new ArgumentConversionException("Wrong argument type");
    }

    private SelenideConfig getConfig(String browser) {
        return new SelenideConfig().browser(browser);
    }
}
