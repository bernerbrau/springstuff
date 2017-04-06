/* Project: continuum
 * File: WebSecurityConfig.java
 * Created: Mar 22, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.JdbcUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.cache.SpringCacheBasedUserCache;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.vumc.jwt.JWTSecurityContextRepository;

import javax.sql.DataSource;


@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, proxyTargetClass = true)
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter
{
  private final Environment                       environment;
  private final JWTSecurityContextRepository      jwtRepo;
  private       JdbcUserDetailsManager            userDetailsManager;

  @Autowired
  public WebSecurityConfig(final Environment inEnvironment,
                           final JWTSecurityContextRepository jwtRepo)
  {
    environment = inEnvironment;
    this.jwtRepo = jwtRepo;
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

    http.sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    http.authorizeRequests()
        .antMatchers(HttpMethod.OPTIONS).permitAll()
        .antMatchers(
            // Angular2 compiled files
            "/index.html",
            "/*.js",

            // Angular2 routes
            "/",
            "/login",

            // Public portions of the API
            "/api",
            "/api/login",
            "/api/profile",
            "/api/profile/**",

            // Static files
            "**/*.html",
            "**/*.css",
            "**/*.js",
            "/public/**",
            "favicon.ico",

//      TODO x509 client authentication
            "/api/patients/c32",

            // Websockets are authenticated within the STOMP protocol
            "/stomp/**"
        ).permitAll()
        .anyRequest().authenticated();

    http.x509().subjectPrincipalRegex("CN=(.*?),");
    http.anonymous().authorities("anon");

    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    http.securityContext().securityContextRepository(jwtRepo);

    http.exceptionHandling().authenticationEntryPoint((req, res, e) ->
    {
      if (req.getServletPath().startsWith("/api"))
      {
        res.setStatus(HttpStatus.UNAUTHORIZED.value());
      }
    });
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
          .authorities("provider")
        .and()
        .withUser("vaadevmessaging.orionhealthcloud.com")
          .password("")
          .authorities("patientsource");
    }

    this.userDetailsManager = jdbc.getUserDetailsService();

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