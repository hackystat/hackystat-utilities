package org.hackystat.utilities.logger;

import java.io.File;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;


/**
 * Supports logging of informational and error messages by this service.
 * @author Philip Johnson
 */
public class HackystatLogger {
  

  /**
   * Create a new Logger for Hackystat.  
   *
   * @param loggerName The name of the logger to create.
   */
  private HackystatLogger(String loggerName) {
    Logger logger = Logger.getLogger(loggerName);
    logger.setUseParentHandlers(false);

    // Define a file handler that writes to the ~/.hackystat/logs directory, creating it if nec.
    File logDir = new File(System.getProperty("user.home") + "/.hackystat/logs/");
    logDir.mkdirs();
    String fileName = logDir + "/" + loggerName + ".%u.log";
    FileHandler fileHandler;
    try {
      fileHandler = new FileHandler(fileName, 500000, 1, true);
      fileHandler.setFormatter(new OneLineFormatter());
      logger.addHandler(fileHandler);
    }
    catch (IOException e) {
      throw new RuntimeException("Could not open the log file for this Hackystat service.", e);
    }

    // Define a console handler to also write the message to the console.
    ConsoleHandler consoleHandler = new ConsoleHandler();
    consoleHandler.setFormatter(new OneLineFormatter());
    logger.addHandler(consoleHandler);
    setLoggingLevel(logger, "INFO");
  }

  
  /**
   * Return the Hackystat Logger named with loggerName, creating it if it does not yet exist.
   * Hackystat loggers have the following characteristics:
   * <ul>
   * <li> Log messages are one line and are prefixed with a time stamp using the OneLineFormatter
   * class.
   * <li> The logger creates a Console logger and a File logger. 
   * <li> The File logger is written out to the ~/.hackystat/logs/ directory, creating this if
   * it is not found.
   * <li> The File log name is {name}.%u.log.
   * </ul> 
   * @param loggerName The name of this HackystatLogger.
   * @return The Logger instance. 
   */
  public static Logger getLogger(String loggerName) {
    Logger logger = LogManager.getLogManager().getLogger(loggerName);
    if (logger == null) {
      new HackystatLogger(loggerName);
    }
    return LogManager.getLogManager().getLogger(loggerName);
  }

  /**
   * Sets the logging level to be used for this Hackystat logger.
   * If the passed string cannot be parsed into a Level, then INFO is set by default.
   * @param logger The logger whose level is to be set.  
   * @param level The new Level.
   */
  public static void setLoggingLevel(Logger logger, String level) {
    Level newLevel = Level.INFO;
    try {
      newLevel = Level.parse(level);
    }
    catch (Exception e) {
      logger.info("Couldn't set Logging level to: " + level);
    }
    logger.setLevel(newLevel);
    logger.getHandlers()[0].setLevel(newLevel);
    logger.getHandlers()[1].setLevel(newLevel);
  }
}

