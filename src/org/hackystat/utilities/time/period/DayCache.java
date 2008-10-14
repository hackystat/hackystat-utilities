package org.hackystat.utilities.time.period;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Provides a cache mechanism for obtaining a single "canonical" Day instance associated with any
 * given UTC time stamp.  "Canonical" means that for any UTC value within a given day, the
 * same Day instance will be returned.  Performance monitoring has indicated that certain types of
 * analyses spend significant resources producing and discarding Day instances.  This class is
 * designed to lower the overhead of such computations by maintaining a cache of Day instances
 * that can be shared.  Note that the DayCache cannot be used in situations where the original UTC
 * value is needed later, since the Day instance that is returned will not return the original UTC
 * value through invocation of the getTime() method.
 * <P>
 * This class is a "helper" class to Day. It is used only within the constructors for Day. There
 * are no other clients to this class. Normal users should manipulate Day instances by calling the
 * associated Day.getInstance() methods.
 * <p>
 * DayCache implements a sliding "window" cache of 730 instances corresponding to 730 days.  The
 * middle instance corresponds to the day that this cache was first used (which normally
 * corresponds to the server startup day.  If the DayCache is asked for a Day instance outside
 * this window, then a  non-cached, regular Day instance is returned. The reason for this design
 * is to minimize object creation: we can do arithmetic to get the array index corresponding to a
 * UTC value.
 * <p>
 * Use only Day.equals() to compare two Day instances. Do not use '=='.
 * 
 * The Calendar is forced to Locale.US to ensure constant week boundaries. 
 * 
 * @author Philip M. Johnson
 * @version $Id: DayCache.java,v 1.1.1.1 2005/10/20 23:56:44 johnson Exp $
 */
public final class DayCache {
  /** The singleton DayCache instance. */
  private static DayCache theCache = new DayCache();

  /** Number of milliseconds in a day. */
  private long millisInDay = 1000 * 60 * 60 * 24;

  /** The first UTC time that will be stored in the cache, initialized in the constructor. */
  private long minUTC = 0;

  /** The last UTC time that will be stored in the cache, initialized in the constructor. */
  private long maxUTC = 0;

  /** The array of cached Day instances. day[365] is the day the server started. */
  private Day[] days = new Day[730];
  
  /** The date the DayCache was constructed. */
  private Date startupDate;

  /**
   * The singleton DayCache instance that provides access to a "window" of cached,  canonical Day
   * instances.  The cache is lazily instantiated.
   */
  private DayCache() {
    Calendar cal = Calendar.getInstance(Locale.US);
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    // I use 364 to avoid off by one errors at expense of thinking hard. :-)
    this.minUTC = cal.getTimeInMillis() - (364 * millisInDay);
    this.maxUTC = cal.getTimeInMillis() + (364 * millisInDay);
    
    this.startupDate = new Date();
  }

  /**
   * Returns the singleton DayCache.
   * 
   * <p>
   * To be used only by the Day class constructors.
   * </p>
   *
   * @return The DayCache.
   */
  static DayCache getInstance() {
    return theCache;
  }

  /**
   * Return the cached Day instance corresponding to timestamp if plus/minus 364 days from the day
   * the Cache was initialized.  Otherwise, returns a new Day instance.  Note that cached Day
   * instances will usually have an internal UTC value that is different  from timestamp.
   * 
   * <p>
   * To be used only by the Day class constructors.
   * </p>
   *
   * @param timestamp The UTC value falling within the Day of interest.
   *
   * @return The cached Day, or a new Day if it returns outside the cache.
   */
  Day getDay(long timestamp) {
    if ((timestamp >= maxUTC) || (timestamp <= minUTC)) {
      return new Day(timestamp);
    }
    else {
      // the dstSavings value is used to correct the timestamps that have added the Daylight
      // Savings Time to the UTC. the DST is usually configured in the OS by the user. 
      // if the system clock isn't DST enabled, dstSavings will be zero.
      int dstSavings = 0;
      if (TimeZone.getDefault().inDaylightTime(this.startupDate) && 
          !TimeZone.getDefault().inDaylightTime(new Date(timestamp))) {
        dstSavings = -TimeZone.getDefault().getDSTSavings();
      }
      else if (!TimeZone.getDefault().inDaylightTime(this.startupDate) && 
          TimeZone.getDefault().inDaylightTime(new Date(timestamp))) {
        dstSavings = TimeZone.getDefault().getDSTSavings();
      }
      int index = (int) (((timestamp + dstSavings) - minUTC ) / millisInDay);
      if (days[index] == null) {
        days[index] = new Day(timestamp);
      }

      return days[index];
    }
  }
}
