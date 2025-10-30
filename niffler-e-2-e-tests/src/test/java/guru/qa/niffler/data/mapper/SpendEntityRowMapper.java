package guru.qa.niffler.data.mapper;

import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CurrencyValues;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SpendEntityRowMapper implements RowMapper<SpendEntity> {

    public static final SpendEntityRowMapper instance = new SpendEntityRowMapper();

    private SpendEntityRowMapper() {
    }

    @Override
    public SpendEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        SpendEntity entity = new SpendEntity();
        entity.setId(rs.getObject("id", UUID.class));
        entity.setUsername(rs.getString("username"));
        entity.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
        entity.setSpendDate(rs.getObject("spend_date", Date.class));
        entity.setAmount(rs.getObject("amount", Double.class));
        entity.setDescription(rs.getString("description"));
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setId(rs.getObject("category_id", UUID.class));
        categoryEntity.setName(rs.getString("name"));
        categoryEntity.setUsername(rs.getString("username"));
        categoryEntity.setArchived(rs.getBoolean("archived"));
        entity.setCategory(categoryEntity);
        return entity;
    }
}
