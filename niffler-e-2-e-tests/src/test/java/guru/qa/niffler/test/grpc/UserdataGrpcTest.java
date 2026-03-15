package guru.qa.niffler.test.grpc;

import guru.qa.niffler.grpc.FriendsRequest;
import guru.qa.niffler.grpc.FriendshipRequest;
import guru.qa.niffler.grpc.FriendshipStatus;
import guru.qa.niffler.grpc.PageInfo;
import guru.qa.niffler.grpc.UserData;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserdataGrpcTest extends BaseGrpcTest {

    private final PageInfo defaultPageInfo = PageInfo.newBuilder().setSize(10).build();

    @Test
    @DisplayName("Список друзей получен в виде Page при передаче параметров page, size")
    @User(friends = 3)
    void friendListShouldBePageable(UserJson user) {
        var listUsersResponse = USERDATA_BLOCKING_STUB.listFriends(FriendsRequest.newBuilder()
                                                                           .setPageInfo(defaultPageInfo)
                                                                           .setUsername(user.username())
                                                                           .build());
        assertThat(listUsersResponse.getTotalElements()).isEqualTo(defaultPageInfo.getSize());
        assertThat(listUsersResponse.getUsersList()).extracting(UserData::getFriendshipStatus)
                .allMatch(status -> status == FriendshipStatus.FRIEND);
    }

    @Test
    @DisplayName("список друзей с фильтраций по username")
    @User(friends = 1)
    void friendListFilteredBySearchQuery(UserJson user) {
        String friendName = user.testData().friends().getFirst().username();
        var listUsersResponse = USERDATA_BLOCKING_STUB.listFriends(FriendsRequest.newBuilder()
                                                                           .setPageInfo(defaultPageInfo)
                                                                           .setUsername(user.username())
                                                                           .setSearchQuery(friendName)
                                                                           .build());
        assertThat(listUsersResponse.getUsersCount()).isOne();
        assertThat(listUsersResponse.getUsers(0)).matches(
                friend -> friend.getUsername().equals(friendName) && friend.getFriendshipStatus() == FriendshipStatus.FRIEND
        );
    }

    @Test
    @DisplayName("Дружба должна удаляться")
    @User(friends = 1)
    void removeFriendship(UserJson user) {
        String friendName = user.testData().friends().getFirst().username();
        USERDATA_BLOCKING_STUB.removeFriend(FriendshipRequest.newBuilder()
                                                    .setRequester(user.username())
                                                    .setAddressee(friendName)
                                                    .build());
        var listUsersResponse = USERDATA_BLOCKING_STUB.listFriends(FriendsRequest.newBuilder()
                                                                           .setPageInfo(defaultPageInfo)
                                                                           .setUsername(user.username())
                                                                           .build());
        assertThat(listUsersResponse.getUsersCount()).isZero();
    }

    @Test
    @DisplayName("Прием заявки в друзья")
    @User(incomeInvitations = 1)
    void acceptIncomeInvitation(UserJson user) {
        String addresseeName = user.testData().incomeInvitations().getFirst().username();
        var userData = USERDATA_BLOCKING_STUB.acceptInvitation(FriendshipRequest.newBuilder()
                                                                       .setRequester(user.username())
                                                                       .setAddressee(addresseeName)
                                                                       .build());
        assertThat(userData.getFriendshipStatus()).isEqualTo(FriendshipStatus.FRIEND);
    }

    @Test
    @DisplayName("Отклонение заявки в друзья")
    @User(incomeInvitations = 1)
    void rejectIncomeInvitation(UserJson user) {
        String addresseeName = user.testData().incomeInvitations().getFirst().username();
        var userData = USERDATA_BLOCKING_STUB.declineInvitation(
                FriendshipRequest.newBuilder()
                        .setRequester(user.username())
                        .setAddressee(addresseeName)
                        .build());
        assertThat(userData.getFriendshipStatus()).isEqualTo(FriendshipStatus.FRIENDSHIP_STATUS_UNSPECIFIED);
    }

    @Test
    @DisplayName("Отправка приглашения дружить")
    @User(incomeInvitations = 1)
    void sendFriendshipInvitation(UserJson user) {
        String addresseeName = user.testData().incomeInvitations().getFirst().username();
        USERDATA_BLOCKING_STUB.declineInvitation(FriendshipRequest.newBuilder()
                                                         .setRequester(user.username())
                                                         .setAddressee(addresseeName)
                                                         .build());
        var userData = USERDATA_BLOCKING_STUB.sendInvitation(FriendshipRequest.newBuilder()
                                                                     .setRequester(user.username())
                                                                     .setAddressee(addresseeName)
                                                                     .build());
        assertThat(userData.getFriendshipStatus()).isEqualTo(FriendshipStatus.INVITE_SENT);
    }
}
