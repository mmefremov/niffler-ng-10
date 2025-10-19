package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.service.SpendApiClient;
import guru.qa.niffler.util.RandomDataUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

public class CategoryExtension implements BeforeEachCallback, AfterTestExecutionCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);
    private final SpendApiClient spendApiClient = new SpendApiClient();

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(
                        context.getRequiredTestMethod(), User.class)
                .ifPresent(
                        user -> {
                            if (ArrayUtils.isNotEmpty(user.categories())) {
                                CategoryJson createdCategory = spendApiClient.createCategory(
                                        new CategoryJson(null,
                                                         RandomDataUtils.randomCategoryName(),
                                                         user.username(),
                                                         false)
                                );
                                if (user.categories()[0].archived()) {
                                    createdCategory = spendApiClient.updateCategory(
                                            new CategoryJson(createdCategory.id(),
                                                             createdCategory.name(),
                                                             createdCategory.username(),
                                                             true));
                                }
                                context.getStore(NAMESPACE)
                                        .put(context.getUniqueId(), createdCategory);
                            }
                        });
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        CategoryJson category = context.getStore(NAMESPACE)
                .get(context.getUniqueId(), CategoryJson.class);
        if (category != null && !category.archived()) {
            spendApiClient.updateCategory(
                    new CategoryJson(category.id(),
                                     category.name(),
                                     category.username(),
                                     true));
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(CategoryJson.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE)
                .get(extensionContext.getUniqueId(), CategoryJson.class);
    }
}
