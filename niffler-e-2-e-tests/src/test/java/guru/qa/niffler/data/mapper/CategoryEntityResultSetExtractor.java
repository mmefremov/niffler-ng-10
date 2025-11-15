package guru.qa.niffler.data.mapper;

import guru.qa.niffler.data.entity.spend.CategoryEntity;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CategoryEntityResultSetExtractor implements ResultSetExtractor<CategoryEntity> {

    public static final CategoryEntityResultSetExtractor instance = new CategoryEntityResultSetExtractor();

    private CategoryEntityResultSetExtractor() {
    }

    @Override
    public CategoryEntity extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<UUID, CategoryEntity> categoryMap = new ConcurrentHashMap<>();
        UUID categoryId = null;
        while (rs.next()) {
            categoryId = rs.getObject("id", UUID.class);
            categoryMap.computeIfAbsent(categoryId, id -> {
                try {
                    CategoryEntity newCategory = new CategoryEntity();
                    newCategory.setId(id);
                    newCategory.setName(rs.getString("name"));
                    newCategory.setUsername(rs.getString("username"));
                    newCategory.setArchived(rs.getBoolean("archived"));
                    return newCategory;
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        return categoryMap.get(categoryId);
    }
}
