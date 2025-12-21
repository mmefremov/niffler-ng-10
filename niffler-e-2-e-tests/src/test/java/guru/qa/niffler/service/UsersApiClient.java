package guru.qa.niffler.service;

import com.google.common.base.Stopwatch;
import guru.qa.niffler.api.UserdataApi;
import guru.qa.niffler.jupiter.extension.UserExtension;
import guru.qa.niffler.model.FriendshipStatus;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.utils.RandomDataUtils;
import io.qameta.allure.Step;
import org.eclipse.jetty.http.HttpStatus;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

@ParametersAreNonnullByDefault
public class UsersApiClient extends RestClient implements UsersClient {

    private final UserdataApi userdataApi;
    private final AuthApiClient authApiClient = new AuthApiClient();

    public UsersApiClient() {
        super(CFG.userdataUrl());
        this.userdataApi = create(UserdataApi.class);
    }

    @Nonnull
    @Override
    @Step("Create user '{username}'")
    public UserJson createUser(String username, String password) {
        Response<UserJson> response;
        try {
            Response<Void> authResponse = authApiClient.register(username, password);
            assertThat(authResponse.code()).isEqualTo(HttpStatus.OK_200);

            Stopwatch sw = Stopwatch.createStarted();
            long maxWaitTime = 10_000;

            while (sw.elapsed(TimeUnit.MILLISECONDS) < maxWaitTime) {
                try {
                    UserJson userJson = userdataApi.currentUser(username).execute().body();
                    if (userJson != null && userJson.id() != null) {
                        return userJson;
                    } else {
                        Thread.sleep(100);
                    }
                } catch (IOException e) {
                    // just wait
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        throw new AssertionError("User was not created");
    }

    @Nonnull
    @Override
    @Step("Add income invitation")
    public List<UserJson> addIncomeInvitation(UserJson targetUser, int count) {
        for (int i = 0; i < count; i++) {
            UserJson user = createUser(RandomDataUtils.randomUsername(), UserExtension.DEFAULT_PASSWORD);
            try {
                Response<UserJson> response = userdataApi.sendInvitation(user.username(), targetUser.username()).execute();
                assertThat(response.code()).isEqualTo(HttpStatus.OK_200);
            } catch (IOException e) {
                throw new AssertionError(e);
            }
        }
        Response<List<UserJson>> response;
        try {
            response = userdataApi.friends(targetUser.username(), null).execute();
            assertThat(response.code()).isEqualTo(HttpStatus.OK_200);
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        return requireNonNull(response.body()).stream()
                .filter(user -> user.friendshipStatus() == FriendshipStatus.INVITE_RECEIVED)
                .toList();
    }

    @Nonnull
    @Override
    @Step("Add outcome invitation")
    public List<UserJson> addOutcomeInvitation(UserJson targetUser, int count) {
        List<UserJson> invitations = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            UserJson user = createUser(RandomDataUtils.randomUsername(), UserExtension.DEFAULT_PASSWORD);
            try {
                Response<UserJson> response = userdataApi.sendInvitation(targetUser.username(), user.username()).execute();
                assertThat(response.code()).isEqualTo(HttpStatus.OK_200);
                invitations.add(response.body());
            } catch (IOException e) {
                throw new AssertionError(e);
            }
        }
        return invitations;
    }

    @Nonnull
    @Override
    @Step("Add friend")
    public List<UserJson> addFriend(UserJson targetUser, int count) {
        List<UserJson> friends = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            UserJson user = createUser(RandomDataUtils.randomUsername(), UserExtension.DEFAULT_PASSWORD);
            try {
                Response<UserJson> sendResponse = userdataApi.sendInvitation(user.username(), targetUser.username()).execute();
                assertThat(sendResponse.code()).isEqualTo(HttpStatus.OK_200);
                Response<UserJson> acceptResponse = userdataApi.acceptInvitation(targetUser.username(), user.username()).execute();
                assertThat(acceptResponse.code()).isEqualTo(HttpStatus.OK_200);
                friends.add(acceptResponse.body());
            } catch (IOException e) {
                throw new AssertionError(e);
            }
        }
        return friends;
    }

    @Nonnull
    public List<UserJson> getAll(String username) {
        List<UserJson> resultList = null;
        try {
            Response<List<UserJson>> response = userdataApi.allUsers(username, null).execute();
            assertThat(response.code()).isEqualTo(HttpStatus.OK_200);
            resultList = response.body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return resultList;
    }
}
