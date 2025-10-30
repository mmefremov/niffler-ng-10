package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AuthAuthorityDaoJdbc implements AuthAuthorityDao {

    private final Connection connection;

    public AuthAuthorityDaoJdbc(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void create(AuthorityEntity... authorities) {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO authority (user_id, authority) VALUES (?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            for (AuthorityEntity authority : authorities) {
                statement.setObject(1, authority.getUserId());
                statement.setString(2, authority.getAuthority());
                statement.addBatch();
                statement.clearParameters();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<AuthorityEntity> findAll() {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM authority"
        )) {
            statement.execute();
            try (ResultSet resultSet = statement.getResultSet()) {
                List<AuthorityEntity> entities = new ArrayList<>();
                while (resultSet.next()) {
                    AuthorityEntity entity = new AuthorityEntity();
                    entity.setId(resultSet.getObject("id", UUID.class));
                    entity.setUserId(resultSet.getObject("user_id", UUID.class));
                    entity.setAuthority(resultSet.getString("authority"));
                    entities.add(entity);
                }
                return entities;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
