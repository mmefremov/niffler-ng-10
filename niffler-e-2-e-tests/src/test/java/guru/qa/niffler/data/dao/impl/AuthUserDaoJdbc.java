package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class AuthUserDaoJdbc implements AuthUserDao {

    private static final Config CFG = Config.getInstance();

    private static final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @Override
    public AuthUserEntity create(AuthUserEntity user) {
        try (PreparedStatement statement = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "INSERT INTO user (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                "VALUES ( ?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            statement.setString(1, user.getUsername());
            statement.setString(2, passwordEncoder.encode(user.getPassword()));
            statement.setBoolean(3, user.getEnabled());
            statement.setBoolean(4, user.getAccountNonExpired());
            statement.setBoolean(5, user.getAccountNonLocked());
            statement.setBoolean(6, user.getCredentialsNonExpired());
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

    @Override
    public List<AuthUserEntity> findAll() {
        try (PreparedStatement statement = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM user"
        )) {
            statement.execute();
            try (ResultSet resultSet = statement.getResultSet()) {
                List<AuthUserEntity> entities = new ArrayList<>();
                while (resultSet.next()) {
                    AuthUserEntity entity = new AuthUserEntity();
                    entity.setId(resultSet.getObject("id", UUID.class));
                    entity.setUsername(resultSet.getString("username"));
                    entity.setPassword(resultSet.getString("password"));
                    entity.setEnabled(resultSet.getBoolean("enabled"));
                    entity.setAccountNonExpired(resultSet.getBoolean("account_non_expired"));
                    entity.setAccountNonLocked(resultSet.getBoolean("account_non_locked"));
                    entity.setCredentialsNonExpired(resultSet.getBoolean("credentials_non_expired"));
                    entities.add(entity);
                }
                return entities;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
