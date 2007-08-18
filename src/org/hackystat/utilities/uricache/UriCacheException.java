package org.hackystat.utilities.uricache;

/**
 * A cache operations exception.
 * 
 * @author <a href="mailto:seninp@gmail.com">Pavel Senin<a>
 */
public class UriCacheException extends Exception {

  /**
   * Fancy eclipse serial.
   */
  private static final long serialVersionUID = 1L;

  /**
   * An empty implementation.
   */
  public UriCacheException() {
    assert true;
    // another PMD bogus
  }

  /**
   * Messaged implementatiuon.
   * 
   * @param msg the message.
   */
  public UriCacheException(String msg) {
    super(msg);
  }

  /**
   * The basic exception translator.
   * 
   * @param cause throwable which causes this exception.
   */
  public UriCacheException(Throwable cause) {
    super(cause);
  }

  /**
   * The full format exception.
   * 
   * @param msg the exception message.
   * @param cause throwable which causes this exception.
   */
  public UriCacheException(String msg, Throwable cause) {
    super(msg, cause);
  }

}
