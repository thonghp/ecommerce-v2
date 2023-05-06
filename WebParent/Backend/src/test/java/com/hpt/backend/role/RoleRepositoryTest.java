package com.hpt.backend.role;

import com.hpt.common.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class RoleRepositoryTest {
    @Autowired
    private RoleRepository repo; // inject an instance of RoleRepository

    @Test
    public void testCreateOneRole() {
        Role roleAdmin = new Role();
        roleAdmin.setName("admin");
        roleAdmin.setDescription("Manage everything");

        Role savedRole = repo.save(roleAdmin);

        assertThat(savedRole.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateMultipleRoles() {
        Role roleSalesperson = new Role();
        roleSalesperson.setName("salesperson");
        roleSalesperson.setDescription("Manage product price, customers, shipping, orders and sales report");

        Role roleEditor = new Role();
        roleEditor.setName("editor");
        roleEditor.setDescription("Manage categories, brands, products, articles and menus");

        Role roleShipper = new Role();
        roleShipper.setName("shipper");
        roleShipper.setDescription("View products, view orders and update order status");

        Role roleAssistant = new Role();
        roleAssistant.setName("assistant");
        roleAssistant.setDescription("Manage questions and reviews");

        repo.saveAll(List.of(roleSalesperson, roleEditor, roleShipper, roleAssistant));
    }
}