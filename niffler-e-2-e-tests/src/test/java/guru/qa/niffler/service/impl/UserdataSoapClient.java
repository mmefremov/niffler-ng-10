package guru.qa.niffler.service.impl;

import guru.qa.jaxb.userdata.AcceptInvitationRequest;
import guru.qa.jaxb.userdata.AllUsersRequest;
import guru.qa.jaxb.userdata.CurrentUserRequest;
import guru.qa.jaxb.userdata.DeclineInvitationRequest;
import guru.qa.jaxb.userdata.FriendsPageRequest;
import guru.qa.jaxb.userdata.PageInfo;
import guru.qa.jaxb.userdata.RemoveFriendRequest;
import guru.qa.jaxb.userdata.SendInvitationRequest;
import guru.qa.jaxb.userdata.UserResponse;
import guru.qa.jaxb.userdata.UsersResponse;
import guru.qa.niffler.api.UserdataSoapApi;
import guru.qa.niffler.api.core.converter.SoapConverterFactory;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.service.RestClient;
import io.qameta.allure.Step;
import okhttp3.logging.HttpLoggingInterceptor;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;

@ParametersAreNonnullByDefault
public class UserdataSoapClient extends RestClient {

    private static final Config CFG = Config.getInstance();

    private final UserdataSoapApi userdataSoapApi;

    public UserdataSoapClient() {
        super(CFG.userdataUrl(), false, SoapConverterFactory.create("niffler-userdata"), HttpLoggingInterceptor.Level.BODY);
        this.userdataSoapApi = create(UserdataSoapApi.class);
    }

    @NotNull
    @Step("Get current user info using SOAP")
    public UserResponse currentUser(CurrentUserRequest request) throws IOException {
        return userdataSoapApi.currentUser(request).execute().body();
    }

    @Step("Get all users info using SOAP")
    @Nullable
    public UsersResponse allUsers(AllUsersRequest allUsersRequest) throws IOException {
        return userdataSoapApi.allUsers(allUsersRequest).execute().body();

    }

    @Step("Send invitation using SOAP")
    public UserResponse sendInvitation(String from, String to) throws IOException {
        var request = new SendInvitationRequest();
        request.setUsername(from);
        request.setFriendToBeRequested(to);
        return userdataSoapApi.sendInvitation(request).execute().body();
    }

    @Step("Accept invitation using SOAP")
    public UserResponse acceptInvitation(String username, String friend) throws IOException {
        var request = new AcceptInvitationRequest();
        request.setUsername(username);
        request.setFriendToBeAdded(friend);
        return userdataSoapApi.acceptInvitation(request).execute().body();
    }

    @Step("Decline invitation using SOAP")
    public UserResponse declineInvitation(String username, String friend) throws IOException {
        var request = new DeclineInvitationRequest();
        request.setUsername(username);
        request.setInvitationToBeDeclined(friend);
        return userdataSoapApi.declineInvitation(request).execute().body();
    }

    @Step("Remove friend using SOAP")
    public void removeFriend(String username, String friend) throws IOException {
        var request = new RemoveFriendRequest();
        request.setUsername(username);
        request.setFriendToBeRemoved(friend);
        userdataSoapApi.removeFriend(request).execute();
    }

    public UsersResponse friends(String username) throws IOException {
        return friends(username, 0, 10, null);
    }

    public UsersResponse friends(String username, String searchQuery) throws IOException {
        return friends(username, 0, 10, searchQuery);
    }

    public UsersResponse friends(String username, int page, int size) throws IOException {
        return friends(username, page, size, null);
    }

    @Step("Get friends page using SOAP")
    public UsersResponse friends(String username, int page, int size, @Nullable String searchQuery) throws IOException {
        var request = new FriendsPageRequest();
        request.setUsername(username);

        var pageInfo = new PageInfo();
        pageInfo.setPage(page);
        pageInfo.setSize(size);

        request.setPageInfo(pageInfo);
        request.setSearchQuery(searchQuery);
        return userdataSoapApi.friendsPage(request).execute().body();
    }
}
