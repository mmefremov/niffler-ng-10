package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.mapper.AuthorityEntityRowMapper;

import javax.annotation.Nonnull;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static guru.qa.niffler.data.jdbc.Connections.holder;

public class AuthAuthorityDaoJdbc implements AuthAuthorityDao {

    private static final Config CFG = Config.getInstance();
    private static final String URL = CFG.authJdbcUrl();

    @Override
    @SuppressWarnings("resource")
    public void create(AuthorityEntity... authorities) {
        try (PreparedStatement statement = holder(URL).connection().prepareStatement(
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

    @Nonnull
    @Override
    @SuppressWarnings("resource")
    public List<AuthorityEntity> findAll() {
        try (PreparedStatement statement = holder(URL).connection().prepareStatement(
                "SELECT * FROM authority"
        )) {
            statement.execute();
            try (ResultSet resultSet = statement.getResultSet()) {
                List<AuthorityEntity> entities = new ArrayList<>();
                while (resultSet.next()) {
                    entities.add(AuthorityEntityRowMapper.instance.mapRow(resultSet, resultSet.getRow()));
                }
                return entities;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Nonnull
    @Override
    @SuppressWarnings("resource")
    public List<AuthorityEntity> findAllByUserId(UUID userId) {
        try (PreparedStatement ps = holder(URL).connection().prepareStatement(
                "SELECT * FROM authority where user_id = ?")) {
            ps.setObject(1, userId);
            ps.execute();
            List<AuthorityEntity> entities = new ArrayList<>();
            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    entities.add(AuthorityEntityRowMapper.instance.mapRow(rs, rs.getRow()));
                }
            }
            return entities;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
