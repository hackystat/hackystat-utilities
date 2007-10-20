package org.hackystat.utilities.time.interval;

import java.util.Iterator;

import org.hackystat.utilities.time.period.Day;

/**
 * Represents an interval as a set of Days.
 * 
 * @author Hongbing Kou, Philip Johnson
 */
public class DayInterval extends Interval implements Iterable<Day> {

  /** Start day of the interval. */
  private Day startDay;

  /** End day of the interval. */
  private Day endDay;

  /**
   * Instantiates DayInterval with start day and end day.
   * 
   * @param startYear Interval's start year.
   * @param startMonth Interval's start month.
   * @param startDay Interval's start day.
   * @param endYear Interval's end year.
   * @param endMonth Interval's end month.
   * @param endDay Interval's end day.
   * @throws IllegalIntervalException If start day is later than end day.
   */
  public DayInterval(String startYear, String startMonth, String startDay, String endYear,
      String endMonth, String endDay) throws IllegalIntervalException {
    super("Day");
    IntervalUtility utility = IntervalUtility.getInstance();
    this.startDay = utility.getDay(startYear, startMonth, startDay);
    this.endDay = utility.getDay(endYear, endMonth, endDay);
    if (this.startDay.compareTo(this.endDay) > 0) {
      throw new IllegalIntervalException("Start day " + this.startDay + " is later than end day "
          + this.endDay);
    }
  }

  /**
   * Instantiates DayInterval with start day and end day.
   * 
   * @param startDay The starting day.
   * @param endDay The ending day.
   * @throws IllegalIntervalException If start day is later than end day.
   */
  public DayInterval(Day startDay, Day endDay) throws IllegalIntervalException {
    super("Day");
    this.startDay = startDay;
    this.endDay = endDay;
    if (this.startDay.compareTo(this.endDay) > 0) {
      throw new IllegalIntervalException("Start day " + this.startDay + " is later than end day "
          + this.endDay);
    }
  }

  /**
   * Gets the start day of this interval.
   * 
   * @return Start day of this interval.
   */
  public Day getStartDay() {
    return this.startDay;
  }

  /**
   * Gets the end day of this interval.
   * 
   * @return End day of this interval.
   */
  public Day getEndDay() {
    return this.endDay;
  }

  /**
   * Returns an iterator over the days in this interval.
   * 
   * @return Iterator over a period.
   */
  @Override
  public Iterator<Day> iterator() {
    return new DayIterator(this);
  }

  /**
   * String representation of this interval.
   * 
   * @return Day interval string
   */
  @Override
  public String toString() {
    StringBuffer buffer = new StringBuffer(55);
    buffer.append("Day Interval : ");
    buffer.append(this.startDay);
    buffer.append(" ~ ");
    buffer.append(this.endDay);

    return buffer.toString();
  }

  /**
   * Gets the hash code.
   * 
   * @return The hash code.
   */
  @Override
  public int hashCode() {
    return this.startDay.hashCode() / 2 + this.endDay.hashCode() / 2;
  }

  /**
   * Indicates whether some other object is "equal to" this one.
   * 
   * @param obj Another instance of <code>DayInterval</code>.
   * 
   * @return True if they are equal, false otherwise.
   */
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof DayInterval)) {
      return false;
    }
    DayInterval another = (DayInterval) obj;
    return this.startDay.equals(another.startDay) && this.endDay.equals(another.endDay);
  }
}
