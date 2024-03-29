package com.hpt.backend.user;

import com.hpt.backend.paging.PagingAndSortingHelper;
import com.hpt.backend.role.RoleRepository;
import com.hpt.common.entity.Role;
import com.hpt.common.entity.User;
import com.hpt.common.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class UserService {
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private RoleRepository roleRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public final static int USERS_PER_PAGE = 5;

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
     * Return an encrypted string
     *
     * @param user user to be encoded
     */
    private void encodePassword(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
    }

    /**
     * Save user information. If the id does not exist, save the user, otherwise the id already exists, update the user
     *
     * @param user user object to save
     * @return saved user object
     */
    public User save(User user) {
        boolean isUpdatingUser = (user.getId() != null);

        if (isUpdatingUser) {
            User existingUser = userRepo.findById(user.getId()).get();
            if (user.getPassword().isEmpty()) {
                user.setPassword(existingUser.getPassword());
            } else {
                encodePassword(user);
            }
        } else {
            encodePassword(user);
        }

        return userRepo.save(user);
    }

    /**
     * Check if the email already exists. The result is {@code true} if the input id is {@code null} and
     * the incoming email does not exist or the received id is not {@code null} and the received email has the same id
     * as the received id.
     *
     * @param email email to be checked
     * @return {@code true} if email is unique, {@code false} otherwise
     */
    public boolean isEmailUnique(Integer id, String email) {
        User userByEmail = userRepo.findByEmail(email);

        boolean isUpdatingId = (id != null);

        if (isUpdatingId) {
            return userByEmail.getId().equals(id);
        } else {
            return userByEmail == null;
        }
    }

    /**
     * Get a user by id
     *
     * @param id id of the user
     * @return user object corresponding to id
     * @throws UserNotFoundException if the user does not exist
     */
    public User get(Integer id) throws UserNotFoundException {
        try {
            return userRepo.findById(id).get();
        } catch (Exception ex) {
            throw new UserNotFoundException("Could not find an employee with id " + id);
        }
    }

    /**
     * Delete a user by id
     *
     * @param id id of the user you want to delete
     * @throws UserNotFoundException if the user does not exist
     */
    public void delete(Integer id) throws UserNotFoundException {
        Long countById = userRepo.countById(id);
        if (countById == null || countById == 0) {
            throw new UserNotFoundException("Could not find an employee with id " + id);
        }
        userRepo.deleteById(id);
    }

    /**
     * Update the enabled status of a user
     *
     * @param id      id of the user
     * @param enabled enabled status
     */
    public void updateUserEnabledStatus(Integer id, boolean enabled) {
        userRepo.updateEnabledStatus(id, enabled);
    }

    /**
     * Returns a paginated list of default or search users and sorted ascending or descending by the specified column.
     *
     * @param pageNum The page requests to return data
     * @param helper  The object helps to sort, search and pagination
     * @return a list of users
     */
    public List<User> listByPage(int pageNum, PagingAndSortingHelper helper) {
        return (List<User>) helper.listByPage(pageNum, USERS_PER_PAGE, userRepo);
    }

    /**
     * Return the user corresponding to the email
     *
     * @param email email of the user
     * @return the user object corresponding to email
     */
    public User getByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    /**
     * Save information user sent from user profile to update
     *
     * @param userInForm the user object sent from user profile
     * @return the user object after saving
     */
    public User updateAccount(User userInForm) {
        User userInDB = userRepo.findById(userInForm.getId()).get();

        if (!userInForm.getPassword().isEmpty()) {
            userInDB.setPassword(userInForm.getPassword());
            encodePassword(userInDB);
        }

        if (userInForm.getImagePath() != null) {
            userInDB.setImagePath(userInForm.getImagePath());
        }

        userInDB.setFirstName(userInForm.getFirstName());
        userInDB.setLastName(userInForm.getLastName());
        userInDB.setPhoneNumber(userInForm.getPhoneNumber());

        return userRepo.save(userInDB);
    }
}
