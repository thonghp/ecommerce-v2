package com.hpt.backend.category;

import com.hpt.common.entity.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class CategoryServiceTest {

    @MockBean
    private CategoryRepository repo;

    @InjectMocks
    private CategoryService service;

    @Test
    public void testCheckUniqueInNewModeReturnDuplicateName() {
        Integer id = null;
        String name = "Laptop";
        String alias = "lap-top";
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        category.setAlias(alias);

        Mockito.when(repo.findByName(name)).thenReturn(category);
        Mockito.when(repo.findByAlias(alias)).thenReturn(null);
        String result = service.checkUnique(id, name, alias);

        assertThat(result).isEqualTo("Duplicate name");
    }

    @Test
    public void testCheckUniqueInNewModeReturnDuplicateAlias() {
        Integer id = null;
        String name = "Default";
        String alias = "Ghế-gaming-theo-thương-hiệu";
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        category.setAlias(alias);

        Mockito.when(repo.findByName(name)).thenReturn(null);
        Mockito.when(repo.findByAlias(alias)).thenReturn(category);
        String result = service.checkUnique(id, name, alias);

        assertThat(result).isEqualTo("Duplicate alias");
    }

    @Test
    public void testCheckUniqueInNewModeReturnOK() {
        Integer id = null;
        String name = "Default";
        String alias = "default";

        Mockito.when(repo.findByName(name)).thenReturn(null);
        Mockito.when(repo.findByAlias(alias)).thenReturn(null);

        String result = service.checkUnique(id, name, alias);

        assertThat(result).isEqualTo("OK");
    }

    @Test
    public void testCheckUniqueInEditModeReturnDuplicateName() {
        Integer id = 1;
        String name = "Laptop";
        String alias = "lap-top";

        Category editCategory = new Category();
        editCategory.setId(2);
        editCategory.setName(name);
        editCategory.setAlias(alias);

        Mockito.when(repo.findByName(name)).thenReturn(editCategory);
        Mockito.when(repo.findByAlias(alias)).thenReturn(null);

        String result = service.checkUnique(id, name, alias);

        assertThat(result).isEqualTo("Duplicate name");
    }

    @Test
    public void testCheckUniqueInEditModeReturnDuplicateAlias() {
        Integer id = 1;
        String name = "Default";
        String alias = "Ghế-gaming-theo-thương-hiệu";

        Category editCategory = new Category();
        editCategory.setId(2);
        editCategory.setName(name);
        editCategory.setAlias(alias);

        Mockito.when(repo.findByName(name)).thenReturn(null);
        Mockito.when(repo.findByAlias(alias)).thenReturn(editCategory);

        String result = service.checkUnique(id, name, alias);

        assertThat(result).isEqualTo("Duplicate alias");
    }

    @Test
    public void testCheckUniqueInEditModeReturnOK() {
        Integer id = 1;
        String name = "Default";
        String alias = "default";

        Category editCategory = new Category();
        editCategory.setId(id);
        editCategory.setName(name);
        editCategory.setAlias(alias);

        Mockito.when(repo.findByName(name)).thenReturn(null);
        Mockito.when(repo.findByAlias(alias)).thenReturn(editCategory);

        String result = service.checkUnique(id, name, alias);

        assertThat(result).isEqualTo("OK");
    }
}
