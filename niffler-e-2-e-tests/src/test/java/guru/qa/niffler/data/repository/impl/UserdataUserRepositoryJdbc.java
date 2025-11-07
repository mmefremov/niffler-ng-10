package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.userdata.FriendshipEntity;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.mapper.UserdataUserEntityRowMapper;
import guru.qa.niffler.data.repository.UserdataUserRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class UserdataUserRepositoryJdbc implements UserdataUserRepository {

    private static final Config CFG = Config.getInstance();
    private static final String URL = CFG.userdataJdbcUrl();

    @Override
    public UserEntity create(UserEntity user) {
        try (PreparedStatement statement = holder(URL).connection().prepareStatement(
                """
                        INSERT INTO "user" (username, currency, firstname, surname, photo, photo_small, full_name)
                        VALUES ( ?, ?, ?, ?, ?, ?, ?)
                        """,
                Statement.RETURN_GENERATED_KEYS
        )) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getCurrency().name());
            statement.setString(3, user.getFirstname());
            statement.setString(4, user.getSurname());
            statement.setObject(5, user.getPhoto());
            statement.setObject(6, user.getPhotoSmall());
            statement.setString(7, user.getFullname());
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
    public Optional<UserEntity> findById(UUID id) {
        try (PreparedStatement statement = holder(URL).connection().prepareStatement(
                """
                        SELECT u.*, f.*
                        FROM "user" u
                        LEFT JOIN friendship f ON u.id IN (f.addressee_id, f.requester_id)
                        WHERE u.id = ?
                        """
        )) {
            statement.setObject(1, id);
            statement.execute();
            UserEntity user = null;
            var friendshipAddressees = new ArrayList<FriendshipEntity>();
            var friendshipRequests = new ArrayList<FriendshipEntity>();

            try (ResultSet resultSet = statement.getResultSet()) {
                while (resultSet.next()) {
                    UUID userId = resultSet.getObject("id", UUID.class);
                    if (user == null) {
                        user = UserdataUserEntityRowMapper.instance.mapRow(resultSet, 1);
                    }
                    if (userId.equals(id)) {
                        var requester = new UserEntity();
                        UUID addresseeId = resultSet.getObject("addressee_id", UUID.class);
                        requester.setId(addresseeId);
                        var addressee = new UserEntity();
                        UUID requesterId = resultSet.getObject("requester_id", UUID.class);
                        addressee.setId(requesterId);
                        var friendship = new FriendshipEntity();
                        friendship.setRequester(requester);
                        friendship.setAddressee(addressee);
                        friendship.setStatus(FriendshipStatus.valueOf(resultSet.getString("status")));
                        friendship.setCreatedDate(resultSet.getDate("created_date"));

                        if (addresseeId.equals(userId)) {
                            friendshipAddressees.add(friendship);
                        } else {
                            friendshipRequests.add(friendship);
                        }
                    }
                }
                if (user == null) {
                    return Optional.empty();
                } else {
                    user.setFriendshipAddressees(friendshipAddressees);
                    user.setFriendshipRequests(friendshipRequests);
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
        try (PreparedStatement statement = holder(URL).connection().prepareStatement(
                """
                        INSERT INTO friendship (requester_id, addressee_id, status)
                        VALUES (?, ?, ?)
                        """
        )) {
            statement.setObject(1, requester.getId());
            statement.setObject(2, addressee.getId());
            statement.setString(3, status.name());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
