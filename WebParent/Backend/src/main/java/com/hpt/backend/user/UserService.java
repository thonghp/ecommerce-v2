package com.hpt.backend.user;

import com.hpt.backend.role.RoleRepository;
import com.hpt.common.entity.Role;
import com.hpt.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoleRepository roleRepo;

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
     * Save a user to the database
     *
     * @param user User to be saved
     */
    public void save(User user) {
        userRepo.save(user);
    }
}
