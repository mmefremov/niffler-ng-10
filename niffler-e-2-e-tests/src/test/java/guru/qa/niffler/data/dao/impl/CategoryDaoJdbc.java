package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CategoryDaoJdbc implements CategoryDao {

    private final Connection connection;

    public CategoryDaoJdbc(Connection connection) {
        this.connection = connection;
    }

    @Override
    public CategoryEntity create(CategoryEntity category) {
        try (PreparedStatement ps = connection.prepareStatement(
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
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM category WHERE id = ?"
        )) {
            ps.setObject(1, id);
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    CategoryEntity ce = new CategoryEntity();
                    ce.setId(rs.getObject("id", UUID.class));
                    ce.setUsername(rs.getString("username"));
                    ce.setName(rs.getString("name"));
                    ce.setArchived(rs.getBoolean("archived"));
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
    public CategoryEntity update(CategoryEntity categoryEntity) {
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE category SET name = ?, username = ?, archived = ? WHERE id = ?"
        )) {
            statement.setString(1, categoryEntity.getName());
            statement.setObject(2, categoryEntity.getUsername());
            statement.setBoolean(3, categoryEntity.isArchived());
            statement.setObject(4, categoryEntity.getId());
            statement.executeUpdate();
            return categoryEntity;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String categoryName) {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM category WHERE username = ? AND name = ?"
        )) {
            statement.setString(1, username);
            statement.setString(2, categoryName);
            statement.execute();
            try (ResultSet resultSet = statement.getResultSet()) {
                if (resultSet.next()) {
                    CategoryEntity entity = getCategoryEntity(resultSet);
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
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM category WHERE username = ?"
        )) {
            statement.setString(1, username);
            statement.execute();
            try (ResultSet resultSet = statement.getResultSet()) {
                List<CategoryEntity> entities = new ArrayList<>();
                while (resultSet.next()) {
                    CategoryEntity entity = getCategoryEntity(resultSet);
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
        try (PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM category WHERE id = ?"
        )) {
            statement.setObject(1, category.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CategoryEntity> findAll() {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM category"
        )) {
            statement.execute();
            try (ResultSet resultSet = statement.getResultSet()) {
                List<CategoryEntity> entities = new ArrayList<>();
                while (resultSet.next()) {
                    CategoryEntity entity = getCategoryEntity(resultSet);
                    entities.add(entity);
                }
                return entities;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private CategoryEntity getCategoryEntity(ResultSet resultSet) throws SQLException {
        CategoryEntity entity = new CategoryEntity();
        entity.setId(resultSet.getObject("id", UUID.class));
        entity.setName(resultSet.getString("name"));
        entity.setUsername(resultSet.getString("username"));
        entity.setArchived(resultSet.getBoolean("archived"));
        return entity;
    }
}
