/* Project: continuum
 * File: WebSecurityConfig.java
 * Created: Mar 22, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.JdbcUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.cache.SpringCacheBasedUserCache;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.vumc.jwt.JWTAuthenticationFilter;
import org.vumc.jwt.JWTAuthenticationProvider;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter
{
  private final Environment                  environment;
  private final JWTAuthenticationProvider    jwtProvider;
  private final JWTAuthenticationFilter      jwtFilter;
  private       JdbcUserDetailsManager       userDetailsManager;

  @Autowired
  public WebSecurityConfig(final Environment inEnvironment,
                           final JWTAuthenticationProvider jwtProvider,
                           final JWTAuthenticationFilter jwtFilter)
  {
    environment = inEnvironment;
    this.jwtProvider = jwtProvider;
    this.jwtFilter = jwtFilter;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    // Don't need CSRF due to JWT security
    http.csrf().disable();

    if (environment.acceptsProfiles("war")) {
      http.headers().frameOptions().sameOrigin();
    } else {
      http.headers().frameOptions().disable();
    }

    http.authorizeRequests()
        .antMatchers(
            "/",
            "/api/users/authenticate",
            "**/*.html",
            "**/*.css",
            "**/*.js",
            "/public/**",
            "/stomp/**",
            "favicon.ico").permitAll()
        .antMatchers(HttpMethod.POST, "/api/patients/c32").hasAnyAuthority("patientSource","*")
        .anyRequest().hasAnyAuthority("provider","*");

    http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    http.addFilterAfter(new AnonymousAuthenticationFilter("anon"),
        UsernamePasswordAuthenticationFilter.class);
  }

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurerAdapter() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        if (!environment.acceptsProfiles("war")) {
          registry.addMapping("/api/**");
        }
      }
    };
  }

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth,
                              DataSource dataSource,
                              PasswordEncoder passwordEncoder,
                              UserCache userCache) throws Exception
  {
    JdbcUserDetailsManagerConfigurer<AuthenticationManagerBuilder>
        jdbc = auth.jdbcAuthentication()
                   .dataSource(dataSource)
                   .passwordEncoder(passwordEncoder)
                   .userCache(userCache);

    if (environment.acceptsProfiles("local")) {
      jdbc.withDefaultSchema()
        .withUser("testuser")
          .password(passwordEncoder.encode("testpass"))
        .authorities("provider");
    }

    this.userDetailsManager = jdbc.getUserDetailsService();

    auth.authenticationProvider(jwtProvider);
  }

  @Bean
  public UserDetailsManager userDetailsManager()
  {
    return userDetailsManager;
	}

	@Bean
  public UserCache userCache() throws Exception
  {
    return new SpringCacheBasedUserCache(new ConcurrentMapCache("users"));
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    if (environment.acceptsProfiles("war")) {
      return new BCryptPasswordEncoder(11);
    } else {
      return new BCryptPasswordEncoder(4);
    }
  }

}