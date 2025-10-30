package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AuthUserDaoJdbc implements AuthUserDao {

    private final Connection connection;
    private static final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    public AuthUserDaoJdbc(Connection connection) {
        this.connection = connection;
    }

    @Override
    public AuthUserEntity create(AuthUserEntity user) {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO user (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                "VALUES ( ?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            statement.setString(1, user.getUsername());
            statement.setString(2, passwordEncoder.encode(user.getPassword()));
            statement.setBoolean(3, user.isEnabled());
            statement.setBoolean(4, user.isAccountNonExpired());
            statement.setBoolean(5, user.isAccountNonLocked());
            statement.setBoolean(6, user.isCredentialsNonExpired());
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
        try (PreparedStatement statement = connection.prepareStatement(
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
