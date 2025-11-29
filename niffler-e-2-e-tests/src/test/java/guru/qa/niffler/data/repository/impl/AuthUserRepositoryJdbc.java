package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.impl.AuthUserDaoJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jdbc.Connections.holder;

public class AuthUserRepositoryJdbc implements AuthUserRepository {

    private static final Config CFG = Config.getInstance();
    private static final String URL = CFG.authJdbcUrl();
    private static final AuthUserDao authUserDao = new AuthUserDaoJdbc();

    @Override
    public AuthUserEntity create(AuthUserEntity user) {
        return authUserDao.create(user);
    }

    @Override
    public AuthUserEntity update(AuthUserEntity user) {
        try (PreparedStatement statement = holder(URL).connection().prepareStatement(
                """
                        UPDATE "user"
                        SET username = ?,
                            password = ?,
                            enabled = ?,
                            account_non_expired = ?,
                            account_non_locked = ?,
                            credentials_non_expired = ?
                        WHERE id = ?
                        """
        )) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setBoolean(3, user.getEnabled());
            statement.setBoolean(4, user.getAccountNonExpired());
            statement.setBoolean(5, user.getAccountNonLocked());
            statement.setBoolean(6, user.getCredentialsNonExpired());
            statement.setObject(8, user.getId());
            statement.executeUpdate();
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        return authUserDao.findById(id);
    }

    @Override
    public Optional<AuthUserEntity> findByUsername(String username) {
        return authUserDao.findByUsername(username);
    }

    @Override
    public void remove(AuthUserEntity user) {
        try (PreparedStatement authorityStatement = holder(URL).connection().prepareStatement(
                "DELETE FROM authority WHERE user_id = ?");
             PreparedStatement userStatement = holder(URL).connection().prepareStatement(
                     "DELETE FROM \"user\" WHERE id = ?")
        ) {
            authorityStatement.setObject(1, user.getId());
            authorityStatement.executeUpdate();

            userStatement.setObject(1, user.getId());
            userStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
