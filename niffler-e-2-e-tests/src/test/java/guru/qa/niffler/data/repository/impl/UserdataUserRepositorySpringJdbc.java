package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UserdataUserDao;
import guru.qa.niffler.data.dao.impl.UserdataUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.util.Optional;
import java.util.UUID;

public class UserdataUserRepositorySpringJdbc implements UserdataUserRepository {

    private static final Config CFG = Config.getInstance();
    private static final String URL = CFG.userdataJdbcUrl();
    private static final UserdataUserDao userDao = new UserdataUserDaoSpringJdbc();

    @Override
    public UserEntity create(UserEntity user) {
        return userDao.create(user);
    }

    @Override
    public Optional<UserEntity> findById(UUID id) {
        return userDao.findById(id);
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Override
    public UserEntity update(UserEntity user) {
        return userDao.update(user);
    }

    @Override
    public void sendInvitation(UserEntity requester, UserEntity addressee) {
        createFriendshipWithStatus(requester, addressee, FriendshipStatus.PENDING);
    }

    @Override
    public void addFriend(UserEntity requester, UserEntity addressee) {
        createFriendshipWithStatus(requester, addressee, FriendshipStatus.ACCEPTED);
        createFriendshipWithStatus(addressee, requester, FriendshipStatus.ACCEPTED);
    }

    @Override
    public void remove(UserEntity user) {
        JdbcTemplate template = new JdbcTemplate(DataSources.dataSource(URL));
        UUID userId = user.getId();
        template.update("DELETE FROM friendship WHERE requester_id = ? or addressee_id = ?", userId, userId);
        template.update("DELETE FROM \"user\" WHERE id = ?", userId);
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
