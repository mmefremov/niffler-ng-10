package guru.qa.niffler.data.extractor;

import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CurrencyValues;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@ParametersAreNonnullByDefault
public class SpendEntityExtractor implements ResultSetExtractor<SpendEntity> {

    public static final SpendEntityExtractor instance = new SpendEntityExtractor();

    private SpendEntityExtractor() {
    }

    @Override
    public SpendEntity extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<UUID, SpendEntity> spendMap = new ConcurrentHashMap<>();
        Map<UUID, CategoryEntity> categoryMap = new ConcurrentHashMap<>();
        UUID spendId = null;
        while (rs.next()) {
            spendId = rs.getObject("id", UUID.class);
            SpendEntity spend = spendMap.computeIfAbsent(spendId, id -> {
                try {
                    SpendEntity newSpend = new SpendEntity();
                    newSpend.setId(id);
                    newSpend.setUsername(rs.getString("username"));
                    newSpend.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                    newSpend.setSpendDate(rs.getObject("spend_date", Date.class));
                    newSpend.setAmount(rs.getObject("amount", Double.class));
                    newSpend.setDescription(rs.getString("description"));
                    return newSpend;
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

            UUID categoryId = rs.getObject("category_id", UUID.class);
            CategoryEntity category = categoryMap.computeIfAbsent(categoryId, id -> {
                try {
                    CategoryEntity newCategory = new CategoryEntity();
                    newCategory.setId(categoryId);
                    newCategory.setName(rs.getString("name"));
                    newCategory.setUsername(rs.getString("username"));
                    newCategory.setArchived(rs.getBoolean("archived"));
                    return newCategory;
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            spend.setCategory(category);
        }
        return spendMap.get(spendId);
    }
}
