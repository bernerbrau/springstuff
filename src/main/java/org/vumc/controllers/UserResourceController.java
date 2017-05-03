package org.vumc.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.*;
import org.vumc.hypermedia.assembler.UserResourceAssembler;
import org.vumc.model.DefinedAuthority;
import org.vumc.model.User;
import org.vumc.security.annotations.AllowedAuthorities;

import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@RepositoryRestController
@RestController
@RequestMapping("/api/users")
@ExposesResourceFor(User.class)
@AllowedAuthorities(DefinedAuthority.USER_ADMIN)
public class UserResourceController
{

    private UserController userController;
    private UserResourceAssembler resourceAssembler;

    @Autowired
    public UserResourceController(final UserController inUserController,
                                  final UserResourceAssembler inResourceAssembler){
        userController = inUserController;
        resourceAssembler = inResourceAssembler;
    }

    @GetMapping
    public Resources<User> getUsers() throws Exception {
        List<User> list = userController.getUsers();
        return new Resources<>(
              resourceAssembler.toResources(list),
              linkTo(UserResourceController.class).withSelfRel());
    }

    @GetMapping(path = "{username}")
    public User getUser(@PathVariable("username") String username) throws Exception {
        User user = userController.getUser(username);
        return resourceAssembler.toResource(user);
    }

    @PostMapping
    public void createNewUser(@RequestBody User userDetails) throws Exception {
        userController.createNewUser(userDetails);
    }

    @PutMapping(path = "{username}")
    public void updateUser(@PathVariable("username") String username,
                           @RequestBody User user) throws Exception {
        userController.updateUser(username, user);
    }

    @DeleteMapping(path = "{username}")
    public void deleteUser(@PathVariable("username") String username) throws Exception {
        userController.deleteUser(username);
    }

}
