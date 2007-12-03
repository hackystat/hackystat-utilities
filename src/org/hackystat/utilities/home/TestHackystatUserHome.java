package org.hackystat.utilities.home;

import static org.junit.Assert.assertEquals;

import java.io.File;
import org.junit.Test;

/**
 * Tests the HackystatUserHome class
 * 
 * @author Philip Johnson
 */

public class TestHackystatUserHome {

  /**
   * Tests the hackystat user home definition facility.
   * 
   */
  @Test
  public void testHome() {
    String hackystatUserHome = System.getProperty("hackystat.user.home");
    File home = HackystatUserHome.getHome();
    
    if (hackystatUserHome == null) {
      File userHomeFile = new File(System.getProperty("user.home"));
      assertEquals ("Checking default home", userHomeFile, home);
    }
    else {
      File userHomeFile = new File(System.getProperty("hackystat.user.home"));
      assertEquals ("Checking overridden home", userHomeFile, home);
      
    }
  }
}
