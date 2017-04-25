package org.vumc.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

public class DefinedAuthorityTest
{
  @Test
  public void invalidAuthorityStringThrowsException()
  {
    try
    {
      DefinedAuthority.fromString("bogus");
      fail("expected exception to be thrown");
    }
    catch (IllegalArgumentException e)
    {
      assertEquals("Undefined authority: bogus", e.getMessage());
    }
  }

  @Test
  public void validAuthorityStringFindsEnum()
  {
    assertEquals(DefinedAuthority.USER_ADMIN, DefinedAuthority.fromString("useradmin"));
  }

}