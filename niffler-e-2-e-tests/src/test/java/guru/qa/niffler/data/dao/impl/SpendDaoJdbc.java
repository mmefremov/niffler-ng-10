package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.mapper.SpendEntityRowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class SpendDaoJdbc implements SpendDao {

    private static final Config CFG = Config.getInstance();

    @Override
    public SpendEntity create(SpendEntity spend) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "INSERT INTO spend (username, spend_date, currency, amount, description, category_id) " +
                "VALUES ( ?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, spend.getUsername());
            ps.setDate(2, new java.sql.Date(spend.getSpendDate().getTime()));
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
        try (PreparedStatement statement = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
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
                    SpendEntity entity = SpendEntityRowMapper.instance.mapRow(resultSet, 1);
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
    public SpendEntity update(SpendEntity spend) {
        try (PreparedStatement statement = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
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
    public List<SpendEntity> findAllByUsername(String username) {
        try (PreparedStatement statement = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
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
                    SpendEntity entity = SpendEntityRowMapper.instance.mapRow(resultSet, 1);
                    entities.add(entity);
                }
                return entities;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(SpendEntity spend) {
        try (PreparedStatement statement = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "DELETE FROM spend WHERE id = ?"
        )) {
            statement.setObject(1, spend.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<SpendEntity> findAll() {
        try (PreparedStatement statement = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM spend"
        )) {
            statement.execute();
            try (ResultSet resultSet = statement.getResultSet()) {
                List<SpendEntity> entities = new ArrayList<>();
                while (resultSet.next()) {
                    SpendEntity entity = SpendEntityRowMapper.instance.mapRow(resultSet, 1);
                    entities.add(entity);
                }
                return entities;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
