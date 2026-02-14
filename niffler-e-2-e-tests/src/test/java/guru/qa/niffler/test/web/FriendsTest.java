package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.FriendsPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({BrowserExtension.class, UsersQueueExtension.class})
class FriendsTest {

    @User(
            friends = 1
    )
    @Test
    @ApiLogin
    @DisplayName("Таблица друзей содержит друга")
    void friendShouldBePresentInFriendsTable(UserJson user) {
        Selenide.open(FriendsPage.URL, FriendsPage.class)
                .friendsTableShouldContainFriend(user.testData().friends().getFirst().username());
    }

    @User
    @Test
    @ApiLogin
    @DisplayName("Таблица друзей пустая")
    void friendsTableShouldBeEmptyForNewUser() {
        Selenide.open(FriendsPage.URL, FriendsPage.class)
                .friendsTableShouldBeEmpty();
    }

    @User(
            incomeInvitations = 1
    )
    @Test
    @ApiLogin
    @DisplayName("Таблица друзей содержит входящий запрос")
    void incomeInvitationBePresentInFriendsTable(UserJson user) {
        Selenide.open(FriendsPage.URL, FriendsPage.class)
                .requestsTableShouldContainIncomeFriend(user.testData().incomeInvitations().getFirst().username());
    }

    @User(
            outcomeInvitations = 1
    )
    @Test
    @ApiLogin
    @DisplayName("Список всех людей содержит исходящий запрос")
    void outcomeInvitationBePresentInAllPeoplesTable(UserJson user) {
        Selenide.open(FriendsPage.URL, FriendsPage.class)
                .openPeopleTab()
                .allPeoplesTableShouldContainWaitingAnswerFromFriend(user.testData().outcomeInvitations().getFirst().username());
    }

    @User(
            incomeInvitations = 1
    )
    @Test
    @ApiLogin
    @DisplayName("Прием заявки в друзья")
    void acceptIncomeInvitation(UserJson user) {
        String friendName = user.testData().incomeInvitations().getFirst().username();
        Selenide.open(FriendsPage.URL, FriendsPage.class)
                .requestsTableShouldContainIncomeFriend(friendName)
                .acceptInvitationFrom(friendName)
                .checkAlert("Invitation of %s accepted".formatted(friendName))
                .friendsTableShouldContainFriend(friendName);
    }

    @User(
            incomeInvitations = 1
    )
    @Test
    @ApiLogin
    @DisplayName("Отклонение заявки в друзья")
    void declineIncomeInvitation(UserJson user) {
        String friendName = user.testData().incomeInvitations().getFirst().username();
        Selenide.open(FriendsPage.URL, FriendsPage.class)
                .requestsTableShouldContainIncomeFriend(friendName)
                .declineInvitationFrom(friendName)
                .checkAlert("Invitation of %s is declined".formatted(friendName))
                .friendsTableShouldBeEmpty();
    }
}
