package org.hackystat.utilities.time.interval;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.hackystat.utilities.time.period.Month;

/**
 * Defines iterator for month interval.
 *  
 * @author Hongbing Kou
 * @version $Id: MonthIterator.java,v 1.1.1.1 2005/10/20 23:56:40 johnson Exp $
 */
public class MonthIterator implements Iterator<Month> {
  /** Start month. */
  private Month startMonth;
  /** End month. */
  private Month endMonth;
  /** Current month. */
  private Month currentMonth;
   
  /**
   * Creates an iterator.
   * 
   * @param monthInterval Month Interval.
   */
  MonthIterator(MonthInterval monthInterval) {
    this.startMonth = monthInterval.getStartMonth();
    this.endMonth = monthInterval.getEndMonth();
    this.currentMonth = this.startMonth.dec();
  }
  
  /**
   * Gets the next month.
   * 
   * @return Next month.
   */  
  public Month next() {
    this.currentMonth = this.currentMonth.inc();
    
    if (this.currentMonth.compareTo(this.endMonth) > 0) {
      throw new NoSuchElementException("Reaches the end of month interval already.");
    }
    
    return this.currentMonth;     
  }
  
  /**
   * If it is before the end month it will be a good one.
   * 
   * @return Run our of interval or not.
   */
  public boolean hasNext() {
    return this.currentMonth.compareTo(this.endMonth) < 0;
  }
  
  /**
   * Required for iterator().  It will throw UnSupportedMethodException.
   */
  public void remove() {
    throw new UnsupportedOperationException("remove() is not supported by month iterator.");
  }
}
