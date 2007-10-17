package org.hackystat.utilities.time.interval;

/**
 * Throws exception when end period of the interval is earlier than the start
 * interval period.
 * 
 * @author Hongbing Kou
 * @version $Id: IllegalIntervalException.java,v 1.1.1.1 2005/10/20 23:56:40 johnson Exp $
 */
@SuppressWarnings("serial")
public class IllegalIntervalException extends Exception {
  /**
   * Thrown when selected interval is not valid.
   *
   * @param detailMessage A message describing the problem.
   * @param previousException A possibly null reference to a prior exception.
   */
  public IllegalIntervalException(String detailMessage, Throwable previousException) {
    super(detailMessage, previousException);
  }


  /**
   * Thrown when selected interval is not valid.
   *
   * @param detailMessage A message describing the problem.
   */
  public IllegalIntervalException(String detailMessage) {
    super(detailMessage, null);
  }
}
