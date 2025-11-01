package guru.qa.niffler.data.mapper;

import guru.qa.niffler.data.entity.spend.CategoryEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class CategoryEntityRowMapper implements RowMapper<CategoryEntity> {

    public static final CategoryEntityRowMapper instance = new CategoryEntityRowMapper();

    private CategoryEntityRowMapper() {
    }

    @Override
    public CategoryEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        CategoryEntity entity = new CategoryEntity();
        entity.setId(rs.getObject("id", UUID.class));
        entity.setName(rs.getString("name"));
        entity.setUsername(rs.getString("username"));
        entity.setArchived(rs.getBoolean("archived"));
        return entity;
    }
}
