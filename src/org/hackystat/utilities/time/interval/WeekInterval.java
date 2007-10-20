package org.hackystat.utilities.time.interval;

import java.util.Iterator;

import org.hackystat.utilities.time.period.Week;

/**
 * Provides week interval. 
 * 
 * @author Hongbing Kou, Philip Johnson
 */
public class WeekInterval extends Interval implements Iterable<Week> {
  /** Start week. */
  private Week startWeek;
  /** End week. */ 
  private Week endWeek;
  
  /**
   * Creates a WeekInterval object with start and end information.
   * 
   * @param startWeek Start week string.
   * @param endWeek   End week string.
   * @throws IllegalIntervalException If the given start and end are invalid.
   */
  public WeekInterval(String startWeek, String endWeek) throws IllegalIntervalException {
    super("Week");
    
    IntervalUtility utility = IntervalUtility.getInstance();
    this.startWeek = utility.getWeek(startWeek);
    this.endWeek = utility.getWeek(endWeek);
    
    if (this.startWeek.compareTo(this.endWeek) > 0) {
      throw new IllegalIntervalException("Start week " + this.startWeek + " is later than end week "
                                        + this.endWeek);       
    }
  }

  /**
   * Gets start week. 
   * 
   * @return Start week.
   */
  public Week getStartWeek () {
    return this.startWeek;
  }
  
  /**
   * Gets end week. 
   * 
   * @return End week.
   */
  public Week getEndWeek () {
    return this.endWeek;
  }

  /**
   * Gets the iterator over the week period.
   * 
   * @return Iterator over week period. 
   */
  @Override
  public Iterator<Week> iterator() {
    return new WeekIterator(this);  
  }
  
  /**
   * String representaiton of the WeekInterval object.
   * 
   * @return Week interval string  
   */
  @Override
  public String toString() {
    StringBuffer buffer = new StringBuffer(80);
    buffer.append("Week Interval : ");
    buffer.append(this.startWeek);
    buffer.append(" ~ ");
    buffer.append(this.endWeek);
    return buffer.toString();
  }    
  
  /**
   * Gets the hash code.
   * 
   * @return The hash code.
   */
  @Override
  public int hashCode() {
    return this.startWeek.hashCode() / 2 + this.endWeek.hashCode() / 2;
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
    if (!(obj instanceof WeekInterval)) {
      return false;
    }    
    WeekInterval another = (WeekInterval) obj;
    return this.startWeek.equals(another.startWeek) && this.endWeek.equals(another.endWeek);
  }
}
