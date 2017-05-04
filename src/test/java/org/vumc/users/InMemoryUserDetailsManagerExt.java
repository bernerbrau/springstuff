/* Project: continuum
 * File: InMemoryUserDetailsManagerExt.java
 * Created: May 04, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.users;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InMemoryUserDetailsManagerExt
    extends InMemoryUserDetailsManager
    implements UserDetailsManagerExt
{
  @Override
  public List<? extends UserDetails> findAllUsers()
  {
    try
    {
      Field f = getClass().getSuperclass().getDeclaredField("users");
      f.setAccessible(true);
      return new ArrayList<UserDetails>((
         (Map<String,? extends UserDetails>)f.get(this)
      ).values());
    }
    catch (NoSuchFieldException | IllegalAccessException e)
    {
      throw new RuntimeException(e);
    }
  }
}
