package org.hackystat.utilities.time.interval;

import junit.framework.TestCase;

import org.hackystat.utilities.time.period.Day;
import org.hackystat.utilities.time.period.Week;

/**
 * Tests week interval.
 * 
 * @author Hongbing Kou
 * @version $Id: TestWeekInterval.java,v 1.1.1.1 2005/10/20 23:56:40 johnson Exp $
 */
public class TestWeekInterval extends TestCase {
  
  /**
   * Tests week interval type.
   * 
   * @throws Exception If error in test.
   */
  public void testWeekInterval() throws Exception {
    WeekInterval interval = new WeekInterval("03-Aug-2003 to 09-Aug-2003", 
                                             "28-Dec-2003 to 03-Jan-2004");
    assertFalse("Test interval type is daily", interval.isDailyInterval());
    assertTrue("Test interval type is not weekly", interval.isWeeklyInterval());
    assertFalse("Test interval type is not monthly", interval.isMonthlyInterval());
 
    assertEquals("Checking week interval name", "Week", interval.getIntervalType());
    
    assertEquals("Test start week", new Week(Day.getInstance(2003, 7, 3)), interval.getStartWeek());
    assertEquals("Test end week", new Week(Day.getInstance(2003, 11, 28)), interval.getEndWeek());
   
    interval = new WeekInterval("03-Aug-2003 to 09-Aug-2003", "03-Aug-2003 to 09-Aug-2003"); 
        
    assertEquals("Test toString() of WeekInterval", "Week Interval : 09-Aug-2003 ~ " 
                    + "09-Aug-2003", interval.toString());
  }
  
  /**
   * Tests week interval constructor with single day.
   * 
   * @throws Exception If failed to construct interval.
   */
  public void testDayWeekInterval() throws Exception {
    Day day1 = Day.getInstance(2005, 2, 3);
    Day day2 = Day.getInstance(2005, 4, 3);
    
    WeekInterval interval = new WeekInterval("03-Mar-2005", "03-May-2005");

    assertFalse("Test interval type is daily", interval.isDailyInterval());
    assertTrue("Test interval type is not weekly", interval.isWeeklyInterval());
    assertFalse("Test interval type is not monthly", interval.isMonthlyInterval());
 
    assertEquals("Checking week interval name", "Week", interval.getIntervalType());
    assertEquals("Test start week", new Week(day1), interval.getStartWeek());
    assertEquals("Test end week", new Week(day2), interval.getEndWeek());
  }
}
