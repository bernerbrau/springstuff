package org.vumc.controllers;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.*;
import org.vumc.model.Authority;
import org.vumc.model.User;
import org.vumc.users.UserDetailsManagerExt;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private UserDetailsManagerExt userDetailsManager;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(final UserDetailsManagerExt inUserMgr,
                          PasswordEncoder inpasswordEncoder){
        userDetailsManager = inUserMgr;
        passwordEncoder = inpasswordEncoder;
    }

    @GetMapping
    public List<? extends UserDetails> getUsers() throws Exception {
        return userDetailsManager.findAllUsers().stream()
                .map(this::erasePassword)
                .collect(Collectors.toList());
    }

    @GetMapping("{username}")
    public UserDetails getUser(@PathVariable("username") String username) throws Exception {
        UserDetails userDetails = userDetailsManager.loadUserByUsername(username);
        return this.erasePassword(userDetails);
    }

    @PostMapping
    public void createNewUser(@RequestBody User userDetails) throws Exception {
        LOGGER.info("Creating New User {}", userDetails.getUsername());
        userDetails.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        userDetailsManager.createUser(userDetails);
    }


    @PutMapping("{username}")
    public ResponseEntity<Void> updateUser(@PathVariable("username") String username,
                                           @RequestBody User user) throws Exception {
        LOGGER.info("Updating User {}", user.getUsername());

        UserDetails currentUser = userDetailsManager.loadUserByUsername(username);
        if (currentUser == null) {
            return ResponseEntity.notFound().build();
        }

        if(user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(currentUser.getPassword());
        }

        userDetailsManager.updateUser(user);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("{username}")
    public ResponseEntity<Void> patchUser(@PathVariable("username") String username,
                                           @RequestBody User user) throws Exception {
        LOGGER.info("Patching User {}", user.getUsername());

        UserDetails currentUser = userDetailsManager.loadUserByUsername(username);
        if (currentUser == null) {
            return ResponseEntity.notFound().build();
        }

        //--- Determine if password needs to be updated ---
        if(user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(currentUser.getPassword());
        }

        //--- Determine if Username needs to be updated ---
        //user.getUsername()
        if(user.getUsername()==null || user.getUsername().isEmpty()){
            user.setUsername(currentUser.getUsername());
        }

        //--- Determine if Authorities need to be updated ---
        if(user.getAuthorities() == null || user.getAuthorities().isEmpty()){
            user.setAuthorities(Authority.from(currentUser.getAuthorities()));
        }

        //-- The following are primitive booleans and thus must be either true or false.  I believe these default to false.
        //-- This may require the calling PATCH to always send these items, lest they get defaulted unintentionally.  May defeat the purpose of a "PATCH".
        //user.isAccountNonExpired()
        //user.isAccountNonLocked()
        //user.isCredentialsNonExpired()
        //user.isEnabled()

        userDetailsManager.updateUser(user);
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("{username}")
    public ResponseEntity<Void> deleteUser(@PathVariable("username") String username) throws Exception {
        LOGGER.info("Deleting User {}", username);

        userDetailsManager.deleteUser(username);
        return ResponseEntity.noContent().build();
    }


    private UserDetails erasePassword(UserDetails userDetails) {
        return User.fromUserDetails(userDetails, false);
    }

}