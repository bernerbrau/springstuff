package org.vumc.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
public class User implements UserDetails {

    private Collection<DefinedAuthority> authorities;
    private String password;
    private String username;
    private boolean isAccountNonExpired;
    private boolean isAccountNonLocked;
    private boolean isCredentialsNonExpired;
    private boolean isEnabled;

    public void setAuthorities(Collection<DefinedAuthority> authorities) {
        this.authorities = authorities;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        isAccountNonExpired = accountNonExpired;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        isAccountNonLocked = accountNonLocked;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        isCredentialsNonExpired = credentialsNonExpired;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    @Override
    public Collection<DefinedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return isAccountNonExpired;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    public static User fromUserDetails(UserDetails userDetails, boolean copyPassword) {
        User user = new User();
        user.setUsername(userDetails.getUsername());
        if (copyPassword) {
            user.setPassword(userDetails.getPassword());
        }
        user.setAuthorities(DefinedAuthority.from(userDetails.getAuthorities()));
        user.setEnabled(userDetails.isEnabled());
        user.setAccountNonExpired(userDetails.isAccountNonExpired());
        user.setAccountNonLocked(userDetails.isAccountNonLocked());
        user.setCredentialsNonExpired(userDetails.isCredentialsNonExpired());
        return user;
    }

    @JsonIgnore
    public boolean isConfigurableByUserAdmin() {
        return authorities.stream()
            .allMatch(DefinedAuthority::isAssignableByUserAdmin);
    }
}
