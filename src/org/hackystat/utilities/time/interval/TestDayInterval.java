package org.hackystat.utilities.time.interval;

import junit.framework.TestCase;

import org.hackystat.utilities.time.period.Day;

/**
 * Tests the interval class. Interval class is from user's selection.
 *  
 * @author Hongbing Kou
 * @version $Id: TestDayInterval.java,v 1.1.1.1 2005/10/20 23:56:40 johnson Exp $
 */
public class TestDayInterval extends TestCase {
  
  /**
   * Test day interval. 
   * 
   * @throws Exception Occurs when interval is invalid.
   */
  public void testDayInterval() throws Exception {
    // Test day interval with start and end day.
    DayInterval interval = new DayInterval("2003", "10", "03", "2004", "0", "2");

    assertTrue("Test interval type is daily", interval.isDailyInterval());
    assertFalse("Test interval type is not weekly", interval.isWeeklyInterval());
    assertFalse("Test interval type is not monthly", interval.isMonthlyInterval());
    
    assertEquals("Testing day interval name", "Day", interval.getIntervalType());
    
    assertEquals("Tests start of the interval", Day.getInstance(2003, 10, 3), 
                                                    interval.getStartDay());
    assertEquals("Tests end of the interval", Day.getInstance(2004, 0, 2), 
                                                    interval.getEndDay());
    assertEquals("Test toString() of DayInterval", "Day Interval : 03-Nov-2003 ~ 02-Jan-2004",
                 interval.toString());
    
    interval = new DayInterval("2003", "10", "03", "2003", "10", "10");
    
    assertEquals("Test toString() of DayInterval", "Day Interval : 03-Nov-2003 ~ 10-Nov-2003",
                 interval.toString());
  }
}
