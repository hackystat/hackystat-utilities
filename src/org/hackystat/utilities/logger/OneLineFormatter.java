package org.hackystat.utilities.logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

  /**
   * Provides a one line formatter for use with Hackystat logging. Supports optional date stamp
   * prefix. If the date stamp prefix is enabled, then a cr is also added.
   *
   * @author Philip Johnson
   */
public class OneLineFormatter extends Formatter {
  
  /** Whether or not to include the date stamp in the format string. */
  private boolean enableDateStamp = true;

  /**
   * Default constructor that enables the date stamp.
   */
  public OneLineFormatter () {
    this(true);
  }

  /**
   * One line format string with optional date stamp.
   * @param enableDateStamp If true, a date stamp is inserted. 
   */
  public OneLineFormatter(boolean enableDateStamp) {
    this.enableDateStamp = enableDateStamp;
  }

    /**
     * Formats the passed log string as a single line. Prefixes the log string with a date stamp.
     *
     * @param record  A log record.
     * @return The message string.
     */
  @Override
  public String format(LogRecord record) {
    StringBuffer  buff = new StringBuffer();
    if (this.enableDateStamp) {
      SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd HH:mm:ss", Locale.US);
      buff.append(dateFormat.format(new Date()));
      buff.append("  ");
    }
    buff.append(record.getMessage());
    buff.append(System.getProperty("line.separator"));
    return buff.toString();
  }
  }
  


