package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.mapper.SpendEntityRowMapper;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SpendDaoSpringJdbc implements SpendDao {

    private static final Config CFG = Config.getInstance();
    private static final String URL = CFG.spendUrl();

    @Override
    public SpendEntity create(SpendEntity spend) {
        JdbcTemplate template = new JdbcTemplate(DataSources.dataSource(URL));
        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO spend (username, spend_date, currency, amount, description, category_id) " +
                    "VALUES ( ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            statement.setString(1, spend.getUsername());
            statement.setDate(2, new java.sql.Date(spend.getSpendDate().getTime()));
            statement.setString(3, spend.getCurrency().name());
            statement.setDouble(4, spend.getAmount());
            statement.setString(5, spend.getDescription());
            statement.setObject(6, spend.getCategory().getId());
            return statement;
        }, keyHolder);

        UUID generatedKey = (UUID) keyHolder.getKeys().get("id");
        spend.setId(generatedKey);
        return spend;
    }

    @Override
    public Optional<SpendEntity> findSpendById(UUID id) {
        JdbcTemplate template = new JdbcTemplate(DataSources.dataSource(URL));
        return Optional.ofNullable(
                template.queryForObject(
                        """
                                SELECT * FROM spend
                                JOIN category ON spend.category_id = category.id
                                WHERE spend.id = ?
                                """,
                        SpendEntityRowMapper.instance,
                        id
                ));
    }

    @Override
    public List<SpendEntity> findAllByUsername(String username) {
        JdbcTemplate template = new JdbcTemplate(DataSources.dataSource(URL));
        return template.query(
                """
                        SELECT * FROM spend
                        JOIN category ON spend.category_id = category.id
                        WHERE spend.username = ?
                        """,
                SpendEntityRowMapper.instance,
                username
        );
    }

    @Override
    public void delete(SpendEntity spend) {
        JdbcTemplate template = new JdbcTemplate(DataSources.dataSource(URL));
        template.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM spend WHERE id = ?");
            statement.setObject(1, spend.getId());
            return statement;
        });
    }

    @Override
    public List<SpendEntity> findAll() {
        JdbcTemplate template = new JdbcTemplate(DataSources.dataSource(URL));
        return template.query(
                "SELECT * FROM spend",
                SpendEntityRowMapper.instance
        );
    }
}
