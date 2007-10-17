package org.hackystat.utilities.time.period;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Provides a representation for seven Day instances where the first Day 
 * is always a Sunday and the last Day is always the following Saturday. 
 * <p>
 * The Calendar is forced to Locale.US to ensure constant week boundaries. 
 *
 * @author Hongbing Kou
 */
public class Week implements TimePeriod {
  /** First day of the week. */
  private Day firstDay;
  /** Last day of the week. */
  private Day lastDay;
  /** A list of days in this week. */
  private List<Day> days;
   
  /**
   * Create a Week instance that starts on Sunday and ends on Saturday and that includes today.
   */
  public Week() {
    this(Day.getInstance(new Date()));
  }
  
  /**
   * Create a Week instance that includes the passed date.
   * @param date The date to be included in the constructed Week.
   */
  public Week (Date date) {
    this(Day.getInstance(date));
  }
  
  /**
   * Creates a Week instance that starts on Sunday and ends on Saturday and that includes the
   * passed day.
   * @param day A day that identifies the Week to be returned.
   */
  public Week(Day day) {
    Calendar cal = Calendar.getInstance(Locale.US);
    cal.setTime(day.getDate());
    cal.set(Calendar.DAY_OF_WEEK, 1);
    
    this.firstDay = Day.getInstance(cal.getTime());
    cal.set(Calendar.DAY_OF_WEEK, 7);
    this.lastDay = Day.getInstance(cal.getTime());
    
    // Create days' list.
    this.days = new ArrayList<Day>();
    for (Day weekDay = this.firstDay; weekDay.compareTo(this.lastDay) <= 0; 
                                      weekDay = weekDay.inc(1)) {
      this.days.add(weekDay);
    }
  }

  /**
   * Gets first day of this Week, always a Sunday.
   * 
   * @return First day of the week.
   */
  public Day getFirstDay() {
    return this.firstDay;
  }
  
  /**
   * Gets the last day of this Week, always a Saturday.
   * 
   * @return Last day of the week.
   */
  public Day getLastDay() {
    return this.lastDay;
  }

  /**
   * Returns a list with the seven Day instances in this Week.
   * 
   * @return A list of the Days in this Week.
   */
  public List<Day> getDays() {
    return this.days;  
  }
  
  /**
   * Calculate the hashcode for this Week.
   * 
   * @return This Week's hashcode.
   */
  @Override
  public int hashCode() {
    return this.firstDay.hashCode();  
  }
  
  /**
   * Returns true if the passed object is a Week and is equal to this Week.
   * 
   * @param obj Any object.
   * @return True if the two Week instances are the same.
   */
  @Override
  public boolean equals(Object obj) {
    return ((obj instanceof Week) &&
            (this.firstDay.equals(((Week) obj).firstDay)));
  }

  /**
   * Returns a String representation of this Week.
   * 
   * @return First day of the week.
   */
  public String getWeekRepresentation() {    
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
    return formatter.format(this.firstDay.getDate()) + " to " + 
           formatter.format(this.lastDay.getDate());
  }
  
  /**
   * First day of the week is chosen to represent the week in toString().
   * 
   * @return Week string.
   */
  @Override
  public String toString() {    
    return (new SimpleDateFormat("dd-MMM-yyyy", Locale.US)).format(this.lastDay.getDate());
  }
  
  /**
   * Returns a new Week instance representing the previous Week.
   * 
   * @return The previous Week.
   */
  public Week dec() {
    return new Week(this.firstDay.inc(-7));
  }
  
  /**
   * Returns a new Week instance representing the next Week.
   * 
   * @return The next Week.
   */
  public Week inc() {
    return new Week(this.lastDay.inc(7));
  }
  
  /**
   * Compares two Week objects.
   * 
   * @param obj Another week object.
   * @return Comparison of the first day.
   */
  public int compareTo(Object obj) {
    if (!(obj instanceof Week)) {
      return -1;
    }
    return this.firstDay.compareTo(((Week) obj).firstDay);
  }
}
