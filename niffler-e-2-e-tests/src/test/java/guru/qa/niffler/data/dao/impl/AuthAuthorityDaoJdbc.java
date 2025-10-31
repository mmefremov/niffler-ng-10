package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.UUID;

public class AuthAuthorityDaoJdbc implements AuthAuthorityDao {

    private final Connection connection;

    public AuthAuthorityDaoJdbc(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void create(AuthorityEntity... authorities) {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO authority (user_id, authority) VALUES (?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            for (AuthorityEntity authority : authorities) {
                statement.setObject(1, authority.getUser().getId());
                statement.setString(2, authority.getAuthority());
                statement.addBatch();
                statement.clearParameters();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<AuthorityEntity> findById(UUID id) {
        try (PreparedStatement statement = connection.prepareStatement(
                """
                        SELECT * FROM authority
                        JOIN user ON user.id = authority.user_id
                        WHERE authority.id = ?
                        """
        )) {
            statement.setObject(1, id);
            statement.execute();
            try (ResultSet resultSet = statement.getResultSet()) {
                if (resultSet.next()) {
                    AuthorityEntity entity = getAuthorityEntity(resultSet);
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
    public void delete(UUID userId) {
        try (PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM authority WHERE user_id = ?"
        )) {
            statement.setObject(1, userId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private AuthorityEntity getAuthorityEntity(ResultSet resultSet) throws SQLException {
        AuthorityEntity authorityEntity = new AuthorityEntity();
        authorityEntity.setId(resultSet.getObject("id", UUID.class));
        authorityEntity.setAuthority(resultSet.getString("authority"));
        AuthUserEntity userEntity = new AuthUserEntity();
        userEntity.setId(resultSet.getObject("user_id", UUID.class));
        userEntity.setUsername(resultSet.getString("username"));
        userEntity.setPassword(resultSet.getString("password"));
        userEntity.setEnabled(resultSet.getBoolean("enabled"));
        userEntity.setAccountNonExpired(resultSet.getBoolean("account_non_expired"));
        userEntity.setAccountNonLocked(resultSet.getBoolean("account_non_locked"));
        userEntity.setCredentialsNonExpired(resultSet.getBoolean("credentials_non_expired"));
        authorityEntity.setUser(userEntity);
        return authorityEntity;
    }
}
