package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UserdataUserDao;
import guru.qa.niffler.data.dao.impl.UserdataUserDaoJdbc;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.UserdataUserRepository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jdbc.Connections.holder;

public class UserdataUserRepositoryJdbc implements UserdataUserRepository {

    private static final Config CFG = Config.getInstance();
    private static final String URL = CFG.userdataJdbcUrl();
    private static final UserdataUserDao userDao = new UserdataUserDaoJdbc();

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
    public void addFriendshipRequest(UserEntity requester, UserEntity addressee) {
        requester.addFriends(FriendshipStatus.PENDING, addressee);
        userDao.update(requester);
    }

    @Override
    public void addFriend(UserEntity requester, UserEntity addressee) {
        requester.addFriends(FriendshipStatus.ACCEPTED, addressee);
        addressee.addFriends(FriendshipStatus.ACCEPTED, requester);
        userDao.update(requester);
        userDao.update(addressee);
    }

    @Override
    public void remove(UserEntity user) {
        try (PreparedStatement friendshipPs = holder(URL).connection().prepareStatement(
                """
                        DELETE FROM friendship
                        WHERE requester_id = ? or addressee_id = ?
                        """);
             PreparedStatement userPs = holder(URL).connection().prepareStatement(
                     """    
                             DELETE FROM "user"
                             WHERE id = ?
                             """)
        ) {
            UUID userId = user.getId();
            friendshipPs.setObject(1, userId);
            friendshipPs.setObject(2, userId);
            friendshipPs.executeUpdate();
            userPs.setObject(1, userId);
            userPs.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
