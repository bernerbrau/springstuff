package org.vumc.controllers;


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
    public List<User> getUsers() throws Exception {
        LOGGER.info("Getting list of all users.");
        return userDetailsManager.findAllUsers()
                   .stream()
                   .map(d -> User.fromUserDetails(d, false))
                   .collect(Collectors.toList());
    }

    @PostAuthorize("returnObject.isConfigurableByUserAdmin()")
    public User getUser(String username) throws Exception {
        LOGGER.info("Getting user {}", username);
        return User.fromUserDetails(userDetailsManager.loadUserByUsername(username), false);
    }

    public void createNewUser(User userDetails) throws Exception {
        LOGGER.info("Creating New User {}", userDetails.getUsername());
        userDetails.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        userDetailsManager.createUser(userDetails);
    }

    @PreAuthorize("@userController.isConfigurableByUserAdmin(#username)")
    public void updateUser(String username, User user) throws Exception {
        LOGGER.info("Updating User {}", user.getUsername());

        UserDetails currentUser = userDetailsManager.loadUserByUsername(username);
        if (currentUser == null) {
            throw new UsernameNotFoundException(username);
        }

        if(user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(currentUser.getPassword());
        }

        userDetailsManager.updateUser(user);
    }

    @PreAuthorize("@userController.isConfigurableByUserAdmin(#username)")
    public void deleteUser(String username) throws Exception {
        LOGGER.info("Deleting User {}", username);

        userDetailsManager.deleteUser(username);
    }

    public boolean isConfigurableByUserAdmin(String username) {
        return ((User)userDetailsManager.loadUserByUsername(username))
                   .isConfigurableByUserAdmin();
    }

}
