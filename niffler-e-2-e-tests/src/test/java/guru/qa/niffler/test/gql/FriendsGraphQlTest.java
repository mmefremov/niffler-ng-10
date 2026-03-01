package guru.qa.niffler.test.gql;

import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.Friends2SubQueriesQuery;
import guru.qa.FriendsWithCategoriesQuery;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FriendsGraphQlTest extends BaseGraphQlTest {

    @Test
    @User(friends = 1)
    @ApiLogin
    void friendCategoriesShouldNotBeAvailableTest(@Token String bearerToken) {
        var call = apolloClient.query(new FriendsWithCategoriesQuery())
                .addHttpHeader("authorization", bearerToken);

        var response = Rx2Apollo.single(call).blockingGet();
        var errors = response.errors;
        assertThat(errors).as("errors").hasSize(1);
        String actualMessage = errors.getFirst().getMessage();
        assertThat(actualMessage).as("message").isEqualTo("Can`t query categories for another user");
    }

    @Test
    @User
    @ApiLogin
    void friendSubQueriesShouldNotFetchFriendsOfFriendsTest(@Token String bearerToken) {
        var call = apolloClient.query(new Friends2SubQueriesQuery())
                .addHttpHeader("authorization", bearerToken);

        var response = Rx2Apollo.single(call).blockingGet();
        var errors = response.errors;
        assertThat(errors).as("errors").hasSize(1);
        String actualMessage = errors.getFirst().getMessage();
        assertThat(actualMessage).as("message").isEqualTo("Can`t fetch sub-queries for friends of user friends");
    }
}
