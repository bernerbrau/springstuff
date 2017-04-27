package org.vumc.controllers;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.vumc.hypermedia.assembler.UserResourceAssembler;
import org.vumc.hypermedia.util.ResourceCollection;
import org.vumc.model.DefinedAuthority;
import org.vumc.model.User;
import org.vumc.security.annotations.AllowedAuthorities;
import org.vumc.users.UserDetailsManagerExt;

import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@RepositoryRestController
@RestController
@RequestMapping("/api/users")
@ExposesResourceFor(User.class)
@AllowedAuthorities(DefinedAuthority.USER_ADMIN)
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private UserDetailsManagerExt userDetailsManager;
    private PasswordEncoder passwordEncoder;
    private UserResourceAssembler resourceAssembler;

    @Autowired
    public UserController(final UserDetailsManagerExt inUserMgr,
                          PasswordEncoder inpasswordEncoder,
                          UserResourceAssembler inResourceAssembler){
        userDetailsManager = inUserMgr;
        passwordEncoder = inpasswordEncoder;
        resourceAssembler = inResourceAssembler;
    }

    @GetMapping
    @PostFilter("filterObject.isConfigurableByUserAdmin()")
    public ResourceCollection<User> getUsers() throws Exception {
        LOGGER.info("Getting list of all users.");
        List<? extends UserDetails> list = userDetailsManager.findAllUsers();
        return new ResourceCollection<>(
              resourceAssembler.toResources(list),
              linkTo(UserController.class).withSelfRel());
    }

    @GetMapping(path = "{username}")
    @PostAuthorize("returnObject.isConfigurableByUserAdmin()")
    public User getUser(@PathVariable("username") String username) throws Exception {
        LOGGER.info("Getting user {}", username);
        UserDetails userDetails = userDetailsManager.loadUserByUsername(username);

        return resourceAssembler.toResource(userDetails);
    }

    @PostMapping
    public void createNewUser(@RequestBody User userDetails) throws Exception {
        LOGGER.info("Creating New User {}", userDetails.getUsername());
        userDetails.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        userDetailsManager.createUser(userDetails);
    }

    @PutMapping(path = "{username}")
    @PreAuthorize("@userController.isConfigurableByUserAdmin(#username)")
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

    @PatchMapping(path = "{username}")
    @PreAuthorize("@userController.isConfigurableByUserAdmin(#username)")
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
            user.setAuthorities(DefinedAuthority.from(currentUser.getAuthorities()));
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

    @DeleteMapping(path = "{username}")
    @PreAuthorize("@userController.isConfigurableByUserAdmin(#username)")
    public ResponseEntity<Void> deleteUser(@PathVariable("username") String username) throws Exception {
        LOGGER.info("Deleting User {}", username);

        userDetailsManager.deleteUser(username);
        return ResponseEntity.noContent().build();
    }

    public boolean isConfigurableByUserAdmin(String username) {
        return ((User)userDetailsManager.loadUserByUsername(username))
                   .isConfigurableByUserAdmin();
    }

}
