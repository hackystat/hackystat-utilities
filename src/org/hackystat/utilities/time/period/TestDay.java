package org.hackystat.utilities.time.period;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import junit.framework.TestCase;

/**
 * Tests the Day implementation.
 *
 * @author    Hongbing Kou
 */
public class TestDay extends TestCase {

  
  /**
   * Test the APIs of day object.
   * @throws Exception If problems occur.
   */
  public void testDay() throws Exception {
    Day day = Day.getInstance("01-Jan-2001");
    Day sameDay = Day.getInstance(2001, 0, 1);
    assertEquals("Check the year string", "2001", day.getYearString());
    assertEquals("Check the month string", "01", day.getMonthString());
    assertEquals("Check the day string", "01", day.getDayString());
    assertTrue("Checking the hashcode of the day object", day.hashCode() > 0);
    assertEquals("Checking day and sameday.", day, sameDay);

    Day day2 = Day.getInstance();
    Day day3 = Day.getInstance();
    assertEquals("Checking that two days in same day are equal", day2, day3);

    Day day4 = day3.inc(1);
    assertTrue("Checking the comparison less", day3.compareTo(day4) < 0);
    assertEquals("Checking the comparison equal", day3.compareTo(day3), 0);
    assertTrue("Checking the comparison more", day4.compareTo(day3) > 0);
    
    assertTrue("Checking isBefore", day3.isBefore(day4));
    assertFalse("Checking isBefore with the same day", day3.isBefore(day3));

    assertEquals("Checking daysBetween on same day", 0, Day.daysBetween(day, day));
    assertEquals("Checking daysBetween on adjacent days.", 1, Day.daysBetween(day3, day4));
    assertEquals("Checking daysBetween on adjacent days.", -1, Day.daysBetween(day4, day3));
    assertEquals("Checking daysBetween on dist days.", 100, Day.daysBetween(day3, day3.inc(100)));
    
    TimeZone defaultTimeZone = TimeZone.getDefault();
    Calendar cal = Calendar.getInstance(Locale.US);
    // Test the DayCache because of daylight saving
    if (defaultTimeZone.inDaylightTime(cal.getTime())) {
      // January first is not in daylight saving
      cal.set(Calendar.MONTH, 0);
      cal.set(Calendar.DAY_OF_MONTH, 1);
      
      // 11:15pm
      cal.set(Calendar.HOUR_OF_DAY, 23);           
      cal.set(Calendar.MINUTE, 15);      
      Date lateNight = cal.getTime();
      Day.getInstance(lateNight);  // late night day. 
      
      // 9:30am
      cal.set(Calendar.HOUR_OF_DAY, 9);           
      cal.set(Calendar.MINUTE, 30);
      Date officeHour = cal.getTime();

      Day officeHourDay = Day.getInstance(officeHour);
      
      // Gets next day
      Day nextDay = officeHourDay.inc(1);
      
      assertNotSame("Next day should be 1/2 but 1/1 ", officeHourDay.compareTo(nextDay), 0);
    }
    else {
      // July 4th is in daylight saving
      cal.add(Calendar.YEAR, 1);
      cal.set(Calendar.MONTH, 6);
      cal.set(Calendar.DAY_OF_MONTH, 4);
      
      // 12:10am
      cal.set(Calendar.HOUR_OF_DAY, 0);           
      cal.set(Calendar.MINUTE, 10);
      Date earlyMorning = cal.getTime();
      Day.getInstance(earlyMorning); // early morning day
      
      // 9:30am
      cal.set(Calendar.HOUR_OF_DAY, 9);           
      cal.set(Calendar.MINUTE, 30);
      Date officeHour = cal.getTime();
      Day officeHourDay = Day.getInstance(officeHour);
      
      Day previousDay = officeHourDay.inc(-1);
      assertNotSame("Previous day should be 7/3 not 7/4", officeHourDay.compareTo(previousDay), 0);
    }
  }
  
  /** 
   * Tests the day light savings issue associated with getting a day.  When a Day instance is 
   * created it is retrieved from DayCache. The DayCache must account for differences
   * in timestamps due to TimeZone and Daylight Saving Issues. This unit tests ensures
   * that we are able to correctly create Day objects for a 3 year period at 30 minute intervals.
   * <p>
   * A failure in this method is caused by a date (ie. 0-31) of the Day object does not
   * equal the date of the Date object.  This indicates that the DayCache is not interpreting
   * the TimeZone and DST correctly.
   */
  public void testDaylightSavingsIssues() {
    Calendar calendar = Calendar.getInstance(Locale.US);
    Calendar tempCal = Calendar.getInstance(Locale.US);
    Calendar calendarNextYear = Calendar.getInstance(Locale.US);
    tempCal.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 1);
    calendarNextYear.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 2);
    while (!tempCal.equals(calendarNextYear)) {
      Day day = Day.getInstance(tempCal.getTime());
      assertEquals("checking that the day values are the same " +
          "[date=" + tempCal.getTime() + ", day=" + day + "]", 
          tempCal.get(Calendar.DATE), Integer.parseInt(day.getDayString()));
      tempCal.add(Calendar.MINUTE, 30);
    }
  }
  
  /**
   * Tests <code>getFirstTickOfTheDay</code> and <code>getLastTickOfTheDay</code> method.
   */
  public void testGetFirstTickOfTheDay() {
    long millisInADay = 24 * 60 * 60 * 1000;
    Calendar cal = Calendar.getInstance(Locale.US);
    cal.set(2005, 0, 1, 10, 10, 10);
    Day day = Day.getInstance(cal.getTime());
    
    cal.set(2005, 0, 1, 0, 0, 0);
    cal.set(Calendar.MILLISECOND, 0);
    long firstTickOfDay = cal.getTimeInMillis();
    long lastTickOfDay = firstTickOfDay + millisInADay - 1;
    assertEquals("Checking first tick", firstTickOfDay, day.getFirstTickOfTheDay());
    assertEquals("Checking last tick", lastTickOfDay, day.getLastTickOfTheDay());
  }
}