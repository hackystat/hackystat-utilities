package org.hackystat.utilities.home;

import java.io.File;

/**
 * Provides a utility that returns the desired location of the dot-hackystat directory. 
 * This defaults to the user.home System Property, but can be overridden by the user
 * by providing the property hackystat.user.home.
 * @author Philip Johnson
 *
 */
public class HackystatUserHome {
  
  /**
   * Return a File instance representing the desired location of the .hackystat directory.
   * Note that this directory may or may not exist. 
   * @return A File instance representing the desired user.home directory. 
   */
  public static File getHome() {
    String userHome = System.getProperty("hackystat.user.home", System.getProperty("user.home"));
    return new File(userHome);
  }

}
