package guru.qa.niffler.data.extractor;

import guru.qa.niffler.data.entity.userdata.FriendshipEntity;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.model.CurrencyValues;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UserdataUserEntityExtractor implements ResultSetExtractor<UserEntity> {

    public static final UserdataUserEntityExtractor instance = new UserdataUserEntityExtractor();

    private UserdataUserEntityExtractor() {
    }

    @Override
    public UserEntity extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<UUID, UserEntity> userMap = new ConcurrentHashMap<>();
        UUID userId = null;
        while (rs.next()) {
            userId = rs.getObject("id", UUID.class);
            UserEntity user = userMap.computeIfAbsent(userId, id -> {
                try {
                    UserEntity newUser = new UserEntity();
                    newUser.setId(id);
                    newUser.setUsername(rs.getString("username"));
                    newUser.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                    newUser.setFirstname(rs.getString("firstname"));
                    newUser.setSurname(rs.getString("surname"));
                    newUser.setFullname(rs.getString("full_name"));
                    newUser.setPhoto(rs.getBytes("photo"));
                    newUser.setPhotoSmall(rs.getBytes("photo_small"));
                    return newUser;
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            var friendship = new FriendshipEntity();
            var requester = new UserEntity();
            UUID addresseeId = rs.getObject("addressee_id", UUID.class);
            requester.setId(addresseeId);
            var addressee = new UserEntity();
            UUID requesterId = rs.getObject("requester_id", UUID.class);
            addressee.setId(requesterId);
            friendship.setRequester(requester);
            friendship.setAddressee(addressee);
            friendship.setStatus(FriendshipStatus.valueOf(rs.getString("status")));
            friendship.setCreatedDate(rs.getDate("created_date"));

            if (addresseeId.equals(userId)) {
                user.getFriendshipAddressees().add(friendship);
            } else {
                user.getFriendshipRequests().add(friendship);
            }
        }
        return userMap.get(userId);
    }
}
