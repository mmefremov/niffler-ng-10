package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UserdataUserDao;
import guru.qa.niffler.data.entity.userdata.FriendshipEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.mapper.UserdataUserEntityRowMapper;

import javax.annotation.Nonnull;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jdbc.Connections.holder;

public class UserdataUserDaoJdbc implements UserdataUserDao {

    private static final Config CFG = Config.getInstance();
    private static final String URL = CFG.userdataJdbcUrl();

    @Nonnull
    @Override
    @SuppressWarnings("resource")
    public UserEntity create(UserEntity user) {
        try (PreparedStatement statement = holder(URL).connection().prepareStatement(
                "INSERT INTO user (username, currency, firstname, surname, photo, photo_small, full_name) " +
                "VALUES ( ?, ?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getCurrency().name());
            statement.setString(3, user.getFirstname());
            statement.setString(4, user.getSurname());
            statement.setObject(5, user.getPhoto());
            statement.setObject(6, user.getPhotoSmall());
            statement.setString(7, user.getFullname());
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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Nonnull
    @Override
    @SuppressWarnings("resource")
    public Optional<UserEntity> findById(UUID id) {
        try (PreparedStatement statement = holder(URL).connection().prepareStatement(
                "SELECT * FROM \"user\" WHERE id = ?"
        )) {
            statement.setObject(1, id);
            statement.execute();
            try (ResultSet resultSet = statement.getResultSet()) {
                if (resultSet.next()) {
                    UserEntity entity = UserdataUserEntityRowMapper.instance.mapRow(resultSet, 1);
                    return Optional.ofNullable(entity);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Nonnull
    @Override
    @SuppressWarnings("resource")
    public Optional<UserEntity> findByUsername(String username) {
        try (PreparedStatement statement = holder(URL).connection().prepareStatement(
                "SELECT * FROM \"user\" WHERE username = ?"
        )) {
            statement.setObject(1, username);
            statement.execute();
            try (ResultSet resultSet = statement.getResultSet()) {
                if (resultSet.next()) {
                    UserEntity entity = UserdataUserEntityRowMapper.instance.mapRow(resultSet, 1);
                    return Optional.ofNullable(entity);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Nonnull
    @Override
    @SuppressWarnings("resource")
    public List<UserEntity> findAll() {
        try (PreparedStatement statement = holder(URL).connection().prepareStatement(
                "SELECT * FROM \"user\""
        )) {
            statement.execute();
            try (ResultSet resultSet = statement.getResultSet()) {
                List<UserEntity> entities = new ArrayList<>();
                while (resultSet.next()) {
                    UserEntity entity = UserdataUserEntityRowMapper.instance.mapRow(resultSet, 1);
                    entities.add(entity);
                }
                return entities;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Nonnull
    @Override
    @SuppressWarnings("resource")
    public UserEntity update(UserEntity user) {
        try (PreparedStatement userStatement = holder(URL).connection().prepareStatement(
                """
                        UPDATE "user"
                        SET username = ?,
                            currency = ?,
                            firstname = ?,
                            surname = ?,
                            photo = ?,
                            photo_small = ?,
                            full_name = ?
                        WHERE id = ?
                        """);
             PreparedStatement friendsStatement = holder(URL).connection().prepareStatement(
                     """
                             INSERT INTO friendship (requester_id, addressee_id, status)
                             VALUES (?, ?, ?)
                             ON CONFLICT (requester_id, addressee_id)
                                 DO UPDATE SET status = ?
                             """)
        ) {
            userStatement.setString(1, user.getUsername());
            userStatement.setString(2, user.getCurrency().name());
            userStatement.setString(3, user.getFirstname());
            userStatement.setString(4, user.getSurname());
            userStatement.setBytes(5, user.getPhoto());
            userStatement.setBytes(6, user.getPhotoSmall());
            userStatement.setString(7, user.getFullname());
            userStatement.setObject(8, user.getId());
            userStatement.executeUpdate();

            for (FriendshipEntity friendship : user.getFriendshipRequests()) {
                friendsStatement.setObject(1, user.getId());
                friendsStatement.setObject(2, friendship.getAddressee().getId());
                friendsStatement.setString(3, friendship.getStatus().name());
                friendsStatement.setString(4, friendship.getStatus().name());
                friendsStatement.addBatch();
                friendsStatement.clearParameters();
            }
            friendsStatement.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
    }
}
