package guru.qa.niffler.service.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.SpendClient;
import io.qameta.allure.Step;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
public final class SpendDbClient implements SpendClient {

    private static final Config CFG = Config.getInstance();

    private final SpendRepository spendRepository = SpendRepository.getInstance();

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.spendJdbcUrl()
    );

    @Nonnull
    @Override
    @Step("Create spend")
    public SpendJson createSpend(SpendJson spend) {
        return requireNonNull(
                xaTransactionTemplate.execute(
                        () -> SpendJson.fromEntity(
                                spendRepository.create(
                                        SpendEntity.fromJson(spend)
                                )
                        )
                )
        );
    }

    @Nonnull
    @Override
    public SpendJson editSpend(SpendJson spend) {
        return requireNonNull(
                xaTransactionTemplate.execute(
                        () -> SpendJson.fromEntity(
                                spendRepository.update(
                                        SpendEntity.fromJson(spend)
                                )
                        )
                )
        );
    }

    @Nullable
    @Override
    public SpendJson getSpend(String id) {
        return spendRepository.findById(UUID.fromString(id))
                .map(SpendJson::fromEntity)
                .orElse(null);
    }

    @Nullable
    @Override
    public void removeSpends(String username, List<String> ids) {
        xaTransactionTemplate.execute(
                () -> {
                    for (String id : ids) {
                        Optional<SpendEntity> spend = spendRepository.findById(UUID.fromString(id));
                        spend.ifPresent(spendRepository::remove);
                    }
                    return null;
                }
        );
    }

    @Nonnull
    @Override
    @Step("Create category")
    public CategoryJson createCategory(CategoryJson category) {
        return requireNonNull(
                xaTransactionTemplate.execute(
                        () -> CategoryJson.fromEntity(
                                spendRepository.createCategory(
                                        CategoryEntity.fromJson(category)
                                )
                        )
                )
        );
    }

    @Nonnull
    @Override
    @Step("Update category")
    public CategoryJson updateCategory(CategoryJson category) {
        return requireNonNull(
                xaTransactionTemplate.execute(
                        () -> CategoryJson.fromEntity(
                                spendRepository.updateCategory(
                                        CategoryEntity.fromJson(category)
                                )
                        )
                )
        );
    }
}
