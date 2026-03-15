package guru.qa.niffler.service;

import com.google.protobuf.Empty;
import guru.qa.niffler.grpc.CurrencyValues;
import guru.qa.niffler.grpc.CurrentUserRequest;
import guru.qa.niffler.grpc.FriendsRequest;
import guru.qa.niffler.grpc.FriendshipRequest;
import guru.qa.niffler.grpc.FriendshipStatus;
import guru.qa.niffler.grpc.ListUsersRequest;
import guru.qa.niffler.grpc.ListUsersResponse;
import guru.qa.niffler.grpc.NifflerUserdataServiceGrpc;
import guru.qa.niffler.grpc.UserData;
import guru.qa.niffler.model.IUserJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.util.ProtobufBuilder;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.domain.PageRequest;

@GrpcService
@RequiredArgsConstructor
@ExtensionMethod(ProtobufBuilder.class)
public class GrpcUserService extends NifflerUserdataServiceGrpc.NifflerUserdataServiceImplBase {

    private final UserService userService;

    @Override
    public void updateUser(UserData request, StreamObserver<UserData> responseObserver) {
        var user = userService.update(UserJson.fromMessage(request));
        responseObserver.onNext(createFromJson(user));
        responseObserver.onCompleted();
    }

    @Override
    public void currentUser(CurrentUserRequest request, StreamObserver<UserData> responseObserver) {
        var user = userService.getCurrentUser(request.getUsername());
        responseObserver.onNext(createFromJson(user));
        responseObserver.onCompleted();
    }

    @Override
    public void listUsers(ListUsersRequest request, StreamObserver<ListUsersResponse> responseObserver) {
        var usersPage = userService.allUsers(
                request.getUsername(),
                PageRequest.of(request.getPageInfo().getPage(), request.getPageInfo().getSize()),
                request.getSearchQuery()
        );
        var usersList = usersPage.stream()
                .map(this::createFromJson)
                .toList();
        responseObserver.onNext(ListUsersResponse.newBuilder()
                                        .setTotalElements(Math.toIntExact(usersPage.getTotalElements()))
                                        .setTotalPages(usersPage.getTotalPages())
                                        .setFirst(usersPage.isFirst())
                                        .setLast(usersPage.isLast())
                                        .setSize(usersPage.getSize())
                                        .addAllUsers(usersList)
                                        .build());
        responseObserver.onCompleted();
    }

    @Override
    public void sendInvitation(FriendshipRequest request, StreamObserver<UserData> responseObserver) {
        var user = userService.createFriendshipRequest(request.getRequester(), request.getAddressee());
        responseObserver.onNext(createFromJson(user));
        responseObserver.onCompleted();
    }

    public void acceptInvitation(FriendshipRequest request, StreamObserver<UserData> responseObserver) {
        var user = userService.acceptFriendshipRequest(request.getRequester(), request.getAddressee());
        responseObserver.onNext(createFromJson(user));
        responseObserver.onCompleted();
    }

    @Override
    public void declineInvitation(FriendshipRequest request, StreamObserver<UserData> responseObserver) {
        var user = userService.declineFriendshipRequest(request.getRequester(), request.getAddressee());
        responseObserver.onNext(createFromJson(user));
        responseObserver.onCompleted();
    }

    @Override
    public void listFriends(FriendsRequest request, StreamObserver<ListUsersResponse> responseObserver) {
        var usersPage = userService.friends(request.getUsername(),
                                            PageRequest.of(request.getPageInfo().getPage(),
                                                           request.getPageInfo().getSize()),
                                            request.getSearchQuery());
        var usersList = usersPage.stream()
                .map(this::createFromJson)
                .toList();
        responseObserver.onNext(ListUsersResponse.newBuilder()
                                        .setTotalElements(usersPage.getSize())
                                        .setTotalPages((Math.toIntExact(usersPage.getTotalPages())))
                                        .setFirst(usersPage.isFirst())
                                        .setLast(usersPage.isLast())
                                        .setSize(usersPage.getSize())
                                        .addAllUsers(usersList)
                                        .build());
        responseObserver.onCompleted();
    }

    @Override
    public void removeFriend(FriendshipRequest request, StreamObserver<Empty> responseObserver) {
        userService.removeFriend(request.getRequester(), request.getAddressee());
        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    private @Nonnull UserData createFromJson(@Nonnull IUserJson userJson) {
        return UserData.newBuilder()
                .setId(userJson.id().toString())
                .setUsername(userJson.username())
                .setIfNotNull(userJson.firstname(), UserData.Builder::setFirstname)
                .setIfNotNull(userJson.surname(), UserData.Builder::setSurname)
                .setCurrency(CurrencyValues.valueOf(userJson.currency().name()))
                .setIfNotNull(userJson.photo(), UserData.Builder::setPhoto)
                .setIfNotNull(userJson.photoSmall(), UserData.Builder::setPhotoSmall)
                .setEnumIfNotNull(userJson.friendshipStatus(), FriendshipStatus.class, UserData.Builder::setFriendshipStatus)
                .build();
    }
}

