package guru.qa.niffler.service;

import guru.qa.niffler.data.CategoryEntity;
import guru.qa.niffler.data.repository.CategoryRepository;
import guru.qa.niffler.ex.CategoryNotFoundException;
import guru.qa.niffler.ex.InvalidCategoryNameException;
import guru.qa.niffler.ex.TooManyCategoriesException;
import guru.qa.niffler.model.CategoryJson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

  @Test
  void categoryNotFoundExceptionShouldBeThrown(@Mock CategoryRepository categoryRepository) {
    final String username = "not_found";
    final UUID id = UUID.randomUUID();

    Mockito.when(categoryRepository.findByUsernameAndId(eq(username), eq(id)))
        .thenReturn(Optional.empty());

    CategoryService categoryService = new CategoryService(categoryRepository);

    CategoryJson categoryJson = new CategoryJson(
        id,
        "",
        username,
        true
    );

    CategoryNotFoundException ex = Assertions.assertThrows(
        CategoryNotFoundException.class,
        () -> categoryService.update(categoryJson)
    );
    Assertions.assertEquals(
        "Can`t find category by id: '" + id + "'",
        ex.getMessage()
    );
  }

  @ValueSource(strings = {"Archived", "ARCHIVED", "ArchIved"})
  @ParameterizedTest
  void categoryNameArchivedShouldBeDenied(String catName, @Mock CategoryRepository categoryRepository) {
    final String username = "duck";
    final UUID id = UUID.randomUUID();
    final CategoryEntity cat = new CategoryEntity();

    Mockito.when(categoryRepository.findByUsernameAndId(eq(username), eq(id)))
        .thenReturn(Optional.of(
            cat
        ));

    CategoryService categoryService = new CategoryService(categoryRepository);

    CategoryJson categoryJson = new CategoryJson(
        id,
        catName,
        username,
        true
    );

    InvalidCategoryNameException ex = Assertions.assertThrows(
        InvalidCategoryNameException.class,
        () -> categoryService.update(categoryJson)
    );
    Assertions.assertEquals(
        "Can`t add category with name: '" + catName + "'",
        ex.getMessage()
    );
  }

  @Test
  void onlyTwoFieldsShouldBeUpdated(@Mock CategoryRepository categoryRepository) {
    final String username = "duck";
    final UUID id = UUID.randomUUID();
    final CategoryEntity cat = new CategoryEntity();
    cat.setId(id);
    cat.setUsername(username);
    cat.setName("Магазины");
    cat.setArchived(false);

    Mockito.when(categoryRepository.findByUsernameAndId(eq(username), eq(id)))
        .thenReturn(Optional.of(
            cat
        ));
    Mockito.when(categoryRepository.save(any(CategoryEntity.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    CategoryService categoryService = new CategoryService(categoryRepository);

    CategoryJson categoryJson = new CategoryJson(
        id,
        "Бары",
        username,
        true
    );

    categoryService.update(categoryJson);
    ArgumentCaptor<CategoryEntity> argumentCaptor = ArgumentCaptor.forClass(CategoryEntity.class);
    verify(categoryRepository).save(argumentCaptor.capture());
    assertEquals("Бары", argumentCaptor.getValue().getName());
    assertEquals("duck", argumentCaptor.getValue().getUsername());
    assertTrue(argumentCaptor.getValue().isArchived());
    assertEquals(id, argumentCaptor.getValue().getId());
  }

    @Test
    void getAllCategoriesShouldReturnAllCategoriesWhenExcludeArchivedIsFalse(@Mock CategoryRepository categoryRepository) {
        final String username = "duck";

        CategoryEntity activeCategory = new CategoryEntity();
        activeCategory.setName("Магазины");
        activeCategory.setUsername(username);
        activeCategory.setArchived(false);

        CategoryEntity archivedCategory = new CategoryEntity();
        archivedCategory.setName("Рестораны");
        archivedCategory.setUsername(username);
        archivedCategory.setArchived(true);

        Mockito.when(categoryRepository.findAllByUsernameOrderByName(eq(username)))
                .thenReturn(List.of(activeCategory, archivedCategory));

        CategoryService categoryService = new CategoryService(categoryRepository);

        List<CategoryJson> result = categoryService.getAllCategories(username, false);

        assertEquals(2, result.size());
    }

    @Test
    void getAllCategoriesShouldExcludeArchivedWhenExcludeArchivedIsTrue(@Mock CategoryRepository categoryRepository) {
        final String username = "duck";

        CategoryEntity activeCategory = new CategoryEntity();
        activeCategory.setName("Магазины");
        activeCategory.setUsername(username);
        activeCategory.setArchived(false);

        CategoryEntity archivedCategory = new CategoryEntity();
        archivedCategory.setName("Рестораны");
        archivedCategory.setUsername(username);
        archivedCategory.setArchived(true);

        Mockito.when(categoryRepository.findAllByUsernameOrderByName(eq(username)))
                .thenReturn(List.of(activeCategory, archivedCategory));

        CategoryService categoryService = new CategoryService(categoryRepository);

        List<CategoryJson> result = categoryService.getAllCategories(username, true);

        assertEquals(1, result.size());
        assertEquals("Магазины", result.getFirst().name());
        assertFalse(result.getFirst().archived());
    }

    @Test
    void updateShouldThrowTooManyCategoriesExceptionWhenUnarchivingExceedsLimit(@Mock CategoryRepository categoryRepository) {
        final String username = "duck";
        final UUID id = UUID.randomUUID();

        CategoryEntity archivedCat = new CategoryEntity();
        archivedCat.setId(id);
        archivedCat.setUsername(username);
        archivedCat.setName("Рестораны");
        archivedCat.setArchived(true);

        Mockito.when(categoryRepository.findByUsernameAndId(eq(username), eq(id)))
                .thenReturn(Optional.of(archivedCat));
        Mockito.when(categoryRepository.countByUsernameAndArchived(eq(username), eq(false)))
                .thenReturn(8L);

        CategoryService categoryService = new CategoryService(categoryRepository);

        TooManyCategoriesException ex = Assertions.assertThrows(
                TooManyCategoriesException.class,
                () -> categoryService.update(new CategoryJson(id, "Рестораны", username, false))
        );
        assertEquals("Can`t unarchive category for user: '" + username + "'", ex.getMessage());
    }

    @Test
    void updateShouldSuccessfullyUnarchiveCategoryWhenCountIsWithinLimit(@Mock CategoryRepository categoryRepository) {
        final String username = "duck";
        final UUID id = UUID.randomUUID();

        CategoryEntity archivedCat = new CategoryEntity();
        archivedCat.setId(id);
        archivedCat.setUsername(username);
        archivedCat.setName("Рестораны");
        archivedCat.setArchived(true);

        Mockito.when(categoryRepository.findByUsernameAndId(eq(username), eq(id)))
                .thenReturn(Optional.of(archivedCat));
        Mockito.when(categoryRepository.countByUsernameAndArchived(eq(username), eq(false)))
                .thenReturn(7L);
        Mockito.when(categoryRepository.save(any(CategoryEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CategoryService categoryService = new CategoryService(categoryRepository);

        CategoryJson result = categoryService.update(new CategoryJson(id, "Рестораны", username, false));

        assertFalse(result.archived());
        assertEquals("Рестораны", result.name());
    }

    @Test
    void updateShouldNotCheckCategoryLimitWhenArchivingActiveCategory(@Mock CategoryRepository categoryRepository) {
        final String username = "duck";
        final UUID id = UUID.randomUUID();

        CategoryEntity activeCat = new CategoryEntity();
        activeCat.setId(id);
        activeCat.setUsername(username);
        activeCat.setName("Магазины");
        activeCat.setArchived(false);

        Mockito.when(categoryRepository.findByUsernameAndId(eq(username), eq(id)))
                .thenReturn(Optional.of(activeCat));
        Mockito.when(categoryRepository.save(any(CategoryEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CategoryService categoryService = new CategoryService(categoryRepository);

        CategoryJson result = categoryService.update(new CategoryJson(id, "Магазины", username, true));

        assertTrue(result.archived());
        verify(categoryRepository, Mockito.never()).countByUsernameAndArchived(any(String.class), anyBoolean());
    }

    @ValueSource(strings = {"Archived", "ARCHIVED", "ArchIved"})
    @ParameterizedTest
    void saveArchivedCategoryNameShouldBeDenied(String catName, @Mock CategoryRepository categoryRepository) {
        CategoryService categoryService = new CategoryService(categoryRepository);

        InvalidCategoryNameException ex = Assertions.assertThrows(
                InvalidCategoryNameException.class,
                () -> categoryService.addCategory(new CategoryJson(null, catName, "duck", false))
        );
        assertEquals("Can`t add category with name: '" + catName + "'", ex.getMessage());
    }

    @Test
    void saveShouldThrowTooManyCategoriesExceptionWhenLimitExceeded(@Mock CategoryRepository categoryRepository) {
        final String username = "duck";

        Mockito.when(categoryRepository.countByUsernameAndArchived(eq(username), eq(false)))
                .thenReturn(8L);

        CategoryService categoryService = new CategoryService(categoryRepository);

        TooManyCategoriesException ex = Assertions.assertThrows(
                TooManyCategoriesException.class,
                () -> categoryService.addCategory(new CategoryJson(null, "Рестораны", username, false))
        );
        assertEquals("Can`t add over than 8 categories for user: '" + username + "'", ex.getMessage());
    }

    @Test
    void saveShouldSuccessfullySaveCategoryWhenCountIsWithinLimit(@Mock CategoryRepository categoryRepository) {
        final String username = "duck";
        final UUID savedId = UUID.randomUUID();

        Mockito.when(categoryRepository.countByUsernameAndArchived(eq(username), eq(false)))
                .thenReturn(7L);
        Mockito.when(categoryRepository.save(any(CategoryEntity.class)))
                .thenAnswer(invocation -> {
                    CategoryEntity ce = invocation.getArgument(0);
                    ce.setId(savedId);
                    return ce;
                });

        CategoryService categoryService = new CategoryService(categoryRepository);

        CategoryJson result = categoryService.addCategory(new CategoryJson(null, "Рестораны", username, false));

        assertEquals(savedId, result.id());
        assertEquals("Рестораны", result.name());
        assertEquals(username, result.username());
        assertFalse(result.archived());
    }
}
