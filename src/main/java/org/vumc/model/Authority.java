package org.vumc.model;

import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Authority implements GrantedAuthority {
    private String authority;

    public Authority(String authority) {
        this.authority = authority;
    }

    @Override
    @JsonValue
    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }


    public static Authority from(GrantedAuthority grantedAuthority) {
        return new Authority(grantedAuthority.getAuthority());
    }

    public static List<Authority> from(Collection<? extends GrantedAuthority> grantedAuthorities) {
        return grantedAuthorities.stream()
                .map(Authority::from)
                .collect(Collectors.toList());
    }
}
