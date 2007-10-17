package org.hackystat.utilities.time.period;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import junit.framework.TestCase;

/**
 * Tests the DayCache implementation, and provides a main() method for simple 
 * performance evaluation.
 *
 * @author Philip M. Johnson
 */
public class TestDayCache extends TestCase {
  
  /**
   * Gets the passed date in yyyy-MM-dd format.
   * @param date The date. 
   * @return Today's date. 
   */
  private String getTestDate(Date date) {
    SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    return dayFormat.format(date);
  }
  
  /**
   * Returns the long value corresponding to the passed time for today.
   *
   * @param hourOfDay Hour of day.
   * @param minute Minute of day.
   * @param second Second of day.
   * @param milli Millisecond of day.
   *
   * @return long value for today at specified time.
   */
  private long getTodayAt(int hourOfDay, int minute, int second, int milli) {
    Calendar cal = Calendar.getInstance(Locale.US);
    cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
    cal.set(Calendar.MINUTE, minute);
    cal.set(Calendar.SECOND, second);
    cal.set(Calendar.MILLISECOND, milli);
    return cal.getTime().getTime();
  }

  /**
   * Tests to see that DayCache returns the same Day object for various times in the same day.
   */
  public void testUTCDayValue() {
    DayCache dayCache = DayCache.getInstance();
    Date today = new Date();
    String todayString = getTestDate(today);

    assertEquals("Test that cached today is equals to an uncached today.",
      dayCache.getDay(new Date().getTime()).getSimpleDayString(), todayString);
    assertEquals("Test that the cached Date of 11:59:59.999 today returns a cached Today.",
      dayCache.getDay(getTodayAt(23, 59, 59, 999)).getSimpleDayString(), todayString);
    assertEquals("Test that the cached Date of 00:00:00.000 today returns a cached Today.",
      dayCache.getDay(getTodayAt(0, 0, 0, 0)).getSimpleDayString(), todayString);
    assertFalse("Test that the cached Date of tomorrow is not Today.",
      dayCache.getDay(getTodayAt(23, 59, 59, 999) + 1).equals(dayCache.getDay(today.getTime())));
    assertFalse("Test that the cached Date of yesterday is not Today.",
      dayCache.getDay(getTodayAt(0, 0, 0, 0) - 1).equals(dayCache.getDay(today.getTime())));

    long twoYearUTC = today.getTime() + (1000 * 60 * 60 * 24 * 365 * 2);
    assertEquals("Test that a day two years in the future returns a day instance",
      dayCache.getDay(today.getTime() + twoYearUTC), new Day(today.getTime() + twoYearUTC));
    assertEquals("Test that a day two years in the past returns a day instance",
      dayCache.getDay(today.getTime() - twoYearUTC), new Day(today.getTime() - twoYearUTC));
  }

  /**
   * Supports performance analysis testing of DayCache. If this main is invoked with "-cache", it
   * invokes the DayCache on times starting now and incrementing by 1 second for the next 364
   * days.  Otherwise, does the same thing but just creates a new Day instance each time.
   * 
   * <p>
   * Initial evaluation indicates that without the cache, it takes around 80 seconds to create a
   * day instance for each second for 100 days. With the cache, it takes around 3 seconds.
   * </p>
   *
   * @param args An arg of "-cache" means use the DayCache.
   */
  public static void main(String[] args) {
    boolean useCache = (args.length > 0) && (args[0].equals("-cache"));
    System.out.println("Starting TestDayCache with cache " + ((useCache) ? "enabled" : "disabled"));

    Date startTime = new Date();
    long today = new Date().getTime();
    for (int day = 1; day < 100; day++) {
      for (int hour = 0; hour < 24; hour++) {
        for (int minute = 0; minute < 60; minute++) {
          for (int second = 0; second < 60; second++) {
            long tstamp = today + (1000 * second * minute * hour * day);
            if (useCache) {
              Day.getInstance(tstamp); 
            }
            else {
              new Day(tstamp);
            }
          }
        }
      }
    }

    System.out.println("Start time: " + startTime);
    System.out.println("End time: " + new Date());
  }
}
