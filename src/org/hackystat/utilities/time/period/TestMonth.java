package org.hackystat.utilities.time.period;

import javax.xml.datatype.XMLGregorianCalendar;

import org.hackystat.utilities.tstamp.Tstamp;

import junit.framework.TestCase;

/**
 * Tests the Month and Months implementations.
 * 
 * @author Hongbing Kou, Philip Johnson
 */
public class TestMonth extends TestCase {
  
  /**
   * Test the XMLGregorianCalendar constructor. 
   * @throws Exception If problems. 
   */
  public void testXmlMonth() throws Exception {
    XMLGregorianCalendar xmlDay = Tstamp.makeTimestamp("2003-08-01");
    Month month = new Month(xmlDay);
    assertEquals("Checking month's toString() method", "Aug-2003", month.toString());
  }
  
  /**
   * Test month constructor and operation.
   */
  public void testMonth() {
    // Creates January, 2003
    Month month = new Month(2003, 0);
    assertEquals("Checking the year", 2003, month.getYear());
    assertEquals("Checking the month", 0, month.getMonth());
    assertEquals("Checking month's toString() method", "Jan-2003", month.toString());
    assertEquals("Checking number of days in this month", 31, month.getNumOfDays());    
    assertEquals("Checking the first day of this month", Day.getInstance(2003, 0, 1), 
                                                         month.getFirstDay());

    assertEquals("Checking the first week of this month", new Week(Day.getInstance(2003, 0, 1)), 
                                                         month.getFirstWeekInMonth());

    assertEquals("Checking the last day of this month", Day.getInstance(2003, 0, 31), 
                                                         month.getLastDay());
    assertEquals("Checking the last week of this month", new Week(Day.getInstance(2003, 0, 31)), 
                                                         month.getLastWeekInMonth());
    
    Month month2 = new Month(2003, 1);
    assertTrue("Check the month comparison", month.compareTo(month2) < 0);
    assertEquals("Checking number of days in month Feb, 2003", 28, month2.getNumOfDays());
    assertEquals("Checking the last day in Feb, 2003", Day.getInstance(2003, 1, 28), 
                                                         month2.getLastDay());
    
    Month month3 = new Month(2002, 11);
    assertTrue("Check the month comparison", month.compareTo(month3) > 0);
    assertEquals("Checking number of days in this month", 31, month3.getNumOfDays());    
    assertEquals("Checking the last day in Feb, 2003", Day.getInstance(2002, 11, 31), 
                                                         month3.getLastDay());
  }
  
  /**
   * Tests month metods.
   */
  public void testMonth2() {
    Month month = new Month(2003, 11);
    Month prev = new Month(2003, 10);
    Month next = new Month(2004, 0);
    
    assertEquals("Checking the previous method", prev, month.dec());
    assertEquals("Checking the next method", next, month.inc());
    month = new Month(2004, 0);
    prev = new Month(2003, 11);
    next = new Month(2004, 1);

    assertEquals("Checking the previous method", prev, month.dec());
    assertEquals("Checking the next method", next, month.inc());
  }
  
  /**
   * Test month collection.
   */
  public void testMonths() {
    Month month = new Month(2004, 1);
    assertEquals("Check the number of days in the month with day list", 29, month.getDays().size());
  }
}
