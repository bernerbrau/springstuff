/* Project: continuum
 * File: UsernameAndPassword.java
 * Created: Mar 30, 2017
 * Author: Derek Berner - derek.c.berner@vanderbilt.edu
 *
 * This code is copyright (c) 2017 Vanderbilt University Medical Center
 */
package org.vumc.model;

public class UsernameAndPassword
{
  private String username;
  private String password;

  public UsernameAndPassword()
  {
  }

  public UsernameAndPassword(final String inUsername, final String inPassword)
  {
    username = inUsername;
    password = inPassword;
  }

  public String getUsername()
  {
    return username;
  }

  public void setUsername(final String inUsername)
  {
    username = inUsername;
  }

  public String getPassword()
  {
    return password;
  }

  public void setPassword(final String inPassword)
  {
    password = inPassword;
  }
}
