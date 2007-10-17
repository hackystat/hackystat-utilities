package org.hackystat.utilities.time.interval;

import junit.framework.TestCase;

import org.hackystat.utilities.time.period.Month;

/**
 * Tests month interval.
 * 
 * @author Hongbing Kou
 * @version $Id: TestMonthInterval.java,v 1.1.1.1 2005/10/20 23:56:40 johnson Exp $
 */
public class TestMonthInterval extends TestCase {
  /**
   * Tests the month interval. 
   * 
   * @throws Exception If error in test.
   */
  public void testMonthInterval() throws Exception {
    MonthInterval interval = new MonthInterval("2003", "6", "2004", "0");

    assertFalse("Test interval type is daily", interval.isDailyInterval());
    assertFalse("Test interval type is not weekly", interval.isWeeklyInterval());
    assertTrue("Test interval type is not monthly", interval.isMonthlyInterval());

    assertEquals("Checking the from month", new Month(2003, 6), interval.getStartMonth());
    assertEquals("Checking the to month", new Month(2004, 0), interval.getEndMonth());
    assertEquals("Check the toString() for interval", "Month Interval : Jul-2003 ~ Jan-2004",
                 interval.toString());    

    
    interval = new MonthInterval("2004", "0", "2004", "0");    
    assertEquals("Check the toString() for interval", "Month Interval : Jan-2004 ~ Jan-2004",
                 interval.toString());    
  }
}
