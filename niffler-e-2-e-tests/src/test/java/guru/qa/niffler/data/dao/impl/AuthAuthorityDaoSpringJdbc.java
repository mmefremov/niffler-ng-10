package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class AuthAuthorityDaoSpringJdbc implements AuthAuthorityDao {

    private final DataSource dataSource;

    public AuthAuthorityDaoSpringJdbc(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void create(AuthorityEntity... authority) {
        JdbcTemplate template = new JdbcTemplate(dataSource);
        template.batchUpdate(
                "INSERT INTO authority (user_id, authority) VALUES (? , ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setObject(1, authority[i].getUserId());
                        ps.setString(2, authority[i].getAuthority());
                    }

                    @Override
                    public int getBatchSize() {
                        return authority.length;
                    }
                }
        );
    }

    @Override
    public List<AuthorityEntity> findAll() {
        JdbcTemplate template = new JdbcTemplate(dataSource);
        return template.query(
                "SELECT * FROM authority",
                new RowMapper<AuthorityEntity>() {
                    @Override
                    public AuthorityEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
                        AuthorityEntity entity = new AuthorityEntity();
                        entity.setId(rs.getObject("id", UUID.class));
                        entity.setUserId(rs.getObject("user_id", UUID.class));
                        entity.setAuthority(rs.getString("authority"));
                        return entity;
                    }
                }
        );
    }
}
