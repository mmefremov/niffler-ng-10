package guru.qa.niffler.jupiter.extension;

import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.TestResult;
import lombok.SneakyThrows;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class AllureBackendLogsExtension implements SuiteExtension {

    public static final String CASE_NAME = "Niffler backend logs";

    @SneakyThrows
    @Override
    public void afterSuite() {
        final AllureLifecycle allureLifecycle = Allure.getLifecycle();
        final String caseId = UUID.randomUUID().toString();
        allureLifecycle.scheduleTestCase(new TestResult().setUuid(caseId).setName(CASE_NAME));
        allureLifecycle.startTestCase(caseId);

        attachServiceLogs(allureLifecycle, "niffler-auth");
        attachServiceLogs(allureLifecycle, "niffler-currency");
        attachServiceLogs(allureLifecycle, "niffler-gateway");
        attachServiceLogs(allureLifecycle, "niffler-spend");
        attachServiceLogs(allureLifecycle, "niffler-userdata");

        allureLifecycle.stopTestCase(caseId);
        allureLifecycle.writeTestCase(caseId);
    }

    private void attachServiceLogs(AllureLifecycle allureLifecycle, String serviceName) throws IOException {
        allureLifecycle.addAttachment(
                "%s log".formatted(serviceName),
                "text/html",
                ".log",
                Files.newInputStream(
                        Path.of("./logs/%s/app.log".formatted(serviceName))
                )
        );
    }
}
