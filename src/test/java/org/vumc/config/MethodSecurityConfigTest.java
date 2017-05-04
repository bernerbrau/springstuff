/* Project: continuum
 * File: MethodSecurityConfigTest.java
 * Created: May 04, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.config;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.vumc.model.DefinedAuthority;
import org.vumc.security.annotations.AllowedAuthorities;

import java.util.Collections;

import static org.vumc.security.SecurityTestUtil.withAuthority;

@RunWith(SpringRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class)
public class MethodSecurityConfigTest
{
  @Configuration
  public static class TestConfig extends MethodSecurityConfig {
    @Bean
    public AllowedAuthoritiesTestComponent allowedAuthoritiesTestComponent() {
      return new AllowedAuthoritiesTestComponent();
    }
  }

  public static class AllowedAuthoritiesTestComponent {
    @AllowedAuthorities(DefinedAuthority.PROVIDER)
    void requiresProvider() {}

    @AllowedAuthorities(DefinedAuthority.USER_ADMIN)
    void requiresUserAdmin() {}

    @AllowedAuthorities(DefinedAuthority.PATIENT_SOURCE)
    void requiresPatientSource() {}

    @AllowedAuthorities(DefinedAuthority.AUTHENTICATION_BROKER)
    void requiresAuthenticationBroker() {}

    @AllowedAuthorities({DefinedAuthority.PROVIDER, DefinedAuthority.USER_ADMIN})
    void requiresProviderOrUserAdmin() {}

    @PreAuthorize("hasAuthority('useradmin')")
    @AllowedAuthorities(DefinedAuthority.AUTHENTICATION_BROKER)
    void hasAmbiguousAnnotations() {}

  }

  @Autowired
  private AllowedAuthoritiesTestComponent allowedAuthoritiesTestComponent;

  @Before
  public void setUp() {
    SecurityContextImpl anonymous = new SecurityContextImpl();
    anonymous.setAuthentication(new AnonymousAuthenticationToken("anonymous","anonymous",
      Collections.singletonList(new SimpleGrantedAuthority("anonymous"))));
    SecurityContextHolder.setContext(anonymous);
  }

  @After
  public void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test(expected=AccessDeniedException.class)
  public void methodThatRequiresProviderFailsAnonymously() {
    allowedAuthoritiesTestComponent.requiresProvider();
  }

  @Test(expected=AccessDeniedException.class)
  public void methodThatRequiresUserAdminFailsAnonymously() {
    allowedAuthoritiesTestComponent.requiresUserAdmin();
  }

  @Test(expected=AccessDeniedException.class)
  public void methodThatRequiresPatientSourceFailsAnonymously() {
    allowedAuthoritiesTestComponent.requiresPatientSource();
  }

  @Test(expected=AccessDeniedException.class)
  public void methodThatRequiresAuthenticationBrokerFailsAnonymously() {
    allowedAuthoritiesTestComponent.requiresAuthenticationBroker();
  }

  @Test(expected=AccessDeniedException.class)
  public void methodThatRequiresProviderOrUserAdminFailsAnonymously() {
    allowedAuthoritiesTestComponent.requiresProviderOrUserAdmin();
  }

  @Test
  public void methodThatRequiresProviderSucceedsAsProvider() {
    withAuthority(DefinedAuthority.PROVIDER, () ->
      allowedAuthoritiesTestComponent.requiresProvider());
  }

  @Test
  public void methodThatRequiresUserAdminSucceedsAsUserAdmin() {
    withAuthority(DefinedAuthority.USER_ADMIN, () ->
      allowedAuthoritiesTestComponent.requiresUserAdmin());
  }

  @Test
  public void methodThatRequiresPatientSucceedsAsPatientSource() {
    withAuthority(DefinedAuthority.PATIENT_SOURCE, () ->
      allowedAuthoritiesTestComponent.requiresPatientSource());
  }

  @Test
  public void methodThatRequiresAuthenticationBrokerSucceedsAsAuthenticationBroker() {
    withAuthority(DefinedAuthority.AUTHENTICATION_BROKER, () ->
      allowedAuthoritiesTestComponent.requiresAuthenticationBroker());
  }

  @Test
  public void methodThatRequiresProviderOrUserAdminSucceedsAsProvider() {
    withAuthority(DefinedAuthority.PROVIDER, () ->
      allowedAuthoritiesTestComponent.requiresProviderOrUserAdmin());
  }

  @Test
  public void methodThatRequiresProviderOrUserAdminSucceedsAsUserAdmin() {
    withAuthority(DefinedAuthority.USER_ADMIN, () ->
      allowedAuthoritiesTestComponent.requiresProviderOrUserAdmin());
  }


  @Test(expected=AccessDeniedException.class)
  public void methodThatRequiresProviderFailsAsOtherRole() {
    withAuthority(DefinedAuthority.USER_ADMIN, () ->
      allowedAuthoritiesTestComponent.requiresProvider());
  }

  @Test(expected=AccessDeniedException.class)
  public void methodThatRequiresUserAdminFailsAsOtherRole() {
    withAuthority(DefinedAuthority.PROVIDER, () ->
      allowedAuthoritiesTestComponent.requiresUserAdmin());
  }

  @Test(expected=AccessDeniedException.class)
  public void methodThatRequiresPatientFailsAsOtherRole() {
    withAuthority(DefinedAuthority.USER_ADMIN, () ->
      allowedAuthoritiesTestComponent.requiresPatientSource());
  }

  @Test(expected=AccessDeniedException.class)
  public void methodThatRequiresAuthenticationBrokerFailsAsOtherRole() {
    withAuthority(DefinedAuthority.USER_ADMIN, () ->
      allowedAuthoritiesTestComponent.requiresAuthenticationBroker());
  }

  @Test(expected=AccessDeniedException.class)
  public void methodThatRequiresProviderOrUserAdminFailsAsOtherRole() {
    withAuthority(DefinedAuthority.PATIENT_SOURCE, () ->
      allowedAuthoritiesTestComponent.requiresProviderOrUserAdmin());
  }

  @Test(expected=AccessDeniedException.class)
  public void methodWithAmbiguousAnnotationsIgnoresPreAuthorize1() {
    allowedAuthoritiesTestComponent.hasAmbiguousAnnotations();
  }
  @Test
  public void methodWithAmbiguousAnnotationsIgnoresPreAuthorize2() {
    withAuthority(DefinedAuthority.AUTHENTICATION_BROKER, () ->
      allowedAuthoritiesTestComponent.hasAmbiguousAnnotations());
  }
  @Test(expected=AccessDeniedException.class)
  public void methodWithAmbiguousAnnotationsIgnoresPreAuthorize3() {
    withAuthority(DefinedAuthority.USER_ADMIN, () ->
      allowedAuthoritiesTestComponent.hasAmbiguousAnnotations());
  }


}
