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
import org.vumc.users.UserDetailsManagerExt;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class UserControllerTest
{
  private UserController controller;

  private UserDetailsManagerExt manager;
  private PasswordEncoder       encoder;

  @Before
  public void setUp() {
    Map<String,User> users = new LinkedHashMap<>();

    manager = new UserDetailsManagerExt()
    {
      @Override
      public List<? extends UserDetails> findAllUsers()
      {
        return new ArrayList<>(users.values());
      }

      @Override
      public void createUser(final UserDetails user)
      {
        users.put(user.getUsername(), User.fromUserDetails(user,true));
      }

      @Override
      public void updateUser(final UserDetails user)
      {
        users.put(user.getUsername(), User.fromUserDetails(user,true));
      }

      @Override
      public void deleteUser(final String username)
      {
        users.remove(username);
      }

      @Override
      public void changePassword(final String oldPassword, final String newPassword)
      {
        throw new UnsupportedOperationException("changePassword is not implemented");
      }

      @Override
      public boolean userExists(final String username)
      {
        return users.containsKey(username);
      }

      @Override
      public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException
      {
        return users.get(username);
      }
    };

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

    User mgrUser = (User)manager.loadUserByUsername("testuser");
    assertEquals(user.getUsername(), mgrUser.getUsername());
    assertTrue(encoder.matches("testpass",mgrUser.getPassword()));
    assertEquals(user.getAuthorities(), mgrUser.getAuthorities());
    assertTrue(user.isEnabled());
  }

}
