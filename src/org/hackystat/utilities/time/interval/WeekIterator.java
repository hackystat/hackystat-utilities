package org.hackystat.utilities.time.interval;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.hackystat.utilities.time.period.Week;

/**
 * Provides an iterator over week intervals.
 * 
 * @author Hongbing Kou
 */
public class WeekIterator implements Iterator<Week> {
  /** Start week. */
  private Week startWeek;
  /** End week. */
  private Week endWeek;
  /** Current week. */
  private Week currentWeek;
   
  /**
   * Creates an iterator.
   * 
   * @param weekInterval Week interval.
   */
  WeekIterator(WeekInterval weekInterval) {
    this.startWeek = weekInterval.getStartWeek();
    this.endWeek = weekInterval.getEndWeek();
    this.currentWeek = this.startWeek.dec();
  }
  
  /**
   * Required for iterator().  It will throw UnSupportedMethodException.
   */
  public void remove() {
    throw new UnsupportedOperationException("remove() is not supported by week iterator.");
  }

  /**
   * Whether it is still inside the day interval.
   * 
   * @return True if it is still in the interval.
   */
  public boolean hasNext() {
    return this.currentWeek.compareTo(this.endWeek) < 0;
  }

  /**
   * Gets the next day.
   * 
   * @return Next day.
   */
  public Week next() {    
    this.currentWeek = this.currentWeek.inc();
    
    if (this.currentWeek.compareTo(this.endWeek) > 0) {
      throw new NoSuchElementException("Reaches the end of week interval already.");
    }
    
    return this.currentWeek;
  }
}
