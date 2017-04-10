package org.vumc.users;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;

import java.util.List;

public interface UserDetailsManagerExt extends UserDetailsManager {
    List<? extends UserDetails> findAllUsers();
}
