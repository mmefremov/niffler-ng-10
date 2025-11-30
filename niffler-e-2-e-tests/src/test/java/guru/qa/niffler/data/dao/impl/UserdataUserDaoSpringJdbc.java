package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UserdataUserDao;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.extractor.UserdataUserEntityExtractor;
import guru.qa.niffler.data.jdbc.DataSources;
import guru.qa.niffler.data.mapper.UserdataUserEntityRowMapper;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.annotation.Nonnull;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserdataUserDaoSpringJdbc implements UserdataUserDao {

    private static final Config CFG = Config.getInstance();
    private static final String URL = CFG.userdataJdbcUrl();

    @Nonnull
    @Override
    public UserEntity create(UserEntity user) {
        JdbcTemplate template = new JdbcTemplate(DataSources.dataSource(URL));
        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO \"user\" (username, currency, firstname, surname, photo, photo_small, full_name) " +
                    "VALUES (?,?,?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getCurrency().name());
            statement.setString(3, user.getFirstname());
            statement.setString(4, user.getSurname());
            statement.setBytes(5, user.getPhoto());
            statement.setBytes(6, user.getPhotoSmall());
            statement.setString(7, user.getFullname());
            return statement;
        }, keyHolder);

        final UUID generatedKey = (UUID) keyHolder.getKeys().get("id");
        user.setId(generatedKey);
        return user;
    }

    @Nonnull
    @Override
    public Optional<UserEntity> findById(UUID id) {
        JdbcTemplate template = new JdbcTemplate(DataSources.dataSource(URL));
        return Optional.ofNullable(template.query(
                """
                        SELECT u.*, f.*
                        FROM "user" u
                        LEFT JOIN friendship f ON u.id IN (f.addressee_id, f.requester_id)
                        WHERE u.id = ?
                        """,
                UserdataUserEntityExtractor.instance,
                id
        ));
    }

    @Nonnull
    @Override
    public Optional<UserEntity> findByUsername(String username) {
        JdbcTemplate template = new JdbcTemplate(DataSources.dataSource(URL));
        return Optional.ofNullable(template.query(
                """
                        SELECT u.*, f.*
                        FROM "user" u
                        LEFT JOIN friendship f ON u.id IN (f.addressee_id, f.requester_id)
                        WHERE u.username = ?
                        """,
                UserdataUserEntityExtractor.instance,
                username
        ));
    }

    @Nonnull
    @Override
    public List<UserEntity> findAll() {
        JdbcTemplate template = new JdbcTemplate(DataSources.dataSource(URL));
        return template.query(
                "SELECT * FROM \"user\"",
                UserdataUserEntityRowMapper.instance
        );
    }

    @Nonnull
    @Override
    public UserEntity update(UserEntity user) {
        JdbcTemplate template = new JdbcTemplate(DataSources.dataSource(URL));
        template.update("""
                                UPDATE "user"
                                SET username = ?,
                                    currency = ?,
                                    firstname = ?,
                                    surname = ?,
                                    photo = ?,
                                    photo_small = ?,
                                    full_name = ?
                                WHERE id = ?
                                """,
                        user.getCurrency().name(),
                        user.getFirstname(),
                        user.getSurname(),
                        user.getPhoto(),
                        user.getPhotoSmall(),
                        user.getId());
        template.batchUpdate("""
                                     INSERT INTO friendship (requester_id, addressee_id, status) VALUES (?, ?, ?)
                                     ON CONFLICT (requester_id, addressee_id) DO UPDATE SET status = ?
                                     """,
                             new BatchPreparedStatementSetter() {
                                 @Override
                                 public void setValues(PreparedStatement ps, int i) throws SQLException {
                                     ps.setObject(1, user.getId());
                                     ps.setObject(2, user.getFriendshipRequests().get(i).getAddressee().getId());
                                     ps.setString(3, user.getFriendshipRequests().get(i).getStatus().name());
                                     ps.setString(4, user.getFriendshipRequests().get(i).getStatus().name());
                                 }

                                 @Override
                                 public int getBatchSize() {
                                     return user.getFriendshipRequests().size();
                                 }
                             });
        return user;
    }
}
