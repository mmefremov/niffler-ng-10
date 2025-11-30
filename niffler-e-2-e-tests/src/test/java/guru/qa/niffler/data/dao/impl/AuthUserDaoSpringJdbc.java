package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.extractor.AuthUserEntityExtractor;
import guru.qa.niffler.data.jdbc.DataSources;
import guru.qa.niffler.data.mapper.AuthUserEntityRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Nonnull;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AuthUserDaoSpringJdbc implements AuthUserDao {

    private static final Config CFG = Config.getInstance();
    private static final String URL = CFG.authJdbcUrl();

    private static final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @Nonnull
    @Override
    public AuthUserEntity create(AuthUserEntity user) {
        JdbcTemplate template = new JdbcTemplate(DataSources.dataSource(URL));
        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO \"user\" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                    "VALUES (?,?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            statement.setString(1, user.getUsername());
            statement.setString(2, passwordEncoder.encode(user.getPassword()));
            statement.setBoolean(3, user.getEnabled());
            statement.setBoolean(4, user.getCredentialsNonExpired());
            statement.setBoolean(5, user.getAccountNonLocked());
            statement.setBoolean(6, user.getAccountNonExpired());
            return statement;
        }, keyHolder);

        final UUID generatedKey = (UUID) keyHolder.getKeys().get("id");
        user.setId(generatedKey);
        return user;
    }

    @Nonnull
    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        JdbcTemplate template = new JdbcTemplate(DataSources.dataSource(URL));
        return Optional.ofNullable(template.query(
                """
                        SELECT
                            user_id as id,
                            u.username,
                            u.password,
                            u.enabled,
                            u.account_non_expired,
                            u.account_non_locked,
                            u.credentials_non_expired,
                            a.id as authority_id,
                            authority
                        FROM "user" u
                        JOIN authority a ON u.id = a.user_id
                        WHERE u.id = ?
                        """,
                AuthUserEntityExtractor.instance,
                id
        ));
    }

    @Nonnull
    @Override
    public Optional<AuthUserEntity> findByUsername(String username) {
        JdbcTemplate template = new JdbcTemplate(DataSources.dataSource(URL));
        return Optional.ofNullable(template.query(
                """
                        SELECT
                            user_id as id,
                            u.username,
                            u.password,
                            u.enabled,
                            u.account_non_expired,
                            u.account_non_locked,
                            u.credentials_non_expired,
                            a.id as authority_id,
                            authority
                        FROM "user" u
                        JOIN authority a ON u.id = a.user_id
                        WHERE u.username = ?
                        """,
                AuthUserEntityExtractor.instance,
                username
        ));
    }

    @Nonnull
    @Override
    public List<AuthUserEntity> findAll() {
        JdbcTemplate template = new JdbcTemplate(DataSources.dataSource(URL));
        return template.query(
                "SELECT * FROM \"user\"",
                AuthUserEntityRowMapper.instance
        );
    }
}
