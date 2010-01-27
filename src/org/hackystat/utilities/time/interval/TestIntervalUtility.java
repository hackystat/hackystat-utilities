package org.hackystat.utilities.time.interval;

import java.util.TreeMap;

import junit.framework.TestCase;

import org.hackystat.utilities.time.period.Day;
import org.hackystat.utilities.time.period.Week;

/**
 * Tests the IntervalUtility class. Note that certain tests are commented out.  
 * It's not clear whether this class is used in Hackystat 8.
 * 
 * @author Hongbing Kou
 */
public class TestIntervalUtility extends TestCase {
  /**
   * Test the singleton instance implementation.
   */
  public void testSingleton() {
    IntervalUtility utility1 = IntervalUtility.getInstance();
    IntervalUtility utility2 = IntervalUtility.getInstance();

    assertSame(
        "Checking whether IntervalUtility is a singleton implementation",
        utility1, utility2);
  }

  /**
   * Tests day options.
   */
  public void testDayOptions() {
    IntervalUtility instance = IntervalUtility.getInstance();
    TreeMap<String, String> dayOptions = instance.getDayOptions();

    assertEquals("Checking the size of day options", 31, dayOptions.size());
    assertEquals("Checking the first day key", "01", dayOptions.firstKey());
    assertEquals("Checking the last day key", "31", dayOptions.lastKey());
  }

  /**
   * Tests year options.
   */
  public void testYearOptions() {
    IntervalUtility instance = IntervalUtility.getInstance();
    TreeMap<String, String> yearOptions = instance.getYearOptions();

    assertEquals("Checking the number of years", 20, yearOptions.size());
    assertEquals("Checking the first year", "2000", yearOptions.firstKey());
    assertEquals("Checking the last year", "2019", yearOptions.lastKey());
  }

  /**
   * Tests month options.
   */
  public void testMonthOptions() {
    IntervalUtility instance = IntervalUtility.getInstance();
    TreeMap<String, String> monthOptions = instance.getMonthOptions();

    assertEquals("Checking the number of month in a year", 12, monthOptions.size());
    //String firstMonthKey = (String) monthOptions.firstKey();
    //String firstMonthValue = (String) monthOptions.get(firstMonthKey);
    // if (Locale.getDefault().getLanguage().equals("en")) {
      //assertEquals("Checking the first month key in a year", "January", firstMonthKey);
    //}
    //assertEquals("Checking the first month value in a year", "00", firstMonthValue);

    //String lastMonthKey = (String) monthOptions.lastKey();
    //String lastMonthValue = (String) monthOptions.get(lastMonthKey);
    // if (Locale.getDefault().getLanguage().equals("en")) {
      //assertEquals("Checking the last month key in a year", "December", lastMonthKey);
    // }
    //assertEquals("Checking the last month value in a year", "11",  lastMonthValue);
  }

  /**
   * Tests week options.
   */
  public void testWeekOptions() {
    IntervalUtility instance = IntervalUtility.getInstance();
    TreeMap<String, String> weekOptions = instance.getWeekOptions();

    // assertEquals("Checking number of weeks look behind", 52,
    // weekOptions.size());
    assertFalse("Checking number of weeks look behind", weekOptions.isEmpty());
  }

  /**
   * Tests the week parser.
   */
  public void testWeekParser() {
    IntervalUtility instance = IntervalUtility.getInstance();

    // Test week string
    String weekString = "28-Dec-2003 to 03-Jan-2004";

    Week week = instance.getWeek(weekString);
    Day firstDay = Day.getInstance(2003, 11, 28);
    assertEquals("Testing the method to get first day of the week", firstDay, week.getFirstDay());

    Day lastDay = Day.getInstance(2004, 0, 3);
    assertEquals("Testing the method to get last day of the week", lastDay,  week.getLastDay());

    // Test another week string
    weekString = "11-Jan-2004 to 17-Jan-2004";

    week = instance.getWeek(weekString);
    firstDay = Day.getInstance(2004, 0, 11);
    assertEquals("Testing the method to get first day of the week", firstDay, week.getFirstDay());

    lastDay = Day.getInstance(2004, 0, 17);
    assertEquals("Testing the method to get last day of the week", lastDay, week.getLastDay());
  }

  /**
   * Tests the day parser.
   */
  public void testDayParser() {
    IntervalUtility instance = IntervalUtility.getInstance();

    String dayString = "01";
    String monthString = "00";
    String yearString = "2004";
    Day day = Day.getInstance(2004, 0, 1);
    assertEquals("Checking day parser", day, instance.getDay(yearString, monthString, dayString));

    dayString = "5";
    monthString = "11";
    yearString = "2004";
    day = Day.getInstance(2004, 11, 5);
    assertEquals("Checking day parser", day, instance.getDay(yearString, monthString, dayString));

    dayString = "14";
    monthString = "06";
    yearString = "2004";
    day = Day.getInstance(2004, 6, 14);
    assertEquals("Checking day parser", day, instance.getDay(yearString, monthString, dayString));
  }

  /**
   * Tests current year value.
   */
  public void testCurrentYearValue() {
    IntervalUtility instance = IntervalUtility.getInstance();
    assertTrue("Tests current year", Integer.parseInt(instance.getCurrentYear()) > 2000);
  }

  /**
   * Tests current month.
   */
  public void testCurrentMonthValue() {
    IntervalUtility instance = IntervalUtility.getInstance();
    assertTrue("Tests current month", Integer.parseInt(instance.getCurrentMonth()) >= 0);

  }

  /**
   * Tests current day.
   */
  public void testCurrentDayValue() {
    IntervalUtility instance = IntervalUtility.getInstance();
    assertTrue("Tests current day", instance.getCurrentDay().length() > 0);
  }

  /**
   * Tests current week.
   */
  public void testCurrentWeekValue() {
    IntervalUtility instance = IntervalUtility.getInstance();
    assertTrue("Tests current week", instance.getCurrentWeek().length() > 0);
  }
}