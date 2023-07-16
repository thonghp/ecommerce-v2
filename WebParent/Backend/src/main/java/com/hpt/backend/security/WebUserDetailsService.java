package com.hpt.backend.security;

import com.hpt.backend.user.UserRepository;
import com.hpt.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class WebUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepo;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepo.findByEmail(email);

        if (user != null) {
            return new WebUserDetails(user);
        }
        throw new UsernameNotFoundException("User not found with email: " + email);
    }
}
