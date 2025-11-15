package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoSpringJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.mapper.SpendEntityResultSetExtractor;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.Optional;
import java.util.UUID;

public class SpendRepositorySpringJdbc implements SpendRepository {

    private static final Config CFG = Config.getInstance();
    private static final String URL = CFG.spendUrl();
    private static final SpendDao spendDao = new SpendDaoSpringJdbc();
    private static final CategoryDao categoryDao = new CategoryDaoSpringJdbc();

    @Override
    public SpendEntity create(SpendEntity spend) {
        return spendDao.create(spend);
    }

    @Override
    public SpendEntity update(SpendEntity spend) {
        JdbcTemplate template = new JdbcTemplate(DataSources.dataSource(URL));
        template.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    """
                            UPDATE spend
                            SET username = ?,
                                spend_date = ?,
                                currency = ?,
                                amount = ?,
                                description = ?,
                                category_id = ?
                            WHERE id = ?
                            """);
            statement.setString(1, spend.getUsername());
            statement.setDate(2, new Date(spend.getSpendDate().getTime()));
            statement.setString(3, spend.getCurrency().name());
            statement.setDouble(4, spend.getAmount());
            statement.setString(5, spend.getDescription());
            statement.setObject(6, spend.getCategory().getId());
            statement.setObject(7, spend.getId());
            return statement;
        });
        return spend;
    }

    @Override
    public CategoryEntity updateCategory(CategoryEntity category) {
        return categoryDao.update(category);
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
        JdbcTemplate template = new JdbcTemplate(DataSources.dataSource(URL));
        return Optional.ofNullable(template.query(
                "SELECT * FROM spend WHERE username = ? AND description = ?",
                SpendEntityResultSetExtractor.instance,
                username,
                description)
        );
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
