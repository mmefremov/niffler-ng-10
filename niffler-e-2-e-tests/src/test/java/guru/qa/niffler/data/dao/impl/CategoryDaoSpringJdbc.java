package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.jdbc.DataSources;
import guru.qa.niffler.data.mapper.CategoryEntityRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.annotation.Nonnull;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CategoryDaoSpringJdbc implements CategoryDao {

    private static final Config CFG = Config.getInstance();
    private static final String URL = CFG.spendJdbcUrl();

    @Nonnull
    @Override
    public CategoryEntity create(CategoryEntity category) {
        JdbcTemplate template = new JdbcTemplate(DataSources.dataSource(URL));
        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO category (username, name, archived) " +
                    "VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, category.getUsername());
            statement.setString(2, category.getName());
            statement.setBoolean(3, category.isArchived());
            return statement;
        }, keyHolder);

        UUID generatedKey = (UUID) keyHolder.getKeys().get("id");
        category.setId(generatedKey);
        return category;
    }

    @Nonnull
    @Override
    public Optional<CategoryEntity> findCategoryById(UUID id) {
        JdbcTemplate template = new JdbcTemplate(DataSources.dataSource(URL));
        return Optional.ofNullable(
                template.queryForObject(
                        "SELECT * FROM category WHERE id = ?",
                        CategoryEntityRowMapper.instance,
                        id
                )
        );
    }

    @Nonnull
    @Override
    public CategoryEntity update(CategoryEntity category) {
        JdbcTemplate template = new JdbcTemplate(DataSources.dataSource(URL));
        template.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE category SET name = ?, username = ?, archived = ? WHERE id = ?");
            statement.setString(1, category.getName());
            statement.setObject(2, category.getUsername());
            statement.setBoolean(3, category.isArchived());
            statement.setObject(4, category.getId());
            return statement;
        });
        return category;
    }

    @Nonnull
    @Override
    public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String categoryName) {
        JdbcTemplate template = new JdbcTemplate(DataSources.dataSource(URL));
        return Optional.ofNullable(
                template.queryForObject(
                        "SELECT * FROM category WHERE username = ? AND name = ?",
                        CategoryEntityRowMapper.instance,
                        username,
                        categoryName
                ));
    }

    @Nonnull
    @Override
    public List<CategoryEntity> findAllByUsername(String username) {
        JdbcTemplate template = new JdbcTemplate(DataSources.dataSource(URL));
        return template.query(
                "SELECT * FROM category WHERE username = ?",
                CategoryEntityRowMapper.instance,
                username
        );
    }

    @Override
    public void deleteCategory(CategoryEntity category) {
        JdbcTemplate template = new JdbcTemplate(DataSources.dataSource(URL));
        template.update("DELETE FROM spend WHERE category_id = ?", category.getId());
        template.update("DELETE FROM category WHERE id = ?", category.getId());
    }

    @Nonnull
    @Override
    public List<CategoryEntity> findAll() {
        JdbcTemplate template = new JdbcTemplate(DataSources.dataSource(URL));
        return template.query(
                "SELECT * FROM category",
                CategoryEntityRowMapper.instance
        );
    }
}
