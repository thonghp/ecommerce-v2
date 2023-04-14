package com.hpt.backend.user;

import com.hpt.common.entity.Role;
import com.hpt.common.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {
    @Autowired
    private UserRepository repo;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testCreateUserWithOneRole() {
        Role employee = entityManager.find(Role.class, 2);
        User user = new User();
        user.setFirstName("Thông");
        user.setLastName("Hoàng Phạm");
        user.setPhoneNumber("0766821606");
        user.setImagePath("nen.jpg");
        user.setEmail("thong@gmail.com");
        user.setPassword("thong123");
        user.setEnabled(true);
        user.addRole(employee);

        User savedUser = repo.save(user);
        assertThat(savedUser.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateUserWithMultipleRole() {
        Role shipper = entityManager.find(Role.class, 3);
        Role assistant = entityManager.find(Role.class, 5);
        User user = new User();
        user.setFirstName("Vinh");
        user.setLastName("Nguyễn Thái");
        user.setPhoneNumber("0123456789");
        user.setEmail("vinh@gmail.com");
        user.setPassword("vinh123");
        user.setEnabled(true);
        user.addRole(shipper);
        user.addRole(assistant);

        User savedUser = repo.save(user);
        assertThat(savedUser.getId()).isGreaterThan(0);
    }

    @Test
    public void testListAllUsers() {
        Iterable<User> users = repo.findAll();
        users.forEach(System.out::println); // users.forEach(user -> System.out.println(user));
    }

    @Test
    public void testGetUserById() {
        User user = repo.findById(1).get();
        System.out.println(user);
        assertThat(user).isNotNull();
    }

    @Test
    public void testUpdateUserDetails() {
        User user = repo.findById(3).get();
        user.setLastName("Nguyễn Hoàng");
        user.setEnabled(false);
        repo.save(user);
    }

    @Test
    public void testUpdateUserRoles() {
        Role shipperEmployee = entityManager.find(Role.class, 3);
        Role editorEmployee = entityManager.find(Role.class, 4);
        User user = repo.findById(3).get();
        user.getRoles().remove(shipperEmployee);
        user.addRole(editorEmployee);
        repo.save(user);
    }

    @Test
    public void testDeleteUser() {
        Integer userId = 3;
        repo.deleteById(userId);
    }

    @Test
    public void testFindByEmail() {
        String email = "thong@gmail.com";
        User userByEmail = repo.findByEmail(email);

        assertThat(userByEmail).isNotNull();
    }

    @Test
    public void testCountById() {
        Integer id = 1;
        Long countById = repo.countById(id);

        assertThat(countById).isNotNull().isGreaterThan(0);
    }

    @Test
    public void testDisableUser() {
        Integer id = 3;
        repo.updateEnabledStatus(id, false);
    }

    @Test
    public void testEnableUser() {
        Integer id = 2;
        repo.updateEnabledStatus(id, true);
    }
}