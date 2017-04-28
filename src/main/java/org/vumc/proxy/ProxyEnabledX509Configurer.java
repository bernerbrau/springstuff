/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vumc.proxy;

import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails;
import org.springframework.security.web.authentication.preauth.x509.SubjectDnX509PrincipalExtractor;

import javax.servlet.http.HttpServletRequest;

public final class ProxyEnabledX509Configurer<H extends HttpSecurityBuilder<H>> extends
		AbstractHttpConfigurer<ProxyEnabledX509Configurer<H>, H> {
	private ProxyEnabledX509AuthenticationFilter                                                                       x509AuthenticationFilter;
	private AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken>                                      authenticationUserDetailsService;
	private AuthenticationDetailsSource<HttpServletRequest, PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails>authenticationDetailsSource;

	private String           subjectPrincipalRegex;
	private String           subjectDNProxyHeader;
	private GrantedAuthority brokerAuthority;

	public ProxyEnabledX509Configurer() {
	}

	public ProxyEnabledX509Configurer<H> x509AuthenticationFilter(ProxyEnabledX509AuthenticationFilter x509AuthenticationFilter) {
		this.x509AuthenticationFilter = x509AuthenticationFilter;
		return this;
	}

	public ProxyEnabledX509Configurer<H> authenticationDetailsSource(
			AuthenticationDetailsSource<HttpServletRequest, PreAuthenticatedGrantedAuthoritiesWebAuthenticationDetails> authenticationDetailsSource) {
		this.authenticationDetailsSource = authenticationDetailsSource;
		return this;
	}

	public ProxyEnabledX509Configurer<H> userDetailsService(UserDetailsService userDetailsService) {
		UserDetailsByNameServiceWrapper<PreAuthenticatedAuthenticationToken> authenticationUserDetailsService = new UserDetailsByNameServiceWrapper<PreAuthenticatedAuthenticationToken>();
		authenticationUserDetailsService.setUserDetailsService(userDetailsService);
		return authenticationUserDetailsService(authenticationUserDetailsService);
	}

	public ProxyEnabledX509Configurer<H> authenticationUserDetailsService(
			AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> authenticationUserDetailsService) {
		this.authenticationUserDetailsService = authenticationUserDetailsService;
		return this;
	}

	public ProxyEnabledX509Configurer<H> subjectPrincipalRegex(String subjectPrincipalRegex) {
		this.subjectPrincipalRegex = subjectPrincipalRegex;
		return this;
	}

	public ProxyEnabledX509Configurer<H> subjectDNProxyHeader(String subjectDNProxyHeader) {
		this.subjectDNProxyHeader = subjectDNProxyHeader;
		return this;
	}

	public ProxyEnabledX509Configurer<H> brokerAuthority(GrantedAuthority brokerAuthority) {
		this.brokerAuthority = brokerAuthority;
		return this;
	}

	@Override
	public void init(H http) throws Exception {
		PreAuthenticatedAuthenticationProvider authenticationProvider = new PreAuthenticatedAuthenticationProvider();
		authenticationProvider.setPreAuthenticatedUserDetailsService(getAuthenticationUserDetailsService(http));

		http
			.authenticationProvider(authenticationProvider)
			.setSharedObject(AuthenticationEntryPoint.class,new Http403ForbiddenEntryPoint());
	}

	@Override
	public void configure(H http) throws Exception {
		ProxyEnabledX509AuthenticationFilter filter = getFilter(http
				.getSharedObject(AuthenticationManager.class));
		http.addFilter(filter);
	}

	private ProxyEnabledX509AuthenticationFilter getFilter(AuthenticationManager authenticationManager) {
		if (x509AuthenticationFilter == null) {
			x509AuthenticationFilter = new ProxyEnabledX509AuthenticationFilter(
					subjectDNProxyHeader,
					subjectPrincipalRegex,
					brokerAuthority);
			x509AuthenticationFilter.setAuthenticationManager(authenticationManager);
			if (subjectPrincipalRegex != null) {
				SubjectDnX509PrincipalExtractor principalExtractor = new SubjectDnX509PrincipalExtractor();
				principalExtractor.setSubjectDnRegex(subjectPrincipalRegex);
				x509AuthenticationFilter.setPrincipalExtractor(principalExtractor);
			}
			if (authenticationDetailsSource != null) {
				x509AuthenticationFilter
						.setAuthenticationDetailsSource(authenticationDetailsSource);
			}
			x509AuthenticationFilter = postProcess(x509AuthenticationFilter);
		}

		return x509AuthenticationFilter;
	}

	private AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> getAuthenticationUserDetailsService(
			H http) {
		if (authenticationUserDetailsService == null) {
			userDetailsService(http.getSharedObject(UserDetailsService.class));
		}
		return authenticationUserDetailsService;
	}

}