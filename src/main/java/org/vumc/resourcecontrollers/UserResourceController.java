/**
 * Created by yarbrojl on 3/14/2017.
 */

package org.vumc.resourcecontrollers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.vumc.resourcecontrollers.model.UserNameAndPassword;

import java.util.Collections;

@RestController
@RequestMapping("/api/users")
public class UserResourceController
{

    private final AuthenticationManager authManager;
    private final UserDetailsManager userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserResourceController(AuthenticationManager authManager,
                                  UserDetailsManager userService,
                                  PasswordEncoder passwordEncoder) {
        this.authManager = authManager;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @RequestMapping(value="/authenticate", method=RequestMethod.POST)
    public ResponseEntity<UserDetails> authenticate(@RequestBody UserNameAndPassword req) {
        // needs to return a JWT token in the response
        try {
            Authentication auth =
                authManager.authenticate(new UsernamePasswordAuthenticationToken(req.username, req.password));
            return new ResponseEntity<>(userService.loadUserByUsername(req.username), HttpStatus.OK);
        } catch (AuthenticationException ex) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @RequestMapping(value="/register", method=RequestMethod.POST)
    public ResponseEntity<Void> register(@RequestBody UserNameAndPassword req) {
        try {
            userService.createUser(new User(req.username, passwordEncoder.encode(req.password),
                                               Collections.singleton(new SimpleGrantedAuthority("*"))));
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch(Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
