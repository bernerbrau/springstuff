/* Project: continuum
 * File: ProxyPreAuthConfigurer.java
 * Created: Apr 12, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.proxy;

import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails;
import org.springframework.security.web.authentication.preauth.x509.X509AuthenticationFilter;

import javax.servlet.http.HttpServletRequest;

public class ProxyPreAuthConfigurer<H extends HttpSecurityBuilder<H>> extends
    AbstractHttpConfigurer<ProxyPreAuthConfigurer<H>, H>
{
  private ProxyPreAuthenticationFilter authenticationFilter;
  private AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken>
                                       authenticationUserDetailsService;
  private AuthenticationDetailsSource<HttpServletRequest, PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails>
                                       authenticationDetailsSource;

  public ProxyPreAuthConfigurer()
  {
  }

  public ProxyPreAuthConfigurer<H> authenticationFilter(
                                                           ProxyPreAuthenticationFilter authenticationFilter)
  {
    this.authenticationFilter = authenticationFilter;
    return this;
  }

  public ProxyPreAuthConfigurer<H> authenticationDetailsSource(
                                                                  AuthenticationDetailsSource<HttpServletRequest, PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails> authenticationDetailsSource)
  {
    this.authenticationDetailsSource = authenticationDetailsSource;
    return this;
  }

  public ProxyPreAuthConfigurer<H> userDetailsService(UserDetailsService userDetailsService)
  {
    UserDetailsByNameServiceWrapper<PreAuthenticatedAuthenticationToken>
        authenticationUserDetailsService =
        new UserDetailsByNameServiceWrapper<PreAuthenticatedAuthenticationToken>();
    authenticationUserDetailsService.setUserDetailsService(userDetailsService);
    return authenticationUserDetailsService(authenticationUserDetailsService);
  }

  public ProxyPreAuthConfigurer<H> authenticationUserDetailsService(
                                                                       AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> authenticationUserDetailsService)
  {
    this.authenticationUserDetailsService = authenticationUserDetailsService;
    return this;
  }

  // @formatter:off
  @Override
  public void init(H http) throws Exception
  {
    PreAuthenticatedAuthenticationProvider authenticationProvider =
        new PreAuthenticatedAuthenticationProvider();
    authenticationProvider
        .setPreAuthenticatedUserDetailsService(getAuthenticationUserDetailsService(http));

    http
        .authenticationProvider(authenticationProvider)
        .setSharedObject(AuthenticationEntryPoint.class, new Http403ForbiddenEntryPoint());
  }
  // @formatter:on

  @Override
  public void configure(H http) throws Exception
  {
    ProxyPreAuthenticationFilter filter = getFilter(http
                                                        .getSharedObject(
                                                            AuthenticationManager.class));
    http.addFilterAfter(filter, X509AuthenticationFilter.class);
  }

  private ProxyPreAuthenticationFilter getFilter(AuthenticationManager authenticationManager)
  {
    if (authenticationFilter == null)
    {
      authenticationFilter = new ProxyPreAuthenticationFilter(authenticationManager);
      if (authenticationDetailsSource != null)
      {
        authenticationFilter
            .setAuthenticationDetailsSource(authenticationDetailsSource);
      }
      authenticationFilter = postProcess(authenticationFilter);
    }

    return authenticationFilter;
  }

  private AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> getAuthenticationUserDetailsService(H http)
  {
    if (authenticationUserDetailsService == null)
    {
      userDetailsService(http.getSharedObject(UserDetailsService.class));
    }
    return authenticationUserDetailsService;
  }

}