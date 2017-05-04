package org.vumc.controllers;


import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.vumc.model.DefinedAuthority;
import org.vumc.model.User;
import org.vumc.security.annotations.AllowedAuthorities;
import org.vumc.users.UserDetailsManagerExt;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@AllowedAuthorities(DefinedAuthority.USER_ADMIN)
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private final UserDetailsManagerExt userDetailsManager;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(final UserDetailsManagerExt inUserMgr,
                          final PasswordEncoder inpasswordEncoder){
        userDetailsManager = inUserMgr;
        passwordEncoder = inpasswordEncoder;
    }

    @PostFilter("filterObject.isConfigurableByUserAdmin()")
    public List<User> getUsers() {
        LOGGER.info("Getting list of all users.");
        return userDetailsManager.findAllUsers()
                   .stream()
                   .map(d -> User.fromUserDetails(d, false))
                   .collect(Collectors.toList());
    }

    @PostAuthorize("@userController.isConfigurableByUserAdmin(#username)")
    public User getUser(String username) {
        LOGGER.info("Getting user {}", username);
        if (userDetailsManager.userExists(username)) {
            UserDetails userDetails = userDetailsManager.loadUserByUsername(username);
            return User.fromUserDetails(userDetails, false);
        } else {
            return null;
        }
    }

    public void createNewUser(User userDetails) {
        LOGGER.info("Creating New User {}", userDetails.getUsername());
        userDetails.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        userDetailsManager.createUser(userDetails);
    }

    @PreAuthorize("@userController.isConfigurableByUserAdmin(#username)")
    public void updateUser(String username, User user) {
        LOGGER.info("Updating User {}", user.getUsername());
        UserDetails currentUser = userDetailsManager.loadUserByUsername(username);

        if (!Objects.equals(currentUser.getUsername(), user.getUsername())) {
            throw new IllegalArgumentException("Username cannot be changed.");
        }

        if(Strings.isNullOrEmpty(user.getPassword())) {
            user.setPassword(currentUser.getPassword());
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        userDetailsManager.updateUser(user);
    }

    @PreAuthorize("@userController.isConfigurableByUserAdmin(#username)")
    public void deleteUser(String username) {
        LOGGER.info("Deleting User {}", username);

        if (!userDetailsManager.userExists(username)) {
            throw new UsernameNotFoundException(username);
        }

        userDetailsManager.deleteUser(username);
    }

    public boolean isConfigurableByUserAdmin(String username)
    {
        return !userDetailsManager.userExists(username) ||
               User.fromUserDetails(
                   userDetailsManager.loadUserByUsername(username),
                   false
               ).isConfigurableByUserAdmin();
    }

}
