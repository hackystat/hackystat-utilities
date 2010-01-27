package org.hackystat.utilities.time.interval;

import java.io.Serializable;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.TreeMap;

import org.hackystat.utilities.time.period.Day;
import org.hackystat.utilities.time.period.Week;

/**
 * Provides support for constants and methods in the interval selector.
 *
 * @author Hongbing Kou, Philip Johnson
 */
class IntervalUtility {
  /** Year options from 2000 to 2020. */
  private TreeMap<String, String> yearOptions;  //NOPMD
  /** Three letters month map from Jan to Dec. */
  private TreeMap<String, String> monthOptions;//NOPMD
  /** Day map from first day to last day of the month.*/
  private TreeMap<String, String> dayOptions; //NOPMD
  /** Last half year's week map. */
  private TreeMap<String, String> weekOptions; //NOPMD

  /**
   * Initializes interval utility object.
   */
  private IntervalUtility() {
    // Day map
    this.dayOptions = new TreeMap<String, String>();
    for (int i = 1; i <= 31; i++) {
      String dayValue = (i < 10) ? "0" + i : String.valueOf(i);
      this.dayOptions.put(dayValue, dayValue);
    }
    
    // Month options
    this.monthOptions = new TreeMap<String, String>();
    DateFormatSymbols dateSymbols = new DateFormatSymbols();
    String[] monthStrings = dateSymbols.getMonths();

    for (int i = 0; i < monthStrings.length - 1; i++) {
      String monthValue = (i >= 0 && i < 10) ? "0" + i : String.valueOf(i);
      this.monthOptions.put(monthStrings[i], monthValue);
    }

    
    // Year options
    this.yearOptions = new TreeMap<String, String>();
    for (int i = 2000; i < 2020; i++) {
      String year = String.valueOf(i);
      yearOptions.put(year, year);
    }
    
    // Week options
    this.weekOptions = new TreeMap<String, String>(new WeekComparator());
    
    fillWeekOptions();
  }

  /**
   * Fills out the week options.
   */
  private void fillWeekOptions() {
    Day today = Day.getInstance();
    Week start = new Week(today.inc(-1 * 52 * 7));

    Week end = new Week();        
    for (Week week = start; week.compareTo(end) <= 0; week = week.inc()) {
      this.weekOptions.put(week.getWeekRepresentation(), week.getWeekRepresentation());
    }
  }
  
  /**
   * Returns a Week instance given a week string. 
   * The week string will be something like "11-Jan-2004 to 17-Jan-2004".
   * Test cases always pass an English locale-based string, so this method will
   * first try to parse the string using the current locale, then try to parse
   * it using Locale.US, then fail if neither works. 
   * 
   * @param weekString Week string from the week selector.
   * @return Week object represented by the week string.
   */
  public Week getWeek(String weekString) {
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
    Day day = null;
    try {
      day = Day.getInstance(formatter.parse(weekString.substring(0, 11)));
      return new Week(day);
    }
    catch (ParseException e) {
      // Now we're hosed; both the default and US Locales didn't work. 
      //ServerProperties.getInstance().getLogger().warning("Cannot parse the first day from " +
        //                      "week string " + weekString + e);
     throw new IllegalArgumentException("Week string " + weekString + " is not well formatted" + e);
    }    
  }

  /**
   * Gets map of years.
   * 
   * @return TreeMap of the year.
   */
  public TreeMap<String, String> getYearOptions() { //NOPMD
    return this.yearOptions;
  }
  
  /**
   * Gets map of months.
   * 
   * @return TreeMap of the months.
   */
  public TreeMap<String, String> getMonthOptions() { //NOPMD
    return this.monthOptions;
  }
  
  /**
   * Gets map of weeks.
   * 
   * @return TreeMap of the past 26 weeks.
   */
  public TreeMap<String, String> getWeekOptions() { //NOPMD
    checkWeekUpdates();
    return this.weekOptions;
  }
  
  /**
   * Update the week options if today is not in. 
   */
  private void checkWeekUpdates() {
    String lastWeekString = this.weekOptions.firstKey();
    Week endWeek = getWeek(lastWeekString);
    
    // If today is not in the range of week then rebuild the week options.
    Day today = Day.getInstance();
    if (today.compareTo(endWeek.getLastDay()) > 0) {
      fillWeekOptions();   
    }
  }
  
  /**
   * Gets map of days.
   * 
   * @return TreeMap of the days in a month.
   */
  public TreeMap<String, String> getDayOptions() { //NOPMD
    return this.dayOptions;
  }
  
  /**
   * Gets the singleton interval utility class.
   *
   * @return Singleton interval utility object.
   */
  public static IntervalUtility getInstance() {
    return IntervalUtilityHolder.THE_INSTANCE;
  }
  
  /** Week comparator class. */
  private static class WeekComparator implements Comparator<String>, Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Compares two month names.
     *
     * @param o1  The first month of the comparison
     * @param o2  The second month of the comparison
     * @return    -1, 0, 1 if o1 is less than, equal to, or greater than o2
     */
    public int compare(String o1, String o2) {
      String week1 = o1;
      String week2 = o2;

      SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
      
      try {
        Date date1 = formatter.parse(week1.substring(0, 11));      
        Date date2 = formatter.parse(week2.substring(0, 11));
        
        //return date1.compareTo(date2);
        // Sort week in reverse chronological order.
        return date2.compareTo(date1);
      }
      catch (ParseException e) {
        System.out.println(o1 + " or " + o2 + " cannot be parsed for comparison");
        e.printStackTrace();
        return -1;
      }      
    }  
  }
  
  /**
   * Creates a factory to hold the single IntervalUtility instance to avoid concurrent modification
   * problem according to Page 194, Chapter 9 of Effective Java Programming.
   */
  private static class IntervalUtilityHolder {
    /** Singleton IntervalUtility object. */
    static final IntervalUtility THE_INSTANCE = new IntervalUtility(); //NOPMD
  }

  /**
   * Get day object from year, month and day strings. 
   * 
   * @param yearString Year string in format '2004'.
   * @param monthString Month string in format '00', '02', '20' etc.
   * @param dayString  Day string in '12', '02' etc.
   * 
   * @return Day object.
   */
  public Day getDay(String yearString, String monthString, String dayString) {
    int year = Integer.parseInt(yearString);
    int month = Integer.parseInt(monthString);
    int day = Integer.parseInt(dayString);
    
    return Day.getInstance(year, month, day);
  }

  /**
   * Gets current year value in the year options.
   * 
   * @return Current year string.
   */
  public String getCurrentYear() {
    return Day.getInstance().getYearString();
  }

  /**
   * Gets current month value. 
   * 
   * @return Current month string.
   */
  public String getCurrentMonth() {
    return Day.getInstance().getMonthString();
  }

  /**
   * Gets current day string. 
   * 
   * @return Current day string.
   */
  public String getCurrentDay() {
    return Day.getInstance().getDayString();
  }

  /**
   * Gets current week string.
   * 
   * @return Current week
   */
  public String getCurrentWeek() {
    return this.weekOptions.lastKey();
  }
}