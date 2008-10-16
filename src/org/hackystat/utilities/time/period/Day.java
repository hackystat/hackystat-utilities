package org.hackystat.utilities.time.period;

import java.util.Date;
import java.util.Locale;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.xml.datatype.XMLGregorianCalendar;


/**
 * Provides a "cousin" of Date that represents only year, month, and day information. Many Hackystat
 * facilities need date information only at the precision of year/month/day.  This abstraction
 * represents a single 24 hour time period.
 * <p>
 * All Day constructors are private or package private. Clients of this class must use the 
 * static getInstance() methods to obtain Day instances. 
 * <p>
 * The Calendar is forced to Locale.US to ensure constant week boundaries. 
 * <p>
 * Day instances are immutable.
 * 
 * @author    Philip M. Johnson
 */
public final class Day implements TimePeriod {

  /** Used to represent the day. */
  private Calendar cal = Calendar.getInstance(Locale.US);
  /** The hashcode used for comparisons. */
  private int hashCode = 0;
  /** The date format for the toString method. */
  private static SimpleDateFormat toStringFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
  /** Simplified date format. */
  private static SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
  /** The formats for the getting the day, month, and year fields as strings. */
  private static SimpleDateFormat monthStringFormat = new SimpleDateFormat("MM", Locale.US);
  /** The formats for the getting the day, month, and year fields as strings. */
  private static SimpleDateFormat mediumMonthStringFormat = new SimpleDateFormat("MMM", Locale.US); 
  /** Year string formatter. */
  private static SimpleDateFormat yearStringFormat = new SimpleDateFormat("yyyy", Locale.US);
  /** Day string formatter. */
  private static SimpleDateFormat dayStringFormat = new SimpleDateFormat("dd", Locale.US);
  /** The number of milliseconds in a day. */
  private static long millisInDay = 1000 * 60 * 60 * 24;
  
  /**
   * Returns a Day instance corresponding to today.
   * @return A Day instance corresponding to today.
   */
  public static Day getInstance() {
    return new Day();
  }
  
  /**
   * Returns a Day instance corresponding to the day associated with timestamp.
   * 
   * @param timestamp A UTC time from which a Day instance will be returned.
   * @return A Day instance corresponding to timestamp.
   */
  public static Day getInstance(long timestamp) {
    return new Day(timestamp);
  }

  /**
   * Returns a Day instance corresponding to the passed Date.
   * 
   * @param date A date from which a Day instance will be returned. 
   * @return The Day instance corresponding to Date.
   */  
  public static Day getInstance(Date date) {
    return new Day(date.getTime());
  }
  
  /**
   * Returns a Day instance corresponding to the passed year, month, and day.
   * Historically used for testing, although getInstance(dayString) should be used instead
   * for clarity's sake unless numeric values are being manipulated directly. 
   *
   * @param year   Year, such as 2003.
   * @param month  Month, such as 0. Note that January is 0!
   * @param day    Day, such as 1. The first day is 1. 
   * @return The Day instance associated with this date.
   */
  public static Day getInstance(int year, int month, int day) {
    Calendar cal = Calendar.getInstance(Locale.US);
    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.MONTH, month);
    cal.set(Calendar.DAY_OF_MONTH, day);
    return new Day(cal.getTime().getTime());
  }
  
  /**
   * Returns a Day instance corresponding to the passed date in dd-MMM-yyyy format. 
   * This creates a temporary SimpleDateFormat, so it shouldn't be used when efficiency is 
   * important, but it's a nice method for test cases where dates are being supplied manually.
   *
   * @param dayString A string in dd-MMM-yyyy US Locale format, such as "01-Jan-2004".
   * @return The Day instance associated with this date.
   * @throws Exception If problems occur parsing dayString.
   */
  public static Day getInstance(String dayString) throws Exception {
    Date date = new SimpleDateFormat("dd-MMM-yyyy", Locale.US).parse(dayString);
    return new Day(date.getTime());
  }
  
  /**
   * Returns a Day instance associated with this passed XMLGregorianCalendar. 
   * @param xmlDay The date.
   * @return The corresponding day.
   */
  public static Day getInstance(XMLGregorianCalendar xmlDay) {
    long millis = xmlDay.toGregorianCalendar().getTimeInMillis();
    return new Day(millis);
  }

  /** 
   * Create a new Day, initializing it to today.
   */
  private Day() {
    this(new Date());
  }

  /**
   * Creates a new Day instance, initializing it to the passed long UTC time.
   * 
   * @param timestamp A UTC value indicating a time. 
   */
  private Day(long timestamp) {
    this(new Date(timestamp));
  }

  /**
   * Creates a new Day instance, initializing it from the passed Date.
   * The internal date is always set to 00:00:00.000 midnight at the 
   * beginning of the passed day.
   * We do this upfront to make comparisons and interval calculations fast, since we're
   * caching these Day instances there should be relatively few of them around.
   * 
   * @param date  A date.
   */
  private Day(Date date) {
    this.cal = Calendar.getInstance(Locale.US);
    this.cal.setTime(date);
    this.cal.set(Calendar.HOUR_OF_DAY, 00);
    this.cal.set(Calendar.MINUTE, 0);
    this.cal.set(Calendar.SECOND, 0);
    this.cal.set(Calendar.MILLISECOND, 0);
    //this.date = cal.getTime();
  }
  
  /**
   * Returns a Day instance corresponding to a day plus or minus increment days in the future 
   * or past. 
   *
   * @param increment  A positive or negative integer.
   * @return           A Day corresponding to the increment from this day.
   */
  public Day inc(int increment) {
    Calendar newCal = (Calendar)this.cal.clone();
    newCal.add(Calendar.DAY_OF_YEAR, increment);
    return new Day(newCal.getTime());
  }
  
  /**
   * Returns a freshly created Date object that corresponds to the current day.
   * Note that this Date object may not correspond to the same time of day as was
   * used to create this Day instance!
   * @return A Date object corresponding to this day.
   */
  public Date getDate() {
    return new Date(this.cal.getTimeInMillis());
  }
  
  /**
   * Returns the "first" day in the TimePeriod.  For Days, that's just this day.
   * @return The current day.
   */
  public Day getFirstDay() {
    return this;
  }
  
  /**
   * Returns the number of days (positive or negative) between day1 and day2.
   * @param day1 A Day instance.
   * @param day2 A Day instance.
   * @return The interval in days between day1 and day2.
   */
  public static int daysBetween(Day day1, Day day2) {
    return Math.round((float)(day2.cal.getTimeInMillis() - day1.cal.getTimeInMillis()) 
        / millisInDay);
  }

  /**
   * Compares two Day objects.
   *
   * @param o  A Day instance.
   * @return   The standard compareTo values.
   */
  public int compareTo(Object o) {
    return this.cal.compareTo(((Day)o).cal);
  }
  
  /**
   * Returns true if this Day preceeds the passed Day, false if the two Days are equal or this
   * day comes after the passed Day.
   * @param day The day to be compared.
   * @return True if the passed day comes after this Day.
   */
  public boolean isBefore(Day day) {
    return (this.cal.before(day.cal));
  }
  
  /**
   * Two Day instances are equal() iff they have equal year, month, and day fields.
   *
   * @param obj  Any object.
   * @return     True if obj is a Day and it's equal to this Day.
   */
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Day)) {
      return false;
    }
    Calendar thisCal = this.cal;
    Calendar otherCal = ((Day)obj).cal;
    return 
    ((thisCal.get(Calendar.YEAR) == otherCal.get(Calendar.YEAR)) &&
        (thisCal.get(Calendar.DAY_OF_YEAR) == otherCal.get(Calendar.DAY_OF_YEAR)));
  }


  /**
   * Compute the hashcode following recommendations in "Effective Java".
   *
   * @return   The hashcode.
   */
  @Override
  public int hashCode() {
    if (this.hashCode == 0) {
      this.hashCode = 17;
      this.hashCode = 37 * this.hashCode + this.cal.get(Calendar.YEAR);
      this.hashCode = 37 * this.hashCode + this.cal.get(Calendar.MONTH);
      this.hashCode = 37 * this.hashCode + this.cal.get(Calendar.DAY_OF_MONTH);
    }
    return this.hashCode;
  }

  /**
   * Returns this Day instance in YYYY-MM-DD format.
   *
   * @return   The day as a string.
   */
  @Override
  public String toString() {
    synchronized (Day.toStringFormat) {
      return Day.toStringFormat.format(new Date(this.cal.getTimeInMillis()));
    }
  }

  /**
   * Gets the simple date string in 2004-03-25 format.
   * 
   * @return Simply date format.
   */
  public String getSimpleDayString() {
    synchronized (Day.simpleFormat) {
      return Day.simpleFormat.format(new Date(this.cal.getTimeInMillis()));
    }
  }
  
  
  /**
   * Returns a two character string representing this Day's day (00-31).
   *
   * @return   The day as a string.
   */
  public String getDayString() {
    synchronized (Day.dayStringFormat) {
      return Day.dayStringFormat.format(new Date(this.cal.getTimeInMillis()));
    }
  }

  /**
   * Returns a two character string representing this Day's month (01-12).
   *
   * @return   The month as a string.
   */
  public String getMonthString() {
    synchronized (Day.monthStringFormat) {
      return Day.monthStringFormat.format(new Date(this.cal.getTimeInMillis()));
    }
  }


  /**
   * Returns a three character string representing this Day's month, e.g. Jan, Feb.
   *
   * @return   The month as a string.
   */
  public String getMediumMonthString() {
    synchronized (Day.mediumMonthStringFormat) {
      return Day.mediumMonthStringFormat.format(new Date(this.cal.getTimeInMillis()));
    }
  }

  /**
   * Returns a four character string representing this Day's year (2000, etc.).
   *
   * @return   The year as a string.
   */
  public String getYearString() {
    synchronized (Day.yearStringFormat) {
      return Day.yearStringFormat.format(new Date(this.cal.getTimeInMillis()));
    }
  }

  /**
   * Gets the first tick of the day.
   * 
   * @return The first tick of the day in millis.
   */
  public long getFirstTickOfTheDay() {
    return this.cal.getTimeInMillis();
  }
  
  /**
   * Gets the last tick of the day.
   * 
   * @return The last tick of the day in millis.
   */
  public long getLastTickOfTheDay() {
    Calendar newCal = Calendar.getInstance(Locale.US);
    newCal.setTimeInMillis(this.cal.getTimeInMillis());
    newCal.set(Calendar.HOUR_OF_DAY, newCal.getActualMaximum(Calendar.HOUR_OF_DAY));
    newCal.set(Calendar.MINUTE, newCal.getActualMaximum(Calendar.MINUTE));
    newCal.set(Calendar.SECOND, newCal.getActualMaximum(Calendar.SECOND));
    newCal.set(Calendar.MILLISECOND, newCal.getActualMaximum(Calendar.MILLISECOND));
    return newCal.getTime().getTime();
  }
}

