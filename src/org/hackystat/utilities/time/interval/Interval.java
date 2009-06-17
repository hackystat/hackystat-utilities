package org.hackystat.utilities.time.interval;

import java.util.Iterator;

/**
 * Provides a general interval type.
 * 
 * @author Hongbing Kou
 */
public abstract class Interval {
  /** Interval type. */
  private final String intervalType;

  /**
   * Instantiates an interval object.
   * 
   * @param intervalType Interval name.
   */
  public Interval(String intervalType) {
    this.intervalType = intervalType;
  }
  
  /**
   * Gets interval type.
   * 
   * @return Interval type.
   */
  public String getIntervalType() {
    return this.intervalType;
  }
  
  /**
   * Gets iterator over the interval.
   * 
   * @return Iterator over the interval.
   */
  public abstract Iterator<?> iterator();
  
  /**
   * Whether this interval is a daily interval type.
   * 
   * @return True if so and false otherwise.
   */
  public boolean isDailyInterval() {
    return this instanceof DayInterval;
  }

  /**
   * Whether this interval is a week interval type.
   * 
   * @return True if so and false otherwise.
   */
  public boolean isWeeklyInterval() {
    return this instanceof WeekInterval;
  }

  /**
   * Whether this interval is a month interval type.
   * 
   * @return True if so and false otherwise.
   */
  public boolean isMonthlyInterval() {
    return this instanceof MonthInterval;
  }
}