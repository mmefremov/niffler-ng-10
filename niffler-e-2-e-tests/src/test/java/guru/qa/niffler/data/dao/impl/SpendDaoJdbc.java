package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CurrencyValues;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SpendDaoJdbc implements SpendDao {

    private final Connection connection;

    public SpendDaoJdbc(Connection connection) {
        this.connection = connection;
    }

    @Override
    public SpendEntity create(SpendEntity spend) {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO spend (username, spend_date, currency, amount, description, category_id) " +
                "VALUES ( ?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, spend.getUsername());
            ps.setDate(2, spend.getSpendDate());
            ps.setString(3, spend.getCurrency().name());
            ps.setDouble(4, spend.getAmount());
            ps.setString(5, spend.getDescription());
            ps.setObject(6, spend.getCategory().getId());

            ps.executeUpdate();

            final UUID generatedKey;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Can`t find id in ResultSet");
                }
            }
            spend.setId(generatedKey);
            return spend;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<SpendEntity> findSpendById(UUID id) {
        try (PreparedStatement statement = connection.prepareStatement(
                """
                        SELECT * FROM spend
                        JOIN category ON spend.category_id = category.id
                        WHERE spend.id = ?
                        """
        )) {
            statement.setObject(1, id);
            statement.execute();
            try (ResultSet resultSet = statement.getResultSet()) {
                if (resultSet.next()) {
                    SpendEntity entity = getSpendEntity(resultSet);
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
    public List<SpendEntity> findAllByUsername(String username) {
        try (PreparedStatement statement = connection.prepareStatement(
                """
                        SELECT * FROM spend
                        JOIN category ON spend.category_id = category.id
                        WHERE spend.username = ?
                        """)) {
            statement.setString(1, username);
            statement.execute();
            try (ResultSet resultSet = statement.getResultSet()) {
                List<SpendEntity> entities = new ArrayList<>();
                while (resultSet.next()) {
                    SpendEntity entity = getSpendEntity(resultSet);
                    entities.add(entity);
                }
                return entities;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteSpend(SpendEntity spend) {
        try (PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM spend WHERE id = ?"
        )) {
            statement.setObject(1, spend.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private SpendEntity getSpendEntity(ResultSet resultSet) throws SQLException {
        SpendEntity entity = new SpendEntity();
        entity.setId(resultSet.getObject("id", UUID.class));
        entity.setUsername(resultSet.getString("username"));
        entity.setCurrency(CurrencyValues.valueOf(resultSet.getString("currency")));
        entity.setSpendDate(resultSet.getObject("spend_date", Date.class));
        entity.setAmount(resultSet.getObject("amount", Double.class));
        entity.setDescription(resultSet.getString("description"));
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setId(resultSet.getObject("category_id", UUID.class));
        categoryEntity.setName(resultSet.getString("name"));
        categoryEntity.setUsername(resultSet.getString("username"));
        categoryEntity.setArchived(resultSet.getBoolean("archived"));
        entity.setCategory(categoryEntity);
        return entity;
    }
}
