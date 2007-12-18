package org.hackystat.utilities.tstamp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.Test;

/**
 * Tests the Tstamp class. 
 * @author Philip Johnson
 */
public class TestTstamp {
  /**
   * Tests the Tstamp class.
   * @throws Exception if problems occur. 
   */
  @Test public void testTstampArithmetic () throws Exception {
    XMLGregorianCalendar date1 = Tstamp.makeTimestamp("2007-08-01");
    XMLGregorianCalendar date2 = Tstamp.makeTimestamp("2007-08-02");
    XMLGregorianCalendar date3 = Tstamp.makeTimestamp("2007-08-03");
    assertTrue("Test equal", Tstamp.equal(date1, date1));
    assertTrue("Test increment days", Tstamp.equal(date2, Tstamp.incrementDays(date1, 1)));
    
    XMLGregorianCalendar date4 = Tstamp.makeTimestamp("2007-08-01T01:00:00");
    assertTrue("Test increment hours", Tstamp.equal(date4, Tstamp.incrementHours(date1, 1)));
    
    XMLGregorianCalendar date5 = Tstamp.makeTimestamp("2007-08-01T00:01:00");
    assertTrue("Test increment mins", Tstamp.equal(date5, Tstamp.incrementMinutes(date1, 1)));
    
    XMLGregorianCalendar date6 = Tstamp.makeTimestamp("2007-08-01T00:00:01");
    assertTrue("Test increment secs", Tstamp.equal(date6, Tstamp.incrementSeconds(date1, 1)));

    assertTrue("Test greater than 1", Tstamp.greaterThan(date2, date1));
    assertFalse("Test greater than 2", Tstamp.greaterThan(date1, date2));
    assertFalse("Test greater than 3", Tstamp.greaterThan(date1, date1));
    
    assertTrue("Test less than 1", Tstamp.lessThan(date1, date2));
    assertFalse("Test less than 2", Tstamp.lessThan(date2, date1));
    assertFalse("Test less than 3", Tstamp.lessThan(date1, date1));   
    
    assertTrue("Test inbetween 1", Tstamp.inBetween(date1, date2, date3));
    assertTrue("Test inbetween 2", Tstamp.inBetween(date1, date1, date2));
    assertTrue("Test inbetween 3", Tstamp.inBetween(date1, date2, date2));    
    assertFalse("Test inbetween 4", Tstamp.inBetween(date1, date3, date2));   
    
    assertFalse("Test bogus time check", Tstamp.isBogusStartTime(date1));
  }
  
  /**
   * Tests time span. 
   * 
   * @throws Exception If problem occurs
   */
  @Test public void testTstampSpan() throws Exception {
    XMLGregorianCalendar date1 = Tstamp.makeTimestamp("2007-08-01T01:00:00.000");
    XMLGregorianCalendar date2 = Tstamp.makeTimestamp("2007-08-01T01:01:20.200");
    XMLGregorianCalendar date3 = Tstamp.makeTimestamp("2007-08-01T01:01:20.400");

    assertEquals("Test timespan 80200 ms", 80200, Tstamp.diff(date1, date2));
    assertEquals("Test timespan 200 ms", 200, Tstamp.diff(date2, date3));
  }

}
