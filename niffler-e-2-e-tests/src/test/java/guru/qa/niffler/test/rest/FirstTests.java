package guru.qa.niffler.test.rest;

import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UsersApiClient;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.List;

@Order(1)
@Execution(ExecutionMode.SAME_THREAD)
class FirstTests {

    private final UsersApiClient usersApiClient = new UsersApiClient();

    @Test
    @DisplayName("Таблица должна быть пуста")
    void userListShouldBeEmpty() {
        List<UserJson> userList = usersApiClient.getAll("");
        Assertions.assertThat(userList).isEmpty();
    }
}
