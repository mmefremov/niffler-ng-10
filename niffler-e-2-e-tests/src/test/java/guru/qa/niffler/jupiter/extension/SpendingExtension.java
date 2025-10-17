package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.SpendApiClient;
import guru.qa.niffler.service.SpendClient;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Date;

public class SpendingExtension implements BeforeEachCallback, ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(SpendingExtension.class);
  private final SpendClient spendClient = new SpendApiClient();

  @Override
  public void beforeEach(ExtensionContext context) {
    AnnotationSupport.findAnnotation(
        context.getRequiredTestMethod(),
        User.class
    ).ifPresent(
            user -> {
                if (user.spendings().length > 0) {
                    Spending spending = user.spendings()[0];
                    SpendJson created;
                    created = spendClient.createSpend(
                            new SpendJson(
                                    null,
                                    new Date(),
                                    new CategoryJson(
                                            null,
                                            spending.category(),
                                            user.username(),
                                            false
                                    ),
                                    spending.currency(),
                                    spending.amount(),
                                    spending.description(),
                                    user.username()
                            )
                    );
                    context.getStore(NAMESPACE).put(
                            context.getUniqueId(),
                            created
                    );
                }
        }
    );
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(SpendJson.class);
  }

  @Override
  public SpendJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return extensionContext.getStore(NAMESPACE)
        .get(extensionContext.getUniqueId(), SpendJson.class);
  }
}
