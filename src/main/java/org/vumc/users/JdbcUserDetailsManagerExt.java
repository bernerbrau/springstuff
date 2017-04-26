package org.vumc.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Component;
import org.vumc.model.User;

import java.util.List;

@Component
public class JdbcUserDetailsManagerExt extends JdbcUserDetailsManager
    implements UserDetailsManagerExt
{

  private static final String DEF_ALL_USERS_QUERY = "select username, password, enabled from users";

  private String allUsersQuery = DEF_ALL_USERS_QUERY;

  @Autowired
  public JdbcUserDetailsManagerExt(JdbcTemplate template)
  {
    this.setJdbcTemplate(template);
  }

  public void setAllUsersQuery(String allUsersQuery)
  {
    this.allUsersQuery = allUsersQuery;
  }

  @Override
  public List<? extends UserDetails> findAllUsers()
  {
    return getJdbcTemplate().query(this.allUsersQuery,
        (rs, rowNum) ->
        {
          String username = rs.getString(1);
          String password = rs.getString(2);
          boolean enabled = rs.getBoolean(3);
          return User.fromUserDetails(
              new org.springframework.security.core.userdetails.User(
                  username, password, enabled, true, true, true,
                  loadUserAuthorities(username)),
              true);
        });
  }

  @Override
  public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException
  {
    UserDetails details = super.loadUserByUsername(username);
    return User.fromUserDetails(details, true);
  }


}
