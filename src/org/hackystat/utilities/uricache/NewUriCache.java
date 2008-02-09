package org.hackystat.utilities.uricache;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.jcs.access.exception.CacheException;
import org.apache.jcs.engine.control.CompositeCacheManager;
import org.apache.jcs.JCS;
import org.hackystat.utilities.home.HackystatUserHome;
import org.hackystat.utilities.logger.HackystatLogger;

/**
 * Provides a wrapper around Apache JCS (Java Caching System) to facilitate Hackystat caching.
 * This wrapper provides the following:
 * <ul>
 * <li> Automatic configuration of an indexed disk cache backing store.
 * <li> Provides a default idle time for expiring of entries of one day.
 * <li> Provides a default maximum in-memory cache size of 50,000 instances before using backing
 * store.
 * <li> Ensures that all UriCache instances have a unique name and are created only once.
 * <li> A more convenient API for setting/getting items from the cache and controlling logging.
 * <li> Logging of exceptions raised by JCS.
 * <li> Disables JCS logging messages unless the System property 
 * org.hackystat.utilities.uricache.enableJCSLogging is set.
 * <li> Convenient packaging mechanism for required jar files to simplify use in Hackystat.
 * </ul> 
 * 
 * Here's an example usage, where we create a separate cache for each user to hold their sensor
 * data instances as part of the dailyprojectdata service.
 * <pre>
 * SensorBaseClient client = new SensorBaseClient(user, host);
 * NewUriCache cache = new UriCache(user.getEmail(), "dailyprojectdata");
 *   :
 * SensorData data = (SensorData)cache.get(uriString);
 * if (data == null) {
 *   // Cache doesn't have it, so retrieve from SensorBase and cache locally for next time.
 *   data = client.getSensorData(uriString);
 *   cache.put(uriString, data);
 * }
 * </pre>
 * The cache files are in the directory ~/.hackystat/dailyprojectdata/uricache.
 * Instances expire from the cache after one day, by default.
 * The maximum number of in-memory instances is 50,000, by default.  
 * 
 * @author Philip Johnson
 * @author Pavel Senin
 */
public class NewUriCache {
  
  /** Cache elements idle time. 24 hours is the default time, after that they are removed. */
  private static final Long defaultIdleTime = 86400L;
  /** Cache default in-memory maximum before sending items to disk. */
  private static final Long defaultCapacity = 50000L;
  /** The name of this cache, which defines a "region" in JCS terms. */
  private String cacheName = null;
  /** The logger used for cache exception logging. */
  private Logger logger = null;
  /** Holds a list of already defined caches to help ensure uniqueness. */
  private static ArrayList<String> cacheNames = new ArrayList<String>();
  
  /**
   * Creates a new UriCache instance with the specified name.
   * Good for services who want to create a single cache for themselves, such as "dailyprojectdata".
   * The cacheName must not have already been instantiated, otherwise we raise a runtime exception.
   * Logger, IdleTime and Capacity will have default values. 
   * @param cacheName The name of this cache.
   * @param subDir the .hackystat subdirectory in which the uricache directory holding the backing
   * store will be created.
   */
  public NewUriCache(String cacheName, String subDir) {
    this(cacheName, subDir, defaultIdleTime, defaultCapacity);
  }
  
  /**
   * Creates a new UriCache with the specified parameters. 
   * The cacheName must not have already been instantiated, otherwise we raise a runtime exception.
   * @param cacheName The name of this UriCache, which will be used as the JCS "region" and also
   * define the subdirectory in which the index files will live.
   * @param subDir the .hackystat subdirectory in which the uricache directory holding the backing
   * store will be created.
   * @param idleTime The idleTime before items expire from the cache.
   * @param capacity The number of in-memory instances to store before sending to disk.
   */
  public NewUriCache(String cacheName, String subDir, Long idleTime, Long capacity) {
    // Check to make sure we have not already instantiated a UriCache with this name.
    if (NewUriCache.cacheNames.contains(cacheName)) {
      throw new RuntimeException("Error: the cache region name is in use: " + cacheName);
    }
    else {
      NewUriCache.cacheNames.add(cacheName);
    }
    this.cacheName = cacheName;
    this.logger = HackystatLogger.getLogger(cacheName + ".uricache", subDir);
    if (!System.getProperties().containsKey("org.hackystat.utilities.uricache.enableJCSLogging")) {
      Logger.getLogger("org.apache.jcs").setLevel(Level.OFF);
    }
    CompositeCacheManager ccm = CompositeCacheManager.getUnconfiguredInstance(); 
    ccm.configure(initJcsProps(cacheName, subDir, idleTime, capacity));
  }
  
  /**
   * Adds the key-value pair to this cache.
   * Logs a message if the cache throws an exception.
   * @param key The key, typically a UriString.
   * @param value The value, typically the object returned from the Hackystat service.
   */
  public void put(Serializable key, Serializable value) {
    try {
      JCS.getInstance(this.cacheName).put(key, value);
    }
    catch (CacheException e) {
      String msg = "Failure to add " + key + " to cache " + this.cacheName + ":" + e.getMessage();
      this.logger.warning(msg);
    }
  }
  
  /**
   * Returns the object associated with key from the cache, or null if not found.
   * Logs a message if the cache throws an exception.
   * @param key The key whose associated value is to be retrieved.
   * @return The value, or null if not found.
   */
  public Object get(Serializable key) {
    try {
      return JCS.getInstance(this.cacheName).get(key);
    }
    catch (CacheException e) {
      String msg = "Failure of get: " + key + " in cache " + this.cacheName + ":" + e.getMessage();
      this.logger.warning(msg);
      return null;
    }
  }

  /**
   * Ensures that the key-value pair associated with key is no longer in this cache.
   * Logs a message if the cache throws an exception.
   * @param key The key to be removed. 
   */
  public void remove(Serializable key) {
    try {
      JCS.getInstance(this.cacheName).remove(key);
    }
    catch (CacheException e) {
      String msg = "Failure to remove: " + key + " cache " + this.cacheName + ":" + e.getMessage();
      this.logger.warning(msg);
    }
  }
  
  /**
   * Removes everything in this cache.
   */
  public void clear() {
    try {
      JCS.getInstance(this.cacheName).clear();
    }
    catch (CacheException e) {
      String msg = "Failure to clear cache " + this.cacheName + ":" + e.getMessage();
      this.logger.warning(msg);
    }
  }
  
  /**
   * Shuts down the specified cache, and removes it from the list of active caches so it can be
   * created again.
   * @param cacheName The name of the cache to dispose of.
   */
  public static void dispose(String cacheName) {
    try {
      cacheNames.remove(cacheName);
      JCS.getInstance(cacheName).dispose();
    }
    catch (CacheException e) {
      String msg = "Failure to clear cache " + cacheName + ":" + e.getMessage();
      System.out.println(msg);
    }
    
  }

  /**
   * Sets up the Properties instance for configuring this JCS cache instance.  Each UriCache is
   * defined as a JCS "region".  Given a UriCache named "PJ", we create a properties instance
   * whose contents are similar to the following:
   * <pre>
   * jcs.region.PJ=DC-PJ
   * jcs.region.PJ.cacheattributes=org.apache.jcs.engine.CompositeCacheAttributes
   * jcs.region.PJ.cacheattributes.MaxObjects=[maxCacheCapacity]
   * jcs.region.PJ.cacheattributes.MemoryCacheName=org.apache.jcs.engine.memory.lru.LRUMemoryCache
   * jcs.region.PJ.cacheattributes.UseMemoryShrinker=true
   * jcs.region.PJ.cacheattributes.MaxMemoryIdleTimeSeconds=3600
   * jcs.region.PJ.cacheattributes.ShrinkerIntervalSeconds=60
   * jcs.region.PJ.cacheattributes.MaxSpoolPerRun=500
   * jcs.region.PJ.elementattributes=org.apache.jcs.engine.ElementAttributes
   * jcs.region.PJ.elementattributes.IsEternal=false
   * jcs.region.PJ.elementattributes.MaxLifeSeconds=[maxIdleTime]
   * jcs.auxiliary.DC-PJ=org.apache.jcs.auxiliary.disk.indexed.IndexedDiskCacheFactory
   * jcs.auxiliary.DC-PJ.attributes=org.apache.jcs.auxiliary.disk.indexed.IndexedDiskCacheAttributes
   * jcs.auxiliary.DC-PJ.attributes.DiskPath=[cachePath]
   * jcs.auxiliary.DC-PJ.attributes.maxKeySize=1000000
   * </pre>
   * We define cachePath as HackystatHome.getHome()/.hackystat/[cacheSubDir]/cache.  This enables
   * a service a cache name of "dailyprojectdata" and have the cache data put inside its
   * internal subdirectory. 
   * 
   * See bottom of: http://jakarta.apache.org/jcs/BasicJCSConfiguration.html for more details.
   * 
   * @param cacheName The name of this cache, used to define the region properties.
   * @param subDir The subdirectory name, used to generate the disk storage directory.
   * @param maxIdleTime The maximum idle time, in seconds.
   * @param maxCacheCapacity The cache capacity before it goes to disk.
   * @return The properties file. 
   */
  private Properties initJcsProps(String cacheName, String subDir, Long 
      maxIdleTime, Long maxCacheCapacity) {
    String reg = "jcs.region." + cacheName;
    String regCacheAtt = reg + ".cacheattributes";
    String regEleAtt = reg + ".elementattributes";
    String aux = "jcs.auxiliary.DC-" + cacheName;
    String auxAtt = aux + ".attributes";
    String memName = "org.apache.jcs.engine.memory.lru.LRUMemoryCache";
    String diskAttName = "org.apache.jcs.auxiliary.disk.indexed.IndexedDiskCacheAttributes";
    Properties props = new Properties();
    props.setProperty(reg, "DC-" + cacheName);
    props.setProperty(regCacheAtt, "org.apache.jcs.engine.CompositeCacheAttributes");
    props.setProperty(regCacheAtt + ".MaxObjects", maxCacheCapacity.toString());
    props.setProperty(regCacheAtt + ".MemoryCacheName", memName);
    props.setProperty(regCacheAtt + ".UseMemoryShrinker", "true");
    props.setProperty(regCacheAtt + ".MaxMemoryIdleTimeSeconds", "3600");
    props.setProperty(regCacheAtt + ".ShrinkerIntervalSeconds", "60");
    props.setProperty(regCacheAtt + ".MaxSpoolPerRun", "500");
    props.setProperty(regEleAtt, "org.apache.jcs.engine.ElementAttributes");
    props.setProperty(regEleAtt + ".IsEternal", "false");
    props.setProperty(regEleAtt + ".MaxLifeSeconds", maxIdleTime.toString());
    props.setProperty(aux, "org.apache.jcs.auxiliary.disk.indexed.IndexedDiskCacheFactory");
    props.setProperty(auxAtt, diskAttName);
    props.setProperty(auxAtt + ".DiskPath", getCachePath(subDir));
    props.setProperty(auxAtt + ".maxKeySize", "100000");
    return props;
  }
  
  /**
   * Returns the fully qualified file path to the directory in which the backing store files for
   * this cache will be placed. Creates the path if it does not already exist.

   * @param cacheSubDir The subdirectory where we want to locate the cache files.
   * @return The fully qualified file path to the location where we should put the index files. 
   */
  private String getCachePath(String cacheSubDir) {
    File path = new File(HackystatUserHome.getHome(), ".hackystat/" + cacheSubDir + "/uricache");
    path.mkdirs();
    return path.getAbsolutePath();
  }
}
