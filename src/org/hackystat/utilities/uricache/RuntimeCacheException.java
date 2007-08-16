package org.hackystat.utilities.uricache;

/**
 * A cache operations exception.
 * 
 * @author <a href="mailto:seninp@gmail.com">Pavel Senin<a>
 */
public class RuntimeCacheException extends Exception {

  /**
   * Fancy eclipse serial.
   */
  private static final long serialVersionUID = 1L;

  /**
   * An empty implementation.
   */
  public RuntimeCacheException() {
    assert true;
    // another PMD bogus
  }

  /**
   * Messaged implementatiuon.
   * 
   * @param msg the message.
   */
  public RuntimeCacheException(String msg) {
    super(msg);
  }

  /**
   * The basic exception translator.
   * 
   * @param cause throwable which causes this exception.
   */
  public RuntimeCacheException(Throwable cause) {
    super(cause);
  }

  /**
   * The full format exception.
   * 
   * @param msg the exception message.
   * @param cause throwable which causes this exception.
   */
  public RuntimeCacheException(String msg, Throwable cause) {
    super(msg, cause);
  }

}
