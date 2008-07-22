package org.hackystat.utilities.email;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests the ValidateEmailSyntax class. 
 * 
 * @author Philip Johnson
 */

public class TestValidateEmailSyntax {

  /**
   * Tests the hackystat user home definition facility.
   * 
   */
  @Test
  public void testEmail() {
    assertTrue("Testing valid email", ValidateEmailSyntax.isValid("johnson@hawaii.edu"));
    assertFalse("Testing invalid email", ValidateEmailSyntax.isValid("foo"));
  }
}
