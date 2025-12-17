package guru.qa.niffler.service;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.niffler.api.GhApi;
import io.qameta.allure.Step;
import lombok.SneakyThrows;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
public final class GhApiClient extends RestClient {

    private static final String GH_TOKEN_ENV = "GITHUB_TOKEN";

    private final GhApi ghApi;

    public GhApiClient() {
        super(CFG.githubUrl());
        this.ghApi = create(GhApi.class);
    }

    @SneakyThrows
    @Nonnull
    @Step("Get issue state '{issueNumber}'")
    public String issueState(String issueNumber) {
        JsonNode responseBody = ghApi.issue(
                "Bearer " + System.getenv(GH_TOKEN_ENV),
                issueNumber
        ).execute().body();
        return requireNonNull(responseBody).get("state").asText();
    }
}
