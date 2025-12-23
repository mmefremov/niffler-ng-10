package guru.qa.niffler.test.rest;

import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.impl.UsersApiClient;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Isolated;

import java.util.List;

@Isolated
@Disabled
class LastTests {

    private final UsersApiClient usersApiClient = new UsersApiClient();

    @Test
    @DisplayName("Таблица пользователей не должна быть пуста")
    void userListShouldNotBeEmpty() {
        List<UserJson> userList = usersApiClient.getAll("");
        Assertions.assertThat(userList).isNotEmpty();
    }
}
