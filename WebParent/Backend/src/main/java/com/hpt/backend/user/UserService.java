package com.hpt.backend.user;

import com.hpt.backend.role.RoleRepository;
import com.hpt.common.entity.Role;
import com.hpt.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private RoleRepository roleRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Return a list of all users
     *
     * @return List of users
     */
    public List<User> listAll() {
        return (List<User>) userRepo.findAll();
    }

    /**
     * Return a list of all roles
     *
     * @return List of roles
     */
    public List<Role> listRoles() {
        return (List<Role>) roleRepo.findAll();
    }

    /**
     * Encode the password of a user
     *
     * @param user User to be encoded
     */
    private void encodePassword(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
    }

    /**
     * Save a user to the database
     *
     * @param user User to be saved
     */
    public void save(User user) {
        encodePassword(user);
        userRepo.save(user);
    }

    /**
     * Check email is unique
     *
     * @param email Email to be checked
     * @return True if email is unique, false otherwise
     */
    public boolean isEmailUnique(String email) {
        User userByEmail = userRepo.findByEmail(email);

        return userByEmail == null;
    }
}
