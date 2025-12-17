package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({BrowserExtension.class, UsersQueueExtension.class})
class FriendsTest {

    private static final Config CFG = Config.getInstance();

    @User(
            friends = 1
    )
    @Test
    @DisplayName("Таблица друзей содержит друга")
    void friendShouldBePresentInFriendsTable(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.testData().password())
                .openFriends()
                .friendsTableShouldContainFriend(user.testData().friends().getFirst().username());
    }

    @User
    @Test
    @DisplayName("Таблица друзей пустая")
    void friendsTableShouldBeEmptyForNewUser(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.testData().password())
                .openFriends()
                .friendsTableShouldBeEmpty();
    }

    @User(
            incomeInvitations = 1
    )
    @Test
    @DisplayName("Таблица друзей содержит входящий запрос")
    void incomeInvitationBePresentInFriendsTable(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.testData().password())
                .openFriends()
                .requestsTableShouldContainIncomeFriend(user.testData().incomeInvitations().getFirst().username());
    }

    @User(
            outcomeInvitations = 1
    )
    @Test
    @DisplayName("Список всех людей содержит исходящий запрос")
    void outcomeInvitationBePresentInAllPeoplesTable(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.testData().password())
                .openFriends()
                .openPeopleTab()
                .allPeoplesTableShouldContainWaitingAnswerFromFriend(user.testData().outcomeInvitations().getFirst().username());
    }

    @User(
            incomeInvitations = 1
    )
    @Test
    @DisplayName("Прием заявки в друзья")
    void acceptIncomeInvitation(UserJson user) {
        String friendName = user.testData().incomeInvitations().getFirst().username();
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.testData().password())
                .openFriends()
                .requestsTableShouldContainIncomeFriend(friendName)
                .acceptInvitationFrom(friendName)
                .friendsTableShouldContainFriend(friendName);
    }

    @User(
            incomeInvitations = 1
    )
    @Test
    @DisplayName("Отклонение заявки в друзья")
    void declineIncomeInvitation(UserJson user) {
        String friendName = user.testData().incomeInvitations().getFirst().username();
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.testData().password())
                .openFriends()
                .requestsTableShouldContainIncomeFriend(friendName)
                .declineInvitationFrom(friendName)
                .friendsTableShouldBeEmpty();
    }
}
