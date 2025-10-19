package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
import guru.qa.niffler.data.dao.UserDao;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.model.CurrencyValues;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.UUID;

public class UserDaoJdbc implements UserDao {

    private static final Config CFG = Config.getInstance();

    @Override
    public UserEntity createUser(UserEntity user) {
        try (Connection connection = Databases.connection(CFG.userdataJdbcUrl())) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO user (username, currency, firstname, surname, photo, photo_small, full_name) " +
                    "VALUES ( ?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                statement.setString(1, user.getUsername());
                statement.setString(2, user.getCurrency().name());
                statement.setString(3, user.getFirstName());
                statement.setString(4, user.getSurname());
                statement.setObject(5, user.getPhoto());
                statement.setObject(6, user.getPhotoSmall());
                statement.setString(7, user.getFullName());
                statement.executeUpdate();

                UUID generatedKey;
                try (ResultSet resultSet = statement.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        generatedKey = resultSet.getObject("id", UUID.class);
                    } else {
                        throw new SQLException("Can`t find id in ResultSet");
                    }
                }
                user.setId(generatedKey);
                return user;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<UserEntity> findById(UUID id) {
        try (Connection connection = Databases.connection(CFG.userdataJdbcUrl());
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT * FROM user WHERE id = ?"
             )) {
            statement.setObject(1, id);
            statement.execute();
            try (ResultSet resultSet = statement.getResultSet()) {
                if (resultSet.next()) {
                    UserEntity entity = getUserEntity(resultSet);
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
    public Optional<UserEntity> findByUsername(String username) {
        try (Connection connection = Databases.connection(CFG.userdataJdbcUrl());
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT * FROM user WHERE username = ?"
             )) {
            statement.setObject(1, username);
            statement.execute();
            try (ResultSet resultSet = statement.getResultSet()) {
                if (resultSet.next()) {
                    UserEntity entity = getUserEntity(resultSet);
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
    public void delete(UserEntity user) {
        try (Connection connection = Databases.connection(CFG.userdataJdbcUrl());
             PreparedStatement statement = connection.prepareStatement(
                     "DELETE FROM user WHERE id = ?"
             )) {
            statement.setObject(1, user.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private UserEntity getUserEntity(ResultSet resultSet) throws SQLException {
        UserEntity entity = new UserEntity();
        entity.setId(resultSet.getObject("id", UUID.class));
        entity.setCurrency(resultSet.getObject("currency", CurrencyValues.class));
        entity.setUsername(resultSet.getString("username"));
        entity.setFirstName(resultSet.getString("firstname"));
        entity.setSurname(resultSet.getString("surname"));
        entity.setPhoto(resultSet.getBytes("photo"));
        entity.setPhotoSmall(resultSet.getBytes("photo_small"));
        entity.setFullName(resultSet.getString("full_name"));
        return entity;
    }
}
