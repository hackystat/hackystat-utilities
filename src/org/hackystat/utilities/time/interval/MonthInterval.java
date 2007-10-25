package org.hackystat.utilities.time.interval;

import java.util.Iterator;

import javax.xml.datatype.XMLGregorianCalendar;

import org.hackystat.utilities.time.period.Month;

/**
 * Provides month interval type.
 * 
 * @author Hongbing Kou, Philip Johnson
 */
public class MonthInterval extends Interval implements Iterable<Month> {
  /** Start month. */
  private Month startMonth;
  /** End month. */
  private Month endMonth;
  
  /**
   * Creates a MonthInterval object with start month and end month.
   * 
   * @param startYear Start year.  
   * @param startMonth Start month.
   * @param endYear End year.
   * @param endMonth End month.
   * 
   * @throws IllegalIntervalException If try to create an interval with invalid period.
   */
  public MonthInterval(String startYear, String startMonth, String endYear, String endMonth) 
     throws IllegalIntervalException {
    super("Month");    
    int year = Integer.parseInt(startYear);
    int month = Integer.parseInt(startMonth);
    this.startMonth = new Month(year, month);
    
    year = Integer.parseInt(endYear);
    month = Integer.parseInt(endMonth);    
    this.endMonth = new Month(year, month);   
    
    if (this.startMonth.compareTo(this.endMonth) > 0) {
      throw new IllegalIntervalException("Start month " + this.startMonth 
                                       + "  is later than end month " + this.endMonth);
    }
  }
  
  /**
   * Creates a MonthInterval object with start month and end month.
   * 
   * @param startMonth Start month.
   * @param endMonth End month.
   * 
   * @throws IllegalIntervalException If try to create an interval with invalid period.
   */
  public MonthInterval(XMLGregorianCalendar startMonth, XMLGregorianCalendar endMonth) 
     throws IllegalIntervalException {
    super("Month");    
    this.startMonth = new Month(startMonth);
    this.endMonth = new Month(endMonth);   
    
    if (this.startMonth.compareTo(this.endMonth) > 0) {
      throw new IllegalIntervalException("Start month " + this.startMonth 
                                       + "  is later than end month " + this.endMonth);
    }
  }
  
  /**
   * Gets start month.
   * 
   * @return Start month.
   */
  public Month getStartMonth() {
    return this.startMonth;
  }

  /**
   * Gets the end month.
   * 
   * @return End month.
   */
  public Month getEndMonth() {
    return this.endMonth;
  }

  
  /**
   * String representation of the MonthInterval object.
   * 
   * @return Month interval string  
   */
  @Override
  public String toString() {
    StringBuffer buffer = new StringBuffer(30);
    buffer.append("Month Interval : ");
    buffer.append(this.startMonth);
    buffer.append(" ~ ");
    buffer.append(this.endMonth);
    
    return buffer.toString();
  }  
  
  /**
   * Gets the iterator over the month interval.
   * 
   * @return Iterator over month. 
   */
  @Override
  public Iterator<Month> iterator() {
    return new MonthIterator(this);
  }  
  
  /**
   * Gets the hash code.
   * 
   * @return The hash code.
   */
  @Override
  public int hashCode() {
    return this.startMonth.hashCode() / 2 + this.endMonth.hashCode() / 2;
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
    if (!(obj instanceof MonthInterval)) {
      return false;
    }    
    MonthInterval another = (MonthInterval) obj;
    return this.startMonth.equals(another.startMonth) && this.endMonth.equals(another.endMonth);
  }  
}
