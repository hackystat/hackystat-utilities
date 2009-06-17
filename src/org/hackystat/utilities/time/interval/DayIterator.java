package org.hackystat.utilities.time.interval;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.hackystat.utilities.time.period.Day;

/**
 * Provides an iterator over Day instances. 
 * 
 * @author Hongbing Kou, Philip Johnson
 */
public class DayIterator implements Iterator<Day> {
  /** End day of the day interval. */
  private final Day endDay;
  /** Current day. */
  private Day currentDay;
  
  /**
   * Creates a day iterator over the interval.
   *  
   * @param dayInterval Iterator over the day.
   */
  DayIterator(DayInterval dayInterval) {
    Day startDay = dayInterval.getStartDay();
    this.endDay = dayInterval.getEndDay();
    this.currentDay = startDay.inc(-1); 
  }
  
  /**
   * Required for iterator().  It will throw UnSupportedMethodException.
   */
  public void remove() {
    throw new UnsupportedOperationException("remove() is not supported by day iterator.");
  }

  /**
   * Whether it is still inside the day interval.
   * 
   * @return True if it is still in the interval.
   */
  public boolean hasNext() {
    return this.currentDay.compareTo(this.endDay) < 0;
  }

  /**
   * Gets the next day.
   * 
   * @return Next day.
   */
  public Day next() {    
    this.currentDay = this.currentDay.inc(1);
    
    if (this.currentDay.compareTo(this.endDay) > 0) {
      throw new NoSuchElementException("Reaches the end of day interval already.");
    }
    
    return this.currentDay;
  }
}
