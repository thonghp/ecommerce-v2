package com.hpt.backend.brand;

import com.hpt.common.entity.Brand;
import com.hpt.common.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BrandRepositoryTest {
    @Autowired
    private BrandRepository repo;

    @Test
    public void testCreateBrand() {
        Category laptop = new Category();
        laptop.setId(12);

        Brand brand = new Brand();
        brand.setName("Apple");
        brand.setImagePath("apple.png");
        brand.getCategories().add(laptop);

        Brand savedBrand = repo.save(brand);

        assertThat(savedBrand).isNotNull();
        assertThat(savedBrand.getId()).isGreaterThan(0);
    }

    @Test
    public void testGetBrandById() {
        Brand brand = repo.findById(1).get();
        assertThat(brand.getName()).isEqualTo("Apple");
    }

    @Test
    public void testUpdateBrand() {
        Category cellphone = new Category();
        cellphone.setId(37);
        Brand brand = repo.findById(1).get();
        brand.getCategories().add(cellphone);

        Brand savedBrand = repo.save(brand);

        assertThat(savedBrand.getCategories().size()).isEqualTo(2);
    }

    @Test
    public void testDeleteUser() {
        Integer brandId = 1;
        repo.deleteById(brandId);
    }

    @Test
    public void testListAllBrands() {
        Iterable<Brand> brands = repo.findAll();
        brands.forEach(System.out::println); 
    }
}