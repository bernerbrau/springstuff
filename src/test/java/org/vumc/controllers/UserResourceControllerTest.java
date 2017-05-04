/* Project: continuum
 * File: UserControllerTest.java
 * Created: May 03, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.controllers;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Streams;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.hateoas.Resources;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.vumc.hypermedia.assembler.UserResourceAssembler;
import org.vumc.model.DefinedAuthority;
import org.vumc.model.User;
import org.vumc.users.InMemoryUserDetailsManagerExt;
import org.vumc.users.UserDetailsManagerExt;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserResourceControllerTest
{
  public static final String URL_ROOT = "http://servername";
  public static final String CONTEXT_PATH = "/continuum";
  public static final String PATH_WITHIN_APP = "";
  private UserResourceController controller;

  private UserResourceAssembler assembler;
  private UserController        delegate;
  private UserDetailsManagerExt manager;
  private PasswordEncoder       encoder;

  private HttpServletRequest request;
  private ServletRequestAttributes requestAttributes;

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

    delegate = new UserController(manager, encoder);

    assembler = new UserResourceAssembler();
    controller = new UserResourceController(delegate, assembler);

    request = mock(HttpServletRequest.class);
    when(request.getRequestURL())
        .thenReturn(new StringBuffer(URL_ROOT + CONTEXT_PATH + PATH_WITHIN_APP));
    when(request.getRequestURI())
        .thenReturn(CONTEXT_PATH + PATH_WITHIN_APP);
    when(request.getServletPath())
        .thenReturn(PATH_WITHIN_APP);
    when(request.getContextPath())
        .thenReturn(CONTEXT_PATH);
    when(request.getHeaderNames())
        .thenReturn(Iterators.asEnumeration(Collections.<String>emptySet().iterator()));

    requestAttributes = new ServletRequestAttributes(request);

    RequestContextHolder.setRequestAttributes(requestAttributes);
  }

  @After
  public void cleanUpRequestContext() {
    RequestContextHolder.resetRequestAttributes();
  }

  @Test
  public void getUsersReturnsCollectionAndUsersWithSelfLinks() throws Exception {
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

    Resources<User> users = controller.getUsers();

    assertEquals(URL_ROOT + CONTEXT_PATH + "/api/users", users.getLink("self").getHref());
    assertEquals(2, Iterables.size(users));
    assertTrue(
        Streams.stream(users)
            .anyMatch(u -> u.getUsername().equals("testuser")
                           && u.getLink("self").getHref().equals(URL_ROOT + CONTEXT_PATH + "/api/users/testuser"))
    );
    assertTrue(
        Streams.stream(users)
            .anyMatch(u -> u.getUsername().equals("testadmin")
                           && u.getLink("self").getHref().equals(URL_ROOT + CONTEXT_PATH + "/api/users/testadmin"))
    );
  }

//
//  @Test
//  public void getUserReturnsUserByName() {
//    User user1 = new User(
//       "testuser",
//       encoder.encode("testpass"),
//       Collections.singletonList(DefinedAuthority.PROVIDER));
//    User user2 = new User(
//       "testadmin",
//       encoder.encode("testpass2"),
//       Collections.singletonList(DefinedAuthority.USER_ADMIN));
//
//    manager.createUser(user1);
//    manager.createUser(user2);
//
//    User controllerUser1 = controller.getUser("testuser");
//    User controllerUser2 = controller.getUser("testadmin");
//
//    assertEquals(user1.getUsername(), controllerUser1.getUsername());
//    assertNull(controllerUser1.getPassword());
//    assertEquals(user1.getAuthorities(), controllerUser1.getAuthorities());
//
//    assertEquals(user2.getUsername(), controllerUser2.getUsername());
//    assertNull(controllerUser1.getPassword());
//    assertEquals(user2.getAuthorities(), controllerUser2.getAuthorities());
//  }
//
//  @Test
//  public void getUserWithNonexistentUsernameReturnsNull() {
//    assertNull(controller.getUser("bogus"));
//  }
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
