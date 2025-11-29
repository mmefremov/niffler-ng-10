package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.mapper.CategoryEntityRowMapper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jdbc.Connections.holder;

public class CategoryDaoJdbc implements CategoryDao {

    private static final Config CFG = Config.getInstance();
    private static final String URL = CFG.spendJdbcUrl();

    @Override
    public CategoryEntity create(CategoryEntity category) {
        try (PreparedStatement ps = holder(URL).connection().prepareStatement(
                "INSERT INTO category (username, name, archived) " +
                "VALUES (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, category.getUsername());
            ps.setString(2, category.getName());
            ps.setBoolean(3, category.isArchived());

            ps.executeUpdate();

            final UUID generatedKey;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Can`t find id in ResultSet");
                }
            }
            category.setId(generatedKey);
            return category;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<CategoryEntity> findCategoryById(UUID id) {
        try (PreparedStatement ps = holder(URL).connection().prepareStatement(
                "SELECT * FROM category WHERE id = ?"
        )) {
            ps.setObject(1, id);
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    CategoryEntity ce = CategoryEntityRowMapper.instance.mapRow(rs, 1);
                    return Optional.of(ce);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CategoryEntity update(CategoryEntity category) {
        try (PreparedStatement statement = holder(URL).connection().prepareStatement(
                "UPDATE category SET name = ?, username = ?, archived = ? WHERE id = ?"
        )) {
            statement.setString(1, category.getName());
            statement.setObject(2, category.getUsername());
            statement.setBoolean(3, category.isArchived());
            statement.setObject(4, category.getId());
            statement.executeUpdate();
            return category;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String categoryName) {
        try (PreparedStatement statement = holder(URL).connection().prepareStatement(
                "SELECT * FROM category WHERE username = ? AND name = ?"
        )) {
            statement.setString(1, username);
            statement.setString(2, categoryName);
            statement.execute();
            try (ResultSet resultSet = statement.getResultSet()) {
                if (resultSet.next()) {
                    CategoryEntity entity = CategoryEntityRowMapper.instance.mapRow(resultSet, 1);
                    return Optional.of(entity);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CategoryEntity> findAllByUsername(String username) {
        try (PreparedStatement statement = holder(URL).connection().prepareStatement(
                "SELECT * FROM category WHERE username = ?"
        )) {
            statement.setString(1, username);
            statement.execute();
            try (ResultSet resultSet = statement.getResultSet()) {
                List<CategoryEntity> entities = new ArrayList<>();
                while (resultSet.next()) {
                    CategoryEntity entity = CategoryEntityRowMapper.instance.mapRow(resultSet, 1);
                    entities.add(entity);
                }
                return entities;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteCategory(CategoryEntity category) {
        try (PreparedStatement spendStatement = holder(URL).connection().prepareStatement(
                "DELETE FROM spend WHERE category_id = ?");
             PreparedStatement userStatement = holder(URL).connection().prepareStatement(
                     "DELETE FROM category WHERE id = ?")
        ) {
            spendStatement.setObject(1, category.getId());
            spendStatement.executeUpdate();

            userStatement.setObject(1, category.getId());
            userStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CategoryEntity> findAll() {
        try (PreparedStatement statement = holder(URL).connection().prepareStatement(
                "SELECT * FROM category"
        )) {
            statement.execute();
            try (ResultSet resultSet = statement.getResultSet()) {
                List<CategoryEntity> entities = new ArrayList<>();
                while (resultSet.next()) {
                    CategoryEntity entity = CategoryEntityRowMapper.instance.mapRow(resultSet, 1);
                    entities.add(entity);
                }
                return entities;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
