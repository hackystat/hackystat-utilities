package org.hackystat.utilities.time.period;

import java.util.Locale;

import junit.framework.TestCase;

/**
 * Tests the Week implementation.
 * 
 * @author Hongbing Kou
 * @version $Id: TestWeek.java,v 1.1.1.1 2005/10/20 23:56:44 johnson Exp $
 */
public class TestWeek  extends TestCase {
  /**
   * Test week constructor.
   * 
   * @throws Exception If error in test. 
   */
  public void testWeek() throws Exception {
    Day day = Day.getInstance(2004, 0, 1);
    
    Week week = new Week(day);
    assertEquals("First day of the week", Day.getInstance(2003, 11, 28), week.getFirstDay());
    assertEquals("Last  day of the week", Day.getInstance(2004, 0, 3), week.getLastDay());
    if (Locale.getDefault().getLanguage().equals("en")) {
      assertEquals("Check the toString method ", "28-Dec-2003 to 03-Jan-2004", 
          week.getWeekRepresentation());
      assertEquals("Check the toString method ", "03-Jan-2004", week.toString());
    }

    Week week2 = new Week(Day.getInstance(2003, 11, 20));
    assertTrue("Checking the comparison method", week.compareTo(week2) > 0);

    Week week3 = new Week(Day.getInstance(2003, 11, 30));
    assertEquals("Checking the comparison method", 0, week.compareTo(week3));

    Week week4 = new Week(Day.getInstance(2004, 0, 4));
    assertTrue("Checking the comparison method", week.compareTo(week4) < 0);
  }
  
  /**
   * Test week methods.
   */
  public void testWeek2 () {
    Week week = new Week(Day.getInstance(2003, 10, 10));
    
    if (Locale.getDefault().getLanguage().equals("en")) {
      assertEquals("Checks week's getWeekRepresentation() method", "09-Nov-2003 to 15-Nov-2003", 
          week.getWeekRepresentation());
      assertEquals("Checks week's toString() method", "15-Nov-2003", week.toString());
    }
    Week prev = new Week(Day.getInstance(2003, 10, 3));
    assertEquals("Checks method previous()", prev, week.dec());
  
    Week next = new Week(Day.getInstance(2003, 10, 17));
    assertEquals("Checks method next()", next, week.inc());
  }
  
  /**
   * Tests days in this week.
   */
  public void testWeeks() {
    Week week = new Week(Day.getInstance(2004, 0, 1));
    assertEquals("Test number of days in week by weeks' list", 7, week.getDays().size());     
  }
}