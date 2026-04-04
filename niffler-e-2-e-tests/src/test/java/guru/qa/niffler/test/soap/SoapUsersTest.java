package guru.qa.niffler.test.soap;

import guru.qa.jaxb.userdata.FriendshipStatus;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.SoapTest;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.impl.UserdataSoapClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SoapTest
public class SoapUsersTest {

    private final UserdataSoapClient userdataSoapClient = new UserdataSoapClient();

    @Test
    @DisplayName("Список друзей получен в виде Page при передаче параметров page, size")
    @User(friends = 3)
    void friendListShouldBePageable(UserJson user) throws IOException {
        var response = userdataSoapClient.friends(user.username(), 0, 3);
        assertThat(response.getTotalElements()).isEqualTo(3);
        assertThat(response.getUser()).hasSize(1)
                .extracting(guru.qa.jaxb.userdata.User::getFriendshipStatus)
                .allMatch(status -> status == FriendshipStatus.FRIEND);
    }

    @Test
    @DisplayName("Список друзей с фильтраций по username")
    @User(friends = 3)
    void friendListFilteredBySearchQuery(UserJson user) throws IOException {
        String friendName = user.testData().friends().getFirst().username();
        var response = userdataSoapClient.friends(user.username(), friendName);
        assertThat(response.getTotalElements()).isOne();
        assertThat(response.getUser()).hasSize(1)
                .first().matches(
                friend -> friend.getUsername().equals(friendName) && friend.getFriendshipStatus() == FriendshipStatus.FRIEND);
    }

    @Test
    @DisplayName("Дружба должна удаляться")
    @User(friends = 1)
    void removeFriendship(UserJson user) throws IOException {
        String friendName = user.testData().friends().getFirst().username();
        userdataSoapClient.removeFriend(user.username(), friendName);
        var response = userdataSoapClient.friends(user.username());
        assertThat(response.getUser()).isEmpty();
    }

    @Test
    @DisplayName("Прием заявки в друзья")
    @User(incomeInvitations = 1)
    void acceptIncomeInvitation(UserJson user) throws IOException {
        String addresseeName = user.testData().incomeInvitations().getFirst().username();
        var response = userdataSoapClient.acceptInvitation(user.username(), addresseeName);
        assertThat(response.getUser().getFriendshipStatus()).isEqualTo(FriendshipStatus.FRIEND);
    }

    @Test
    @DisplayName("Отклонение заявки в друзья")
    @User(incomeInvitations = 1)
    void rejectIncomeInvitation(UserJson user) throws IOException {
        String addresseeName = user.testData().incomeInvitations().getFirst().username();
        var response = userdataSoapClient.declineInvitation(user.username(), addresseeName);
        assertThat(response.getUser().getFriendshipStatus()).isEqualTo(FriendshipStatus.VOID);
    }

    @Test
    @DisplayName("Отправка приглашения дружить")
    @User(incomeInvitations = 1)
    void sendFriendshipInvitation(UserJson user) throws IOException {
        String addresseeName = user.testData().incomeInvitations().getFirst().username();
        userdataSoapClient.declineInvitation(user.username(), addresseeName);
        var response = userdataSoapClient.sendInvitation(user.username(), addresseeName);
        assertThat(response.getUser().getFriendshipStatus()).isEqualTo(FriendshipStatus.INVITE_SENT);
    }
}
