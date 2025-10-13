package guru.qa.niffler.jupiter.extension;

import io.qameta.allure.Allure;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class UsersQueueExtension implements
        BeforeTestExecutionCallback,
        AfterTestExecutionCallback,
        ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UsersQueueExtension.class);

    public record StaticUser(
            String username,
            String password,
            String friend,
            String income,
            String outcome) {
    }

    private static final Queue<StaticUser> EMPTY_USERS = new ConcurrentLinkedQueue<>();
    private static final Queue<StaticUser> WITH_FRIEND_USERS = new ConcurrentLinkedQueue<>();
    private static final Queue<StaticUser> WITH_INCOME_REQUEST_USERS = new ConcurrentLinkedQueue<>();
    private static final Queue<StaticUser> WITH_OUTCOME_REQUEST_USERS = new ConcurrentLinkedQueue<>();
    private static final Map<String, Map<UserType, List<StaticUser>>> USED_USERS = new ConcurrentHashMap<>();

    static {
        EMPTY_USERS.add(new StaticUser("bee", "12345", null, null, null));
        EMPTY_USERS.add(new StaticUser("sheep", "12345", null, null, null));
        WITH_FRIEND_USERS.add(new StaticUser("duck", "12345", "dima", null, null));
        WITH_FRIEND_USERS.add(new StaticUser("wolf", "12345", "igor", null, null));
        WITH_INCOME_REQUEST_USERS.add(new StaticUser("dima", "12345", null, "barsik", null));
        WITH_INCOME_REQUEST_USERS.add(new StaticUser("igor", "12345", null, "murzik", null));
        WITH_OUTCOME_REQUEST_USERS.add(new StaticUser("barsik", "12345", null, null, "dima"));
        WITH_OUTCOME_REQUEST_USERS.add(new StaticUser("murzik", "12345", null, null, "igor"));
    }

    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface UserType {

        Type value() default Type.EMPTY;

        enum Type {
            EMPTY, WITH_FRIEND, WITH_INCOME_REQUEST, WITH_OUTCOME_REQUEST
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void beforeTestExecution(ExtensionContext context) {
        Map<UserType, Queue<StaticUser>> usersMap = Arrays.stream(context.getRequiredTestMethod().getParameters())
                .filter(p -> AnnotationSupport.isAnnotated(p, UserType.class))
                .map(p -> p.getAnnotation(UserType.class))
                .map(userType -> {
                    Optional<StaticUser> userOptional = Optional.empty();
                    StopWatch sw = StopWatch.createStarted();
                    while (userOptional.isEmpty() && sw.getTime(TimeUnit.SECONDS) < 30) {
                        userOptional = Optional.ofNullable(
                                getQueueByUserType(userType).poll()
                        );
                    }
                    Allure.getLifecycle().updateTestCase(testCase ->
                                                                 testCase.setStart(new Date().getTime())
                    );
                    return Map.entry(
                            userType,
                            userOptional.orElseThrow(
                                    () -> new IllegalStateException("Can`t obtain user after 30s.")
                            ));
                })
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(
                                Map.Entry::getValue,
                                Collectors.toCollection(ConcurrentLinkedQueue::new))));
        context.getStore(NAMESPACE).put(context.getUniqueId(), usersMap);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void afterTestExecution(ExtensionContext context) {
        USED_USERS
                .get(context.getUniqueId())
                .forEach((UserType userType, List<StaticUser> usersList) -> {
                             getQueueByUserType(userType).addAll(usersList);
                             usersList.clear();
                         }
                );
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(StaticUser.class)
               && AnnotationSupport.isAnnotated(parameterContext.getParameter(), UserType.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public StaticUser resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        ExtensionContext.Store store = extensionContext.getStore(NAMESPACE);

        return parameterContext.findAnnotation(UserType.class)
                .map(userType -> {
                    Map<UserType, Queue<StaticUser>> userMap = store
                            .get(extensionContext.getUniqueId(), Map.class);
                    StaticUser user = userMap.get(userType).remove();
                    USED_USERS.computeIfAbsent(
                                    extensionContext.getUniqueId(),
                                    id -> new HashMap<>())
                            .computeIfAbsent(
                                    userType,
                                    type -> new ArrayList<>())
                            .add(user);
                    return user;
                })
                .orElseThrow(() -> new ParameterResolutionException("No @UserType annotation found")
                );
    }

    private Queue<StaticUser> getQueueByUserType(UserType userType) {
        return switch (userType.value()) {
            case EMPTY -> EMPTY_USERS;
            case WITH_FRIEND -> WITH_FRIEND_USERS;
            case WITH_INCOME_REQUEST -> WITH_INCOME_REQUEST_USERS;
            case WITH_OUTCOME_REQUEST -> WITH_OUTCOME_REQUEST_USERS;
        };
    }
}
