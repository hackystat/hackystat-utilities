package org.hackystat.utilities.time.period;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Provides the Month abstract data type, which represents the collection of Week and Day
 * instances in a given month.
 * 
 * The Calendar is forced to Locale.US to ensure constant week boundaries. 
 * 
 * @author Hongbing Kou
 */
public class Month implements TimePeriod {
  /** Calendar year. */
  private int year;
  /** Calendar month. */
  private int month;
  /** Number of days in this month. */
  private int numOfDays;
  /** First day of the month. */
  private Day firstDay;
  /** Last day of the month. */
  private Day lastDay;
  /** Days' list in a month. */
  private List<Day> days;  
  
  /**
   * Creates a Month instance for the given year and month.
   * 
   * @param year The calendar year, such as 2004.
   * @param month The zero-based calendar month, such as 0 (January) or 11 (December).
   */
  public Month(int year, int month) {
    this.year = year;
    this.month = month;
    
    Calendar cal = Calendar.getInstance(Locale.US);
    cal.set(Calendar.DAY_OF_MONTH, 1);
    cal.set(Calendar.YEAR, this.year);
    cal.set(Calendar.MONTH, this.month);

    this.numOfDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    this.firstDay = Day.getInstance(year, month, 1);    
    this.lastDay = Day.getInstance(year, month, this.numOfDays);
    
    this.days = new ArrayList<Day>();
    for (int i = 0; i < this.numOfDays; i++) {
      this.days.add(Day.getInstance(year, month, i));
    }
  }
  
  /**
   * Returns the year associated with this Month.
   * 
   * @return The year, such as 2004.
   */
  public int getYear() {
    return this.year;
  }
  
  /**
   * Returns the zero-based month associated with this Month.
   * 
   * @return The month, such as 0 (January) or 11 (December).
   */
  public int getMonth() {
    return this.month;
  }
 
  /**
   * Returns the first Day instance associated with this Month.
   * 
   * @return First Day of this Month.
   */
  public Day getFirstDay() {
    return this.firstDay;
  }

  /**
   * Returns the Week instance associated with the first day in this Month.
   * Note that the Week instance may include Day instances from the prior Month.
   * 
   * @return The Week instance that includes the first Day of this Month.
   */
  public Week getFirstWeekInMonth() {
    return new Week(this.firstDay);
  }
    
  /**
   * Returns the last Day instance associated with this Month.
   * 
   * @return Last day of the month.
   */
  public Day getLastDay() {
    return this.lastDay;
  }

  /**
   * Returns the Week instance associated with the last Day of this Month. 
   * Note that the Week instance may include Day instances from the next Month.
   * 
   * @return Last week in the month.
   */
  public Week getLastWeekInMonth() {
    return new Week(this.lastDay);
  }

  /**
   * Returns the number of days in this Month.
   * 
   * @return Days in this month.
   */
  public int getNumOfDays() {
    return this.numOfDays;    
  }
  
  /**
   * Compares two Month instances.
   * 
   * @param o Another month object.
   * @return Comparison result.
   */
  public int compareTo(Object o) {
    Month month = (Month) o;
    
    if (this.year == month.year) { 
      return this.month - month.month; 
    }
    else {
      return this.year - month.year;
    }
  }

  /**
   * Returns the Month instance prior to this Month.
   * 
   * @return Previous month.
   */
  public Month dec() {
    if (this.month == 0) {
      return new Month(this.year - 1, 11);    
    }
    else {
      return new Month(this.year, this.month - 1);
    }     
  }
   
  /**
   * Returns the Month instance after this Month.
   * 
   * @return Next month.
   */
  public Month inc() {
    if (this.month == 11) {
      return new Month(this.year + 1, 0);    
    }
    else {
      return new Month(this.year, this.month + 1);
    }     
  }
  
  /**
   * Hash code of this Month.
   * 
   * @return Month's hashcode.
   */
  @Override
  public int hashCode() {
    String monthString = "" + this.year + "" + this.month;
    return monthString.hashCode();
  }
  
  /**
   * Returns true if the passed object is a month and is equal to this Month.
   * 
   * @param obj Any object.
   * @return True if two months are the equal.
   */
  @Override
  public boolean equals(Object obj) {
    return ((obj instanceof Month) && 
            (this.year == ((Month) obj).year && this.month == ((Month) obj).month));  
  }
  
  /**
   * String representation of this Month.
   * 
   * @return Month string.
   */
  @Override
  public String toString() {
    SimpleDateFormat formatter = new SimpleDateFormat("MMM-yyyy", Locale.US);
    Day day = Day.getInstance(this.year, this.month, 1);
    return formatter.format(day.getDate());
  }

  /**
   * Returns a list of Day instances from the first Day to the last Day in this Month.
   * 
   * @return A sorted list of the Days in this Month.
   */
  public List<Day> getDays() {
    return this.days;
  }
  
  
}
