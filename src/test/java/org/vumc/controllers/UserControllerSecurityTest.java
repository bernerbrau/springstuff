/* Project: continuum
 * File: UserControllerTest.java
 * Created: May 03, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.controllers;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.vumc.config.MethodSecurityConfig;
import org.vumc.model.DefinedAuthority;
import org.vumc.model.User;
import org.vumc.users.InMemoryUserDetailsManagerExt;
import org.vumc.users.UserDetailsManagerExt;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.vumc.security.SecurityTestUtil.withAuthority;

@RunWith(SpringRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserControllerSecurityTest
{
  @Configuration
  public static class TestConfig extends MethodSecurityConfig {

    @Bean
    public UserController userController() {
      return new UserController(manager(), encoder());
    }

    @Bean
    public UserDetailsManagerExt manager() {
      return new InMemoryUserDetailsManagerExt();
    }

    @Bean
    public PasswordEncoder encoder() {
      return new PasswordEncoder()
      {
        @Override
        public String encode(final CharSequence rawPassword)
        {
          return String.format("ENCODED(%s)", rawPassword);
        }

        @Override
        public boolean matches(final CharSequence rawPassword, final String encodedPassword)
        {
          return encode(rawPassword).equals(encodedPassword);
        }
      };
    }
  }

  @Autowired
  private UserController controller;
  @Autowired
  private UserDetailsManagerExt manager;
  @Autowired
  private PasswordEncoder encoder;

  @Before
  public void setUp() {
    SecurityContextImpl anonymous = new SecurityContextImpl();
    anonymous.setAuthentication(new AnonymousAuthenticationToken("anonymous","anonymous",Collections.singletonList(new SimpleGrantedAuthority("anonymous"))));
    SecurityContextHolder.setContext(anonymous);
  }

  @After
  public void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test(expected = AccessDeniedException.class)
  public void getUsersAnonymouslyThrowsAccessDenied() {
    controller.getUsers();
  }

  @Test(expected = AccessDeniedException.class)
  public void getUsersAsProviderThrowsAccessDenied() {
    withAuthority(DefinedAuthority.PROVIDER, () ->
      controller.getUsers()
    );
  }

  @Test(expected = AccessDeniedException.class)
  public void getUsersAsPatientSourceThrowsAccessDenied() {
    withAuthority(DefinedAuthority.PATIENT_SOURCE, () ->
      controller.getUsers()
    );
  }

  @Test(expected = AccessDeniedException.class)
  public void getUsersAsAuthBrokerThrowsAccessDenied() {
    withAuthority(DefinedAuthority.AUTHENTICATION_BROKER, () ->
      controller.getUsers()
    );
  }

  @Test
  public void getUsersOnlyReturnsAdminAssignableUsers() {
    for(DefinedAuthority authority : DefinedAuthority.values()) {
      manager.createUser(
          new User(
             authority.name(),
             "0",
             Collections.singletonList(authority)));
    }

    List<User> users = withAuthority(DefinedAuthority.USER_ADMIN, () -> controller.getUsers());

    assertFalse(users.isEmpty());

    assertTrue(
        users.stream()
            .map(User::getAuthorities)
            .flatMap(Collection::stream)
            .allMatch(DefinedAuthority::isAssignableByUserAdmin));
  }

  @Test
  public void getUserReturnsUserByName() {
    User user = new User(
       "testuser",
       encoder.encode("testpass"),
       Collections.singletonList(DefinedAuthority.PROVIDER));

    manager.createUser(user);

    User controllerUser = withAuthority(DefinedAuthority.USER_ADMIN, () -> controller.getUser("testuser"));

    assertEquals("testuser", controllerUser.getUsername());
  }

  @Test(expected = AccessDeniedException.class)
  public void getUserThrowsAccessDeniedIfAnonymous() {
    User user = new User(
      "testuser",
      encoder.encode("testpass"),
      Collections.singletonList(DefinedAuthority.PROVIDER));

    manager.createUser(user);

    controller.getUser("testuser");
  }

  @Test(expected=AccessDeniedException.class)
  public void getUserThrowsAccessDeniedIfUserIsNotAdminAssignable() {
    User user = new User(
      "testuser",
      "0",
      Collections.singletonList(DefinedAuthority.AUTHENTICATION_BROKER));

    manager.createUser(user);

    withAuthority(DefinedAuthority.USER_ADMIN, () -> controller.getUser("testuser"));
  }

  @Test
  public void getUserWithNonexistentUsernameReturnsNull() {
    assertNull(withAuthority(DefinedAuthority.USER_ADMIN, () ->
       controller.getUser("bogus")
    ));
  }


//
//  @Test
//  public void createUserCreatesEnabledUserWithEncodedPassword() {
//    User user =
//        new User("testuser", "testpass", Collections.singletonList(DefinedAuthority.PROVIDER));
//    controller.createNewUser(user);
//
//    UserDetails mgrUser = manager.loadUserByUsername("testuser");
//    assertEquals(user.getUsername(), mgrUser.getUsername());
//    assertTrue(encoder.matches("testpass",mgrUser.getPassword()));
//    assertEquals(user.getAuthorities(), DefinedAuthority.from(mgrUser.getAuthorities()));
//    assertTrue(mgrUser.isEnabled());
//
//    assertTrue(manager.userExists("testuser"));
//  }
//
//  @Test
//  public void updateUserUpdatesUserFields() {
//    User user =
//        new User("testuser", encoder.encode("testpass"), Collections.singletonList(DefinedAuthority.PROVIDER));
//    manager.createUser(user);
//
//    User updated =
//        new User("testuser", "testpass2", Collections.singletonList(DefinedAuthority.USER_ADMIN));
//    updated.setEnabled(false);
//    controller.updateUser("testuser", updated);
//
//    UserDetails mgrUser = manager.loadUserByUsername("testuser");
//    assertEquals(updated.getUsername(), mgrUser.getUsername());
//    assertTrue(encoder.matches("testpass2",mgrUser.getPassword()));
//    assertEquals(updated.getAuthorities(), DefinedAuthority.from(mgrUser.getAuthorities()));
//    assertFalse(mgrUser.isEnabled());
//  }
//
//  @Test
//  public void updateUserPreservesPasswordIfNull() {
//    User user =
//        new User("testuser", encoder.encode("testpass"), Collections.singletonList(DefinedAuthority.PROVIDER));
//    manager.createUser(user);
//
//    User updated =
//        new User("testuser", null, Collections.singletonList(DefinedAuthority.USER_ADMIN));
//    updated.setEnabled(false);
//    controller.updateUser("testuser", updated);
//
//    UserDetails mgrUser = manager.loadUserByUsername("testuser");
//    assertTrue(encoder.matches("testpass",mgrUser.getPassword()));
//  }
//
//  @Test(expected=IllegalArgumentException.class)
//  public void updateUserThrowsExceptionIfUsernameChanged() {
//    User user =
//        new User("testuser", encoder.encode("testpass"), Collections.singletonList(DefinedAuthority.PROVIDER));
//    manager.createUser(user);
//
//    User updated =
//        new User("testuser2", null, Collections.singletonList(DefinedAuthority.USER_ADMIN));
//    updated.setEnabled(false);
//    controller.updateUser("testuser", updated);
//  }
//
//
//  @Test(expected=UsernameNotFoundException.class)
//  public void updateUserThrowsExceptionIfUserDoesNotExist() {
//    User user =
//        new User("testuser", encoder.encode("testpass"), Collections.singletonList(DefinedAuthority.PROVIDER));
//    controller.updateUser("testuser", user);
//  }
//
//  @Test
//  public void deleteUserRemovesUser() {
//    User user =
//        new User("testuser", encoder.encode("testpass"), Collections.singletonList(DefinedAuthority.PROVIDER));
//    manager.createUser(user);
//
//    controller.deleteUser("testuser");
//
//    assertFalse(manager.userExists("testuser"));
//  }
//
//  @Test(expected=UsernameNotFoundException.class)
//  public void deleteUserThrowsExceptionIfUserDoesNotExist() {
//    controller.deleteUser("testuser");
//  }

}
