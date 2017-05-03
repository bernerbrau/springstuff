package org.vumc.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
public class User extends ResourceSupport implements UserDetails
{
  private Collection<DefinedAuthority> authorities;
  private String                       password;
  private String                       username;
  private boolean                      isAccountNonExpired;
  private boolean                      isAccountNonLocked;
  private boolean                      isCredentialsNonExpired;
  private boolean                      isEnabled;

  public User() {}

  public User(final String inUsername,
              final String inPassword,
              final Collection<DefinedAuthority> inAuthorities)
  {
    authorities = inAuthorities;
    password = inPassword;
    username = inUsername;
    isEnabled = true;
  }

  public static User fromUserDetails(UserDetails userDetails, boolean copyPassword)
  {
    User user = new User();
    user.setUsername(userDetails.getUsername());
    if (copyPassword)
    {
      user.setPassword(userDetails.getPassword());
    }
    user.setAuthorities(DefinedAuthority.from(userDetails.getAuthorities()));
    user.setEnabled(userDetails.isEnabled());
    user.setAccountNonExpired(userDetails.isAccountNonExpired());
    user.setAccountNonLocked(userDetails.isAccountNonLocked());
    user.setCredentialsNonExpired(userDetails.isCredentialsNonExpired());
    return user;
  }

  @Override
  public Collection<DefinedAuthority> getAuthorities()
  {
    return authorities;
  }

  public void setAuthorities(Collection<DefinedAuthority> authorities)
  {
    this.authorities = authorities;
  }

  @Override
  public String getPassword()
  {
    return password;
  }

  public void setPassword(String password)
  {
    this.password = password;
  }

  @Override
  public String getUsername()
  {
    return username;
  }

  public void setUsername(String username)
  {
    this.username = username;
  }

  @Override
  @JsonIgnore
  public boolean isAccountNonExpired()
  {
    return isAccountNonExpired;
  }

  public void setAccountNonExpired(boolean accountNonExpired)
  {
    isAccountNonExpired = accountNonExpired;
  }

  @Override
  @JsonIgnore
  public boolean isAccountNonLocked()
  {
    return isAccountNonLocked;
  }

  public void setAccountNonLocked(boolean accountNonLocked)
  {
    isAccountNonLocked = accountNonLocked;
  }

  @Override
  @JsonIgnore
  public boolean isCredentialsNonExpired()
  {
    return isCredentialsNonExpired;
  }

  public void setCredentialsNonExpired(boolean credentialsNonExpired)
  {
    isCredentialsNonExpired = credentialsNonExpired;
  }

  @Override
  public boolean isEnabled()
  {
    return isEnabled;
  }

  public void setEnabled(boolean enabled)
  {
    isEnabled = enabled;
  }

  @JsonIgnore
  public boolean isConfigurableByUserAdmin()
  {
    return !authorities.isEmpty() && authorities.stream()
                                         .allMatch(DefinedAuthority::isAssignableByUserAdmin);
  }
}
