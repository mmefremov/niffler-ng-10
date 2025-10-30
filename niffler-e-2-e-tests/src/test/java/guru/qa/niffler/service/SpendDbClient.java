package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;

import static guru.qa.niffler.data.Databases.transaction;

public class SpendDbClient implements SpendClient {

    private static final Config CFG = Config.getInstance();

    @Override
    public SpendJson createSpend(SpendJson spend) {
        return transaction(connection -> {
                               SpendEntity spendEntity = SpendEntity.fromJson(spend);
                               CategoryEntity category = spendEntity.getCategory();
                               if (category.getId() == null) {
                                   CategoryDao categoryDaoJdbc = new CategoryDaoJdbc(connection);
                                   spendEntity.setCategory(
                                           categoryDaoJdbc
                                                   .findCategoryByUsernameAndCategoryName(category.getUsername(), category.getName())
                                                   .orElseGet(() -> categoryDaoJdbc.create(category))
                                   );
                               }
                               return SpendJson.fromEntity(
                                       new SpendDaoJdbc(connection).create(spendEntity)
                               );
                           },
                           CFG.spendJdbcUrl()
        );
    }

    @Override
    public CategoryJson createCategory(CategoryJson category) {
        return transaction(connection -> {
                               return CategoryJson.fromEntity(
                                       new CategoryDaoJdbc(connection).create(
                                               CategoryEntity.fromJson(category)
                                       )
                               );
                           },
                           CFG.spendJdbcUrl()
        );
    }

    @Override
    public CategoryJson updateCategory(CategoryJson category) {
        return transaction(connection -> {
                               return CategoryJson.fromEntity(
                                       new CategoryDaoJdbc(connection).update(
                                               CategoryEntity.fromJson(category)
                                       )
                               );
                           },
                           CFG.spendJdbcUrl()
        );
    }
}
