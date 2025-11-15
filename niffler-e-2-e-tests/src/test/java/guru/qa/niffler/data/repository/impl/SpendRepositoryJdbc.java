package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.mapper.SpendEntityRowMapper;
import guru.qa.niffler.data.repository.SpendRepository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class SpendRepositoryJdbc implements SpendRepository {

    private static final Config CFG = Config.getInstance();
    private static final String URL = CFG.spendUrl();
    private static final SpendDao spendDao = new SpendDaoJdbc();
    private static final CategoryDao categoryDao = new CategoryDaoJdbc();

    @Override
    public SpendEntity create(SpendEntity spend) {
        return spendDao.create(spend);
    }

    @Override
    public SpendEntity update(SpendEntity spend) {
        try (PreparedStatement statement = holder(URL).connection().prepareStatement(
                """
                        UPDATE spend
                        SET username = ?,
                            spend_date = ?,
                            currency = ?,
                            amount = ?,
                            description = ?,
                            category_id = ?
                        WHERE id = ?
                        """)
        ) {
            statement.setString(1, spend.getUsername());
            statement.setDate(2, new Date(spend.getSpendDate().getTime()));
            statement.setString(3, spend.getCurrency().name());
            statement.setDouble(4, spend.getAmount());
            statement.setString(5, spend.getDescription());
            statement.setObject(6, spend.getCategory().getId());
            statement.setObject(7, spend.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return spend;
    }

    @Override
    public CategoryEntity updateCategory(CategoryEntity category) {
        try (PreparedStatement statement = holder(URL).connection().prepareStatement(
                """
                        UPDATE category
                        SET name = ?,
                            username = ?,
                            archived = ?
                        WHERE id = ?
                        """)
        ) {
            statement.setString(1, category.getName());
            statement.setString(2, category.getUsername());
            statement.setBoolean(3, category.isArchived());
            statement.setObject(4, category.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return category;
    }

    @Override
    public CategoryEntity createCategory(CategoryEntity category) {
        return categoryDao.create(category);
    }

    @Override
    public Optional<CategoryEntity> findCategoryById(UUID id) {
        return categoryDao.findCategoryById(id);
    }

    @Override
    public Optional<CategoryEntity> findCategoryByUsernameAndSpendName(String username, String name) {
        return categoryDao.findCategoryByUsernameAndCategoryName(username, name);
    }

    @Override
    public Optional<SpendEntity> findById(UUID id) {
        return spendDao.findSpendById(id);
    }

    @Override
    public Optional<SpendEntity> findByUsernameAndSpendDescription(String username, String description) {
        try (PreparedStatement statement = holder(URL).connection().prepareStatement(
                "SELECT * FROM spend WHERE username = ? and description = ?")
        ) {
            statement.setObject(1, username);
            statement.setObject(2, description);
            statement.executeQuery();

            try (ResultSet resultSet = statement.getResultSet()) {
                SpendEntity spend = SpendEntityRowMapper.instance.mapRow(resultSet, 1);
                return Optional.ofNullable(spend);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(SpendEntity spend) {
        spendDao.delete(spend);
    }

    @Override
    public void removeCategory(CategoryEntity spend) {
        categoryDao.delete(spend);
    }
}
