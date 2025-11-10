package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.mapper.UserdataUserEntityResultSetExtractor;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;
import java.util.UUID;

public class UserdataUserRepositorySpringJdbc implements UserdataUserRepository {

    private static final Config CFG = Config.getInstance();
    private static final String URL = CFG.userdataJdbcUrl();

    @Override
    public UserEntity create(UserEntity user) {
        JdbcTemplate template = new JdbcTemplate(DataSources.dataSource(URL));
        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    """
                            INSERT INTO "user" (username, currency, firstname, surname, photo, photo_small, full_name)
                            VALUES ( ?, ?, ?, ?, ?, ?, ?)
                            """,
                    Statement.RETURN_GENERATED_KEYS
            );
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getCurrency().name());
            statement.setString(3, user.getFirstname());
            statement.setString(4, user.getSurname());
            statement.setObject(5, user.getPhoto());
            statement.setObject(6, user.getPhotoSmall());
            statement.setString(7, user.getFullname());
            return statement;
        }, keyHolder);

        UUID generatedKey = (UUID) keyHolder.getKeys().get("id");
        user.setId(generatedKey);
        return user;
    }

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
                UserdataUserEntityResultSetExtractor.instance,
                id
        ));
    }

    @Override
    public void addIncomeInvitation(UserEntity requester, UserEntity addressee) {
        createFriendshipWithStatus(requester, addressee, FriendshipStatus.PENDING);
    }

    @Override
    public void addOutcomeInvitation(UserEntity requester, UserEntity addressee) {
        createFriendshipWithStatus(requester, addressee, FriendshipStatus.PENDING);
    }

    @Override
    public void addFriend(UserEntity requester, UserEntity addressee) {
        createFriendshipWithStatus(requester, addressee, FriendshipStatus.ACCEPTED);
        createFriendshipWithStatus(addressee, requester, FriendshipStatus.ACCEPTED);
    }

    private void createFriendshipWithStatus(UserEntity requester, UserEntity addressee, FriendshipStatus status) {
        JdbcTemplate template = new JdbcTemplate(DataSources.dataSource(URL));
        template.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    """
                            INSERT INTO friendship (requester_id, addressee_id, status)
                            VALUES (?, ?, ?)
                            """
            );
            statement.setObject(1, requester.getId());
            statement.setObject(2, addressee.getId());
            statement.setString(3, status.name());
            return statement;
        });
    }
}
