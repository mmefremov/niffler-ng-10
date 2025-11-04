package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UserdataUserDao;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.mapper.UserdataUserEntityRowMapper;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserdataUserDaoSpringJdbc implements UserdataUserDao {

    private static final Config CFG = Config.getInstance();

    @Override
    public UserEntity create(UserEntity user) {
        JdbcTemplate template = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
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

    @Override
    public Optional<UserEntity> findById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        return Optional.ofNullable(
                jdbcTemplate.queryForObject(
                        "SELECT * FROM \"user\" WHERE id = ?",
                        UserdataUserEntityRowMapper.instance,
                        id
                )
        );
    }

    @Override
    public List<UserEntity> findAll() {
        JdbcTemplate template = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        return template.query(
                "SELECT * FROM user",
                UserdataUserEntityRowMapper.instance
        );
    }
}
