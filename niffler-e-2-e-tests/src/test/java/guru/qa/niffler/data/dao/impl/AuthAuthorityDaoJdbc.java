package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class AuthAuthorityDaoJdbc implements AuthAuthorityDao {

    private static final Config CFG = Config.getInstance();

    @Override
    public void create(AuthorityEntity... authorities) {
        try (PreparedStatement statement = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "INSERT INTO authority (user_id, authority) VALUES (?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            for (AuthorityEntity authority : authorities) {
                statement.setObject(1, authority.getUser().getId());
                statement.setString(2, authority.getAuthority().name());
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
        try (PreparedStatement statement = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM authority"
        )) {
            statement.execute();
            try (ResultSet resultSet = statement.getResultSet()) {
                List<AuthorityEntity> entities = new ArrayList<>();
                while (resultSet.next()) {
                    AuthorityEntity entity = new AuthorityEntity();
                    entity.setId(resultSet.getObject("id", UUID.class));
                    // entity.setUserId(resultSet.getObject("user_id", UUID.class));
                    entity.setAuthority(Authority.valueOf(resultSet.getString("authority")));
                    entities.add(entity);
                }
                return entities;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
