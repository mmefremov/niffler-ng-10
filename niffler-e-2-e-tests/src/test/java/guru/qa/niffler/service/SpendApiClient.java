package guru.qa.niffler.service;

import guru.qa.niffler.api.SpendApi;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import io.qameta.allure.Step;
import org.eclipse.jetty.http.HttpStatus;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;

@ParametersAreNonnullByDefault
public final class SpendApiClient extends RestClient implements SpendClient {

    private final SpendApi spendApi;

    public SpendApiClient() {
        super(CFG.spendUrl());
        this.spendApi = create(SpendApi.class);
    }

    @Nonnull
    @Override
    @Step("Create spend")
    public SpendJson createSpend(SpendJson spend) {
        final Response<SpendJson> response;
        try {
            response = spendApi.createSpend(spend)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertThat(response.code()).isEqualTo(HttpStatus.CREATED_201);
        return requireNonNull(response.body());
    }

    @Nonnull
    @Step("Edit spend")
    public SpendJson editSpend(SpendJson spend) {
        final Response<SpendJson> response;
        try {
            response = spendApi.editSpend(spend)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertThat(response.code()).isEqualTo(HttpStatus.OK_200);
        return requireNonNull(response.body());
    }

    @Nonnull
    @Step("Get spend")
    public SpendJson getSpend(String id) {
        final Response<SpendJson> response;
        try {
            response = spendApi.getSpend(id)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertThat(response.code()).isEqualTo(HttpStatus.OK_200);
        return requireNonNull(response.body());
    }

    @Nonnull
    @Step("Get all spends for '{username}'")
    public List<SpendJson> allSpends(String username,
                                     @Nullable CurrencyValues currency,
                                     @Nullable String from,
                                     @Nullable String to) {
        final Response<List<SpendJson>> response;
        try {
            response = spendApi.allSpends(username, currency, from, to)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertThat(response.code()).isEqualTo(HttpStatus.OK_200);
        return response.body() != null
                ? response.body()
                : Collections.emptyList();
    }

    @Step("Remove spends from '{username}'")
    public void removeSpends(String username, List<String> ids) {
        final Response<Void> response;
        try {
            response = spendApi.removeSpends(username, ids)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertThat(response.code()).isEqualTo(HttpStatus.ACCEPTED_202);
    }

    @Nonnull
    @Override
    @Step("Create category")
    public CategoryJson createCategory(CategoryJson category) {
        final Response<CategoryJson> response;
        try {
            response = spendApi.addCategory(category)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertThat(response.code()).isEqualTo(HttpStatus.OK_200);
        return requireNonNull(response.body());
    }

    @Nonnull
    @Override
    @Step("Update category")
    public CategoryJson updateCategory(CategoryJson category) {
        final Response<CategoryJson> response;
        try {
            response = spendApi.updateCategory(category)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertThat(response.code()).isEqualTo(HttpStatus.OK_200);
        return requireNonNull(response.body());
    }

    @Nonnull
    @Step("Get all categories for '{username}'")
    public List<CategoryJson> allCategories(String username) {
        final Response<List<CategoryJson>> response;
        try {
            response = spendApi.allCategories(username)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertThat(response.code()).isEqualTo(HttpStatus.OK_200);
        return response.body() != null
                ? response.body()
                : Collections.emptyList();
    }
}
