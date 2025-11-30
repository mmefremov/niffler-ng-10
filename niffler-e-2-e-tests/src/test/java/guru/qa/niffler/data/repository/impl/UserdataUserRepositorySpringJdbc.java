package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UserdataUserDao;
import guru.qa.niffler.data.dao.impl.UserdataUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.jdbc.DataSources;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class UserdataUserRepositorySpringJdbc implements UserdataUserRepository {

    private static final Config CFG = Config.getInstance();
    private static final String URL = CFG.userdataJdbcUrl();
    private static final UserdataUserDao userDao = new UserdataUserDaoSpringJdbc();

    @Nonnull
    @Override
    public UserEntity create(UserEntity user) {
        return userDao.create(user);
    }

    @Nonnull
    @Override
    public Optional<UserEntity> findById(UUID id) {
        return userDao.findById(id);
    }

    @Nonnull
    @Override
    public Optional<UserEntity> findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Nonnull
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
        JdbcTemplate template = new JdbcTemplate(DataSources.dataSource(URL));
        UUID userId = user.getId();
        template.update("DELETE FROM friendship WHERE requester_id = ? or addressee_id = ?", userId, userId);
        template.update("DELETE FROM \"user\" WHERE id = ?", userId);
    }
}
