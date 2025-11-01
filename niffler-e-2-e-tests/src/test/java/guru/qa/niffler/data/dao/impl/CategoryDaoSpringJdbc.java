package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.mapper.CategoryEntityRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CategoryDaoSpringJdbc implements CategoryDao {

    private final DataSource dataSource;

    public CategoryDaoSpringJdbc(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public CategoryEntity create(CategoryEntity category) {
        JdbcTemplate template = new JdbcTemplate(dataSource);
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

    @Override
    public Optional<CategoryEntity> findCategoryById(UUID id) {
        JdbcTemplate template = new JdbcTemplate(dataSource);
        return Optional.ofNullable(
                template.queryForObject(
                        "SELECT * FROM category WHERE id = ?",
                        CategoryEntityRowMapper.instance,
                        id
                )
        );
    }

    @Override
    public CategoryEntity update(CategoryEntity category) {
        JdbcTemplate template = new JdbcTemplate(dataSource);
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

    @Override
    public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String categoryName) {
        JdbcTemplate template = new JdbcTemplate(dataSource);
        return Optional.ofNullable(
                template.queryForObject(
                        "SELECT * FROM category WHERE username = ? AND name = ?",
                        CategoryEntityRowMapper.instance,
                        username,
                        categoryName
                ));
    }

    @Override
    public List<CategoryEntity> findAllByUsername(String username) {
        JdbcTemplate template = new JdbcTemplate(dataSource);
        return template.query(
                "SELECT * FROM category WHERE username = ?",
                CategoryEntityRowMapper.instance,
                username
        );
    }

    @Override
    public void deleteCategory(CategoryEntity category) {
        JdbcTemplate template = new JdbcTemplate(dataSource);
        template.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM category WHERE id = ?");
            statement.setObject(1, category.getId());
            return statement;
        });
    }

    @Override
    public List<CategoryEntity> findAll() {
        JdbcTemplate template = new JdbcTemplate(dataSource);
        return template.query(
                "SELECT * FROM category",
                CategoryEntityRowMapper.instance
        );
    }
}
