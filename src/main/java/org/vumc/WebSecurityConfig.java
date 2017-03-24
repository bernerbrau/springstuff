/* Project: continuum
 * File: WebSecurityConfig.java
 * Created: Mar 22, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.JdbcUserDetailsManagerConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter
{
  private final Environment environment;
  private JdbcUserDetailsManager userDetailsManager;

  @Autowired
  public WebSecurityConfig(final Environment inEnvironment)
  {
    environment = inEnvironment;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    // TODO - research if we need to keep this!
    http.csrf().disable();
    if (environment.acceptsProfiles("war")) {
      http.headers().frameOptions().sameOrigin();
    } else {
      http.headers().frameOptions().disable();
    }
    http.authorizeRequests()
        .anyRequest()
        .permitAll();
//        .antMatchers("/", "/home").permitAll()
//        .anyRequest().authenticated()
//        .and()
//        .formLogin()
//        .loginPage("/login")
//        .permitAll()
//        .and()
//        .logout();
  }

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth,
                              DataSource dataSource,
                              PasswordEncoder passwordEncoder) throws Exception {
    JdbcUserDetailsManagerConfigurer<AuthenticationManagerBuilder>
        jdbc = auth.jdbcAuthentication().dataSource(dataSource).passwordEncoder(passwordEncoder);

    if (environment.acceptsProfiles("local")) {
      jdbc.withDefaultSchema()
        .withUser("testuser")
        .password("testpass")
        .authorities("*");
    }

    this.userDetailsManager = jdbc.getUserDetailsService();
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

  @Bean
	public UserDetailsManager userDetailsManager() throws Exception {
    return userDetailsManager;
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