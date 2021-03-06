package org.hackystat.utilities.uricache;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.jcs.access.exception.CacheException;
import org.apache.jcs.engine.ElementAttributes;
import org.apache.jcs.engine.control.CompositeCacheManager;
import org.apache.jcs.JCS;
import org.hackystat.utilities.home.HackystatUserHome;
import org.hackystat.utilities.logger.HackystatLogger;

/**
 * Provides a wrapper around Apache JCS (Java Caching System) to facilitate Hackystat caching. This
 * wrapper provides the following:
 * <ul>
 * <li> Automatic configuration of an indexed disk cache backing store.
 * <li> Write-through caching: All cached instances are written out to disk. 
 * <li> Provides a default maximum life for expiring of entries of one day.
 * <li> Provides a default maximum cache size of 10000 instances.
 * <li> Provides a default directory location (inside ~/.hackystat) for backing store files. 
 * <li> Helps ensure that all UriCache instances have a unique name.
 * <li> All caches use the JCS "group" facility to allow access to the set of keys. 
 * <li> Constructor uses "days" rather than seconds as time unit for maxLife.
 * <li> put() uses "hours" rather than seconds as time unit for maxLife.
 * <li> A more convenient API for setting/getting items from the cache and controlling logging.
 * <li> Logging of exceptions raised by JCS.
 * <li> Disables JCS logging messages unless the System property
 * org.hackystat.utilities.uricache.enableJCSLogging is set.
 * <li> Shutdown hook ensures that backing index file is closed correctly on JVM exit. 
 * <li> Convenient packaging mechanism for required jar files to simplify library use.
 * </ul>
 * 
 * Here's an example usage, where we create a separate cache for each user to hold their sensor data
 * instances as part of the dailyprojectdata service.
 * 
 * <pre>
 * SensorBaseClient client = new SensorBaseClient(user, host);
 * UriCache cache = new UriCache(user.getEmail(), &quot;dailyprojectdata&quot;);
 *   :
 * SensorData data = (SensorData)cache.get(uriString);
 * if (data == null) {
 *   // Cache doesn't have it, so retrieve from SensorBase and cache locally for next time.
 *   data = client.getSensorData(uriString);
 *   cache.put(uriString, data);
 * }
 * </pre>
 * 
 * The cache files are in the directory ~/.hackystat/dailyprojectdata/uricache. Instances expire
 * from the cache after one day, by default. The maximum number of in-memory instances is 10,000, by
 * default.
 * 
 * @author Philip Johnson
 */
public class UriCache {
  
  /** The number of seconds in a day. * */
  private static final long secondsInADay = 60L * 60L * 24L;
  /** Maximum life in seconds that an entry stays in the cache. Default is 1 day. */
  private static final Long defaultMaxLifeSeconds = secondsInADay;
  /** Maximum number of in-memory instances before sending items to disk. Default is 50,000. */
  private static final Long defaultCapacity = 10000L;
  /** The name of this cache, which defines a "region" in JCS terms. */
  private String cacheName = null;
  /** The logger used for cache exception logging. */
  private Logger logger = null;
  /** Holds a list of already defined caches to help ensure uniqueness. */
  private static List<String> cacheNames = new ArrayList<String>();
  /** Default group name. No client should ever using the following string for a group. */
  private static final String DEFAULT_GROUP = "__Default_UriCache_Group__";
  
  private static final String failureMsg = "Failure to clear cache ";
  
  /** A thread that will ensure that all of these caches will be disposed of during shutdown. */ 
  private static Thread shutdownThread = new Thread() {
    /** Run the shutdown hook for disposing of all caches. */
    @Override 
    public void run() {
      for (String cacheName : cacheNames) {
        try {
          System.out.println("Shutting down " + cacheName + " cache.");
          JCS.getInstance(cacheName).dispose();
        }
        catch (Exception e) {
          String msg = failureMsg + cacheName + ":" + e.getMessage();
          System.out.println(msg);
        }
      }
    }
  };
  
  /** A boolean that enables us to figure out whether to install the shutdown thread.  */
  private static boolean hasShutdownHook = false;  //NOPMD
  
  /**
   * Creates a new UriCache instance with the specified name. Good for services who want to create a
   * single cache for themselves, such as "dailyprojectdata". If the cache with this name has been 
   * previously created, then this instance will become an alias to the previously existing cache. 
   * 
   * @param cacheName The name of this cache.
   * @param subDir the .hackystat subdirectory in which the uricache directory holding the backing
   *        store will be created.
   */
  public UriCache(String cacheName, String subDir) {
    this(cacheName, subDir, new Double(defaultMaxLifeSeconds), defaultCapacity);
  }
  
  /**
   * Creates a new UriCache with the specified parameters. 
   * If a cache with this name already exists, then this instance will be an alias to that cache
   * and its original configuration will remain unchanged. 
   * 
   * @param cacheName The name of this UriCache, which will be used as the JCS "region" and also
   *        define the subdirectory in which the index files will live.
   * @param subDir the .hackystat subdirectory in which the uricache directory holding the backing
   *        store will be created.
   * @param maxLifeDays The maximum number of days after which items expire from the cache.
   * @param capacity The maximum number of instances to hold in the cache. 
   */
  public UriCache(String cacheName, String subDir, Double maxLifeDays, Long capacity) {
    // Set up the shutdown hook if we're the first one. Not thread safe, but there's not too
    // much harm done if there are multiple shutdown hooks running.
    if (!UriCache.hasShutdownHook) {
      Runtime.getRuntime().addShutdownHook(UriCache.shutdownThread);
      UriCache.hasShutdownHook = true;
    }
    this.cacheName = cacheName;
    this.logger = HackystatLogger.getLogger(cacheName + ".uricache", subDir);

    // Finish configuration if this is a new instance of the cache.
    if (!UriCache.cacheNames.contains(cacheName)) {
      UriCache.cacheNames.add(cacheName);
      if (!System.getProperties().containsKey(
          "org.hackystat.utilities.uricache.enableJCSLogging")) {
        Logger.getLogger("org.apache.jcs").setLevel(Level.OFF);
      }
      CompositeCacheManager ccm = CompositeCacheManager.getUnconfiguredInstance();
      long maxLifeSeconds = (long) (maxLifeDays * secondsInADay);
      ccm.configure(initJcsProps(cacheName, subDir, maxLifeSeconds, capacity));
    }
  }
  
  /**
   * Adds the key-value pair to this cache. Entry will expire from cache after the default maxLife
   * (currently 24 hours). Logs a message if the cache throws an exception.
   * 
   * @param key The key, typically a UriString.
   * @param value The value, typically the object returned from the Hackystat service.
   */
  public void put(Serializable key, Serializable value) {
    try {
      JCS.getInstance(this.cacheName).putInGroup(key, DEFAULT_GROUP, value);
    }
    catch (CacheException e) {
      String msg = "Failure to add " + key + " to cache " + this.cacheName + ":" + e.getMessage();
      this.logger.warning(msg);
    }
  }
  
  /**
   * Adds the key-value pair to this cache with an explicit expiration time.  
   * 
   * @param key The key, typically a UriString.
   * @param value The value, typically the object returned from the Hackystat service.
   * @param maxLifeHours The number of hours before this item will expire from cache.
   */
  public void put(Serializable key, Serializable value, double maxLifeHours) {
    try {
      ElementAttributes attributes = new ElementAttributes();
      long maxLifeSeconds = (long)(maxLifeHours * 3600D);
      attributes.setMaxLifeSeconds(maxLifeSeconds);
      attributes.setIsEternal(false);
      JCS.getInstance(this.cacheName).putInGroup(key, DEFAULT_GROUP, value, attributes);
    }
    catch (CacheException e) {
      String msg = "Failure to add " + key + " to cache " + this.cacheName + ":" + e.getMessage();
      this.logger.warning(msg);
    }
  }
  
  /**
   * Returns the object associated with key from the cache, or null if not found. 
   * 
   * @param key The key whose associated value is to be retrieved.
   * @return The value, or null if not found.
   */
  public Object get(Serializable key) {
    try {
      return JCS.getInstance(this.cacheName).getFromGroup(key, DEFAULT_GROUP);
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
   * 
   * @param key The key to be removed.
   */
  public void remove(Serializable key) {
    try {
      JCS.getInstance(this.cacheName).remove(key, DEFAULT_GROUP);
    }
    catch (CacheException e) {
      String msg = "Failure to remove: " + key + " cache " + this.cacheName + ":" + e.getMessage();
      this.logger.warning(msg);
    }
  }
  
  /**
   * Removes everything in the default cache, but not any of the group caches. 
   */
  public void clear() {
    clearGroup(DEFAULT_GROUP);
  }

  /**
   * Clears the default as well as all group caches. 
   */
  public void clearAll() {
    try {
      JCS.getInstance(this.cacheName).clear();
    }
    catch (CacheException e) {
      String msg = failureMsg + this.cacheName + ":" + e.getMessage();
      this.logger.warning(msg);
    }
  }

  /**
   * Returns the set of keys associated with this cache. 
   * @return The set containing the keys for this cache. 
   */
  public Set<Serializable> getKeys() {
    return getGroupKeys(DEFAULT_GROUP);
  }
  
  /**
   * Returns the current number of elements in this cache. 
   * @return The current size of this cache. 
   */
  public int size() {
    return getGroupSize(DEFAULT_GROUP);
  }
  
  
  /**
   * Shuts down the specified cache, and removes it from the list of active caches so it can be
   * created again.
   * 
   * @param cacheName The name of the cache to dispose of.
   */
  public static void dispose(String cacheName) {
    try {
      cacheNames.remove(cacheName);
      JCS.getInstance(cacheName).dispose();
    }
    catch (CacheException e) {
      String msg = failureMsg + cacheName + ":" + e.getMessage();
      System.out.println(msg);
    }
  }
  
  
  /**
   * Implements group-based addition of cache elements.
   * @param key The key.
   * @param group The group.
   * @param value The value.
   */
  public void putInGroup(Serializable key, String group, Serializable value) {
    try {
      JCS.getInstance(this.cacheName).putInGroup(key, group, value);
    }
    catch (CacheException e) {
      String msg = "Failure to add " + key + " to cache " + this.cacheName + ":" + e.getMessage();
      this.logger.warning(msg);
    }
  }

  /**
   * Implements group-based retrieval of cache elements. 
   * @param key The key.
   * @param group The group.
   * @return The element associated with key in the group, or null.
   */
  public Object getFromGroup(Serializable key, String group) {
    try {
      return JCS.getInstance(this.cacheName).getFromGroup(key, group);
    }
    catch (CacheException e) {
      String msg = "Failure of get: " + key + " in cache " + this.cacheName + ":" + e.getMessage();
      this.logger.warning(msg);
      return null;
    }
  }
  
  /**
   * Implements group-based removal of cache elements. 
   * @param key The key whose value is to be removed. 
   * @param group The group.
   */
  public void removeFromGroup(Serializable key, String group) {
    try {
      JCS.getInstance(this.cacheName).remove(key, group);
    }
    catch (CacheException e) {
      String msg = "Failure to remove: " + key + " cache " + this.cacheName + ":" + e.getMessage();
      this.logger.warning(msg);
    }
  }
  
  /**
   * Returns the set of cache keys associated with this group.
   * @param group The group.
   * @return The set of cache keys for this group.
   */
  @SuppressWarnings("unchecked")
  public Set<Serializable> getGroupKeys(String group) {
    Set<Serializable> keySet;
    try {
      keySet = JCS.getInstance(this.cacheName).getGroupKeys(group);
    }
    catch (CacheException e) {
      String msg = "Failure to obtain keyset for cache: " + this.cacheName;
      this.logger.warning(msg);
      keySet = new HashSet<Serializable>();
    }
    return keySet;
  }
  
  /**
   * Returns the current number of elements in this cache group.
   * @param group The name of the group.  
   * @return The current size of this cache. 
   */
  public int getGroupSize(String group) {
    return getGroupKeys(group).size();
  }
 
  /**
   * Removes everything in the specified group.
   * @param group The group name.  
   */
  public void clearGroup(String group) {
    try {
      JCS cache = JCS.getInstance(this.cacheName);
      for (Object key : cache.getGroupKeys(group)) {
        cache.remove(key, group);
      }
    }
    catch (CacheException e) {
      String msg = failureMsg + this.cacheName + ":" + e.getMessage();
      this.logger.warning(msg);
    }
  }

  /**
   * Sets up the Properties instance for configuring this JCS cache instance. Each UriCache is
   * defined as a JCS "region". Given a UriCache named "PJ", we create a properties instance whose
   * contents are similar to the following:
   * 
   * <pre>
   * jcs.region.PJ=DC-PJ
   * jcs.region.PJ.cacheattributes=org.apache.jcs.engine.CompositeCacheAttributes
   * jcs.region.PJ.cacheattributes.MaxObjects=[maxCacheCapacity]
   * jcs.region.PJ.cacheattributes.MemoryCacheName=org.apache.jcs.engine.memory.lru.LRUMemoryCache
   * jcs.region.PJ.cacheattributes.UseMemoryShrinker=true
   * jcs.region.PJ.cacheattributes.MaxMemoryIdleTimeSeconds=3600
   * jcs.region.PJ.cacheattributes.ShrinkerIntervalSeconds=3600
   * jcs.region.PJ.cacheattributes.MaxSpoolPerRun=500
   * jcs.region.PJ.elementattributes=org.apache.jcs.engine.ElementAttributes
   * jcs.region.PJ.elementattributes.IsEternal=false
   * jcs.region.PJ.elementattributes.MaxLifeSeconds=[maxIdleTime]
   * jcs.auxiliary.DC-PJ=org.apache.jcs.auxiliary.disk.indexed.IndexedDiskCacheFactory
   * jcs.auxiliary.DC-PJ.attributes=org.apache.jcs.auxiliary.disk.indexed.IndexedDiskCacheAttributes
   * jcs.auxiliary.DC-PJ.attributes.DiskPath=[cachePath]
   * jcs.auxiliary.DC-PJ.attributes.maxKeySize=10000000
   * </pre>
   * 
   * We define cachePath as HackystatHome.getHome()/.hackystat/[cacheSubDir]/cache. This enables a
   * service a cache name of "dailyprojectdata" and have the cache data put inside its internal
   * subdirectory.
   * 
   * See bottom of: http://jakarta.apache.org/jcs/BasicJCSConfiguration.html for more details.
   * 
   * @param cacheName The name of this cache, used to define the region properties.
   * @param subDir The subdirectory name, used to generate the disk storage directory.
   * @param maxLifeSeconds The maximum life of instances in the cache in seconds before they expire.
   * @param maxCapacity The maximum size of this cache.
   * @return The properties file.
   */
  private Properties initJcsProps(String cacheName, String subDir, Long maxLifeSeconds, 
      Long maxCapacity) {
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
    props.setProperty(regCacheAtt + ".MaxObjects", maxCapacity.toString());
    props.setProperty(regCacheAtt + ".MemoryCacheName", memName);
    props.setProperty(regCacheAtt + ".UseMemoryShrinker", "true");
    props.setProperty(regCacheAtt + ".MaxMemoryIdleTimeSeconds", "3600");
    props.setProperty(regCacheAtt + ".ShrinkerIntervalSeconds", "3600");
    props.setProperty(regCacheAtt + ".DiskUsagePatternName", "UPDATE");
    props.setProperty(regCacheAtt + ".MaxSpoolPerRun", "500");
    props.setProperty(regEleAtt, "org.apache.jcs.engine.ElementAttributes");
    props.setProperty(regEleAtt + ".IsEternal", "false");
    props.setProperty(regEleAtt + ".MaxLifeSeconds", maxLifeSeconds.toString());
    props.setProperty(aux, "org.apache.jcs.auxiliary.disk.indexed.IndexedDiskCacheFactory");
    props.setProperty(auxAtt, diskAttName);
    props.setProperty(auxAtt + ".DiskPath", getCachePath(subDir));
    props.setProperty(auxAtt + ".maxKeySize", "1000000");
    return props;
  }
  
  /**
   * Returns the fully qualified file path to the directory in which the backing store files for
   * this cache will be placed. Creates the path if it does not already exist.
   * 
   * @param cacheSubDir The subdirectory where we want to locate the cache files.
   * @return The fully qualified file path to the location where we should put the index files.
   */
  private String getCachePath(String cacheSubDir) {
    File path = new File(HackystatUserHome.getHome(), ".hackystat/" + cacheSubDir + "/uricache");
    boolean dirsOk = path.mkdirs();
    if (!dirsOk && !path.exists()) {
      throw new RuntimeException("mkdirs() failed");
    }
    return path.getAbsolutePath();
  }
  
  /**
   * Sets the logging level for this logger to level.
   * @param level A string indicating the level, such as "FINE", "INFO", "ALL", etc.
   */
  public void setLoggingLevel(String level) {
    HackystatLogger.setLoggingLevel(this.logger, level);
  }
}
