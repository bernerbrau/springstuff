/* Project: continuum
 * File: UserControllerTest.java
 * Created: May 03, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.controllers;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.vumc.model.DefinedAuthority;
import org.vumc.model.User;
import org.vumc.users.InMemoryUserDetailsManagerExt;
import org.vumc.users.UserDetailsManagerExt;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class UserControllerTest
{
  private UserController controller;

  private UserDetailsManagerExt manager;
  private PasswordEncoder       encoder;

  @Before
  public void setUp() {
    manager = new InMemoryUserDetailsManagerExt();

    encoder = new PasswordEncoder()
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

    controller = new UserController(manager, encoder);
  }

  @Test
  public void getUsersReturnsAllUsers() {
    User user1 = new User(
        "testuser",
        encoder.encode("testpass"),
        Collections.singletonList(DefinedAuthority.PROVIDER));
    User user2 = new User(
        "testadmin",
        encoder.encode("testpass2"),
        Collections.singletonList(DefinedAuthority.USER_ADMIN));

    manager.createUser(user1);
    manager.createUser(user2);

    List<User> users = controller.getUsers();

    assertEquals(2, users.size());
    assertTrue(
      users.stream().anyMatch(u ->
         u.getUsername().equals(user1.getUsername())
         && u.getPassword() == null
         && u.getAuthorities().equals(user1.getAuthorities())));
    assertTrue(
      users.stream().anyMatch(u ->
        u.getUsername().equals(user2.getUsername())
        && u.getPassword() == null
        && u.getAuthorities().equals(user2.getAuthorities())));
  }

  @Test
  public void getUserReturnsUserByName() {
    User user1 = new User(
       "testuser",
       encoder.encode("testpass"),
       Collections.singletonList(DefinedAuthority.PROVIDER));
    User user2 = new User(
       "testadmin",
       encoder.encode("testpass2"),
       Collections.singletonList(DefinedAuthority.USER_ADMIN));

    manager.createUser(user1);
    manager.createUser(user2);

    User controllerUser1 = controller.getUser("testuser");
    User controllerUser2 = controller.getUser("testadmin");

    assertEquals(user1.getUsername(), controllerUser1.getUsername());
    assertNull(controllerUser1.getPassword());
    assertEquals(user1.getAuthorities(), controllerUser1.getAuthorities());

    assertEquals(user2.getUsername(), controllerUser2.getUsername());
    assertNull(controllerUser1.getPassword());
    assertEquals(user2.getAuthorities(), controllerUser2.getAuthorities());
  }

  @Test
  public void getUserWithNonexistentUsernameReturnsNull() {
    assertNull(controller.getUser("bogus"));
  }

  @Test
  public void createUserCreatesEnabledUserWithEncodedPassword() {
    User user =
        new User("testuser", "testpass", Collections.singletonList(DefinedAuthority.PROVIDER));
    controller.createNewUser(user);

    UserDetails mgrUser = manager.loadUserByUsername("testuser");
    assertEquals(user.getUsername(), mgrUser.getUsername());
    assertTrue(encoder.matches("testpass",mgrUser.getPassword()));
    assertEquals(user.getAuthorities(), DefinedAuthority.from(mgrUser.getAuthorities()));
    assertTrue(mgrUser.isEnabled());

    assertTrue(manager.userExists("testuser"));
  }

  @Test
  public void updateUserUpdatesUserFields() {
    User user =
        new User("testuser", encoder.encode("testpass"), Collections.singletonList(DefinedAuthority.PROVIDER));
    manager.createUser(user);

    User updated =
        new User("testuser", "testpass2", Collections.singletonList(DefinedAuthority.USER_ADMIN));
    updated.setEnabled(false);
    controller.updateUser("testuser", updated);

    UserDetails mgrUser = manager.loadUserByUsername("testuser");
    assertEquals(updated.getUsername(), mgrUser.getUsername());
    assertTrue(encoder.matches("testpass2",mgrUser.getPassword()));
    assertEquals(updated.getAuthorities(), DefinedAuthority.from(mgrUser.getAuthorities()));
    assertFalse(mgrUser.isEnabled());
  }

  @Test
  public void updateUserPreservesPasswordIfNull() {
    User user =
        new User("testuser", encoder.encode("testpass"), Collections.singletonList(DefinedAuthority.PROVIDER));
    manager.createUser(user);

    User updated =
        new User("testuser", null, Collections.singletonList(DefinedAuthority.USER_ADMIN));
    updated.setEnabled(false);
    controller.updateUser("testuser", updated);

    UserDetails mgrUser = manager.loadUserByUsername("testuser");
    assertTrue(encoder.matches("testpass",mgrUser.getPassword()));
  }

  @Test(expected=IllegalArgumentException.class)
  public void updateUserThrowsExceptionIfUsernameChanged() {
    User user =
        new User("testuser", encoder.encode("testpass"), Collections.singletonList(DefinedAuthority.PROVIDER));
    manager.createUser(user);

    User updated =
        new User("testuser2", null, Collections.singletonList(DefinedAuthority.USER_ADMIN));
    updated.setEnabled(false);
    controller.updateUser("testuser", updated);
  }


  @Test(expected=UsernameNotFoundException.class)
  public void updateUserThrowsExceptionIfUserDoesNotExist() {
    User user =
        new User("testuser", encoder.encode("testpass"), Collections.singletonList(DefinedAuthority.PROVIDER));
    controller.updateUser("testuser", user);
  }

  @Test
  public void deleteUserRemovesUser() {
    User user =
        new User("testuser", encoder.encode("testpass"), Collections.singletonList(DefinedAuthority.PROVIDER));
    manager.createUser(user);

    controller.deleteUser("testuser");

    assertFalse(manager.userExists("testuser"));
  }

  @Test(expected=UsernameNotFoundException.class)
  public void deleteUserThrowsExceptionIfUserDoesNotExist() {
    controller.deleteUser("testuser");
  }

}
