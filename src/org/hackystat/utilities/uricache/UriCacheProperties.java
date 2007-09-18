package org.hackystat.utilities.uricache;

import java.util.Properties;
import java.util.logging.Level;

/**
 * Provides configuration properties for the UriCache library.
 * 
 * @author Pavel Senin.
 * 
 */
public class UriCacheProperties {

  /** Cache default region name. */
  private String uriCaheRegionName = "UriCache";

  /** Cache elements idle time. 24 hours default time. */
  private Long maxIdleTime = 86400L;

  /** Cache default capacity. */
  private Long maxMemoryCacheCapacity = 50000L;

  /** JCS cache configuration properties. */
  private Properties cacheProperties = null;

  /** JCS DC plug-in storage path. */
  private String dcStoragePath = System.getProperties().getProperty("java.io.tmpdir");

  /** the internal properties handler */
  private Properties prop;

  /** UriCache logging level */
  private Level loggerLevel = Level.OFF;

  /**
   * Creates new instance and sets default properties.
   */
  public UriCacheProperties() {
    cacheProperties = setupProperties();
  }

  /**
   * Sets maximum idle time for this cache items.
   * 
   * @param idleTime the item idle time.
   */
  public void setMaxIdleTime(Long idleTime) {
    this.maxIdleTime = idleTime;
  }

  /**
   * Sets maximum capacity for this cache.
   * 
   * @param maxMemoryCacheCapacity maximal cache capacity.
   */
  public void setMaxMemoryCacpacity(Long maxMemoryCacheCapacity) {
    this.maxMemoryCacheCapacity = maxMemoryCacheCapacity;
  }

  /**
   * Sets the path to the cache storage folder.
   * 
   * @param storagePath the cache storage folder.
   */
  public void setCacheStoragePath(String storagePath) {
    this.dcStoragePath = storagePath;
    prop.setProperty("jcs.auxiliary.indexedDiskCache.attributes.DiskPath", this.dcStoragePath);
  }

  /**
   * Constructs configuration properties for the cache instance.
   * 
   * @return cache properties.
   */
  private Properties setupProperties() {

    prop = new Properties();
    //
    // this is JCS required part - configuring default cache properties
    //
    prop.setProperty("jcs.default", "indexedDiskCache");
    prop.setProperty("jcs.default.cacheattributes",
        "org.apache.jcs.engine.CompositeCacheAttributes");
    prop.setProperty("jcs.default.cacheattributes.MaxObjects", this.maxMemoryCacheCapacity
        .toString());
    prop.setProperty("jcs.default.cacheattributes.MemoryCacheName",
        "org.apache.jcs.engine.memory.lru.LRUMemoryCache");
    //
    // ------- UriCache region cache attributes ---------
    //
    prop.setProperty("jcs.region." + this.uriCaheRegionName, "indexedDiskCache");
    prop.setProperty("jcs.region." + this.uriCaheRegionName + ".cacheattributes",
        "org.apache.jcs.engine.CompositeCacheAttributes");
    prop.setProperty("jcs." + this.uriCaheRegionName + ".cacheattributes.MaxObjects",
        this.maxMemoryCacheCapacity.toString());
    prop.setProperty("jcs.region." + this.uriCaheRegionName + ".cacheattributes.MemoryCacheName",
        "org.apache.jcs.engine.memory.lru.LRUMemoryCache");
    prop.setProperty("jcs.region." + this.uriCaheRegionName
        + ".cacheattributes.ShrinkerIntervalSeconds", "300");
    prop.setProperty("jcs.region." + this.uriCaheRegionName + ".cacheattributes.UseMemoryShrinker",
        "true");
    prop.setProperty("jcs.region." + this.uriCaheRegionName + ".cacheattributes.UseDisk", "true");
    prop.setProperty("jcs.region." + this.uriCaheRegionName + 
        ".cacheattributes.UseRemote", "false");
    prop.setProperty("jcs.region." + this.uriCaheRegionName + ".cacheattributes.UseLateral",
        "false");
    prop.setProperty("jcs.region." + this.uriCaheRegionName + ".cacheattributes.MaxSpoolPerRun",
        "500");
    //
    // -------- elements attributes --------
    //
    prop.setProperty("jcs.region.UriCache.elementattributes",
        "org.apache.jcs.engine.ElementAttributes");
    prop.setProperty("jcs.region.UriCache.elementattributes.IsEternal", "false");
    prop.setProperty("jcs.region.UriCache.elementattributes.IsLateral", "false");
    prop.setProperty("jcs.region.UriCache.elementattributes.MaxLifeSeconds", this.maxIdleTime
        .toString());
    prop.setProperty("jcs.region.UriCache.elementattributes.IdleTime", this.maxIdleTime.toString());
    //
    // -------- disk cache elements attributes --------
    //
    prop.setProperty("jcs.auxiliary.indexedDiskCache",
        "org.apache.jcs.auxiliary.disk.indexed.IndexedDiskCacheFactory");
    prop.setProperty("jcs.auxiliary.indexedDiskCache.attributes",
        "org.apache.jcs.auxiliary.disk.indexed.IndexedDiskCacheAttributes");
    prop.setProperty("jcs.auxiliary.indexedDiskCache.attributes.MaxPurgatorySize", "300000");
    prop.setProperty("jcs.auxiliary.indexedDiskCache.attributes.MaxKeySize", "500000");
    prop.setProperty("jcs.auxiliary.indexedDiskCache.attributes.MaxRecycleBinSize", "50000");
    prop.setProperty("jcs.auxiliary.indexedDiskCache.attributes.OptimizeAtRemoveCount", "300000");
    prop.setProperty("jcs.auxiliary.indexedDiskCache.attributes.EventQueueType", "SINGLE");
    prop.setProperty("jcs.auxiliary.indexedDiskCache.attributes.EventQueuePoolName",
        "disk_cache_event_queue");
    //
    // --------- disk cache tricks
    //
    prop.setProperty("thread_pool.default.boundarySize", "2000");
    prop.setProperty("thread_pool.default.maximumPoolSize", "150");
    prop.setProperty("thread_pool.default.minimumPoolSize", "4");
    prop.setProperty("thread_pool.default.keepAliveTime", "350000");
    prop.setProperty("thread_pool.default.whenBlockedPolicy", "RUN");
    prop.setProperty("thread_pool.default.startUpSize", "4");
    prop.setProperty("thread_pool.disk_cache_event_queue.useBoundary", "false");
    prop.setProperty("thread_pool.disk_cache_event_queue.minimumPoolSize", "2");
    prop.setProperty("thread_pool.disk_cache_event_queue.keepAliveTime", "3500");
    prop.setProperty("thread_pool.disk_cache_event_queue.startUpSize", "10");

    return prop;
  }

  /**
   * Reports full set of configuration properties.
   * 
   * @return configuration properties.
   */
  public Properties getProperties() {
    return this.cacheProperties;
  }

  /**
   * Reports this class default region name.
   * 
   * @return this cache region name.
   */
  public String getCacheRegionName() {
    return this.uriCaheRegionName;
  }

  /**
   * Sets the cache region name.
   * 
   * @param cacheName name to set.
   */
  public void setCacheRegionName(String cacheName) {
    this.uriCaheRegionName = cacheName;
  }

  /**
   * Reports the logger level set in the properties.
   * 
   * @return logger level.
   */
  public Level getLoggerLevel() {
    return this.loggerLevel;
  }

  /**
   * Set the logger level.
   * 
   * @param level logger level to set.
   */
  public void setLoggerLevel(Level level) {
    this.loggerLevel = level;
  }

  // /**
  // * Constructs configuration properties for the cache instance.
  // *
  // * @return cache properties.
  // */
  // private Properties setupProperties() {
  // Properties prop = new Properties();
  // // this is JCS required part - configuring default cache properties
  // prop.setProperty("jcs.default", "");
  // prop.setProperty("jcs.default.cacheattributes",
  // "org.apache.jcs.engine.CompositeCacheAttributes");
  // prop.setProperty("jcs.default.cacheattributes.MemoryCacheName",
  // "org.apache.jcs.engine.memory.lru.LRUMemoryCache");
  // prop.setProperty("jcs.default.cacheattributes.MaxObjects", "500");
  //
  // // UriCache region - elements won't go to disk, but should be marked as expired after five
  // // seconds of non-use
  // prop.setProperty("jcs.region.UriCache", "");
  // prop.setProperty("jcs.region.UriCache.cacheattributes.MemoryCacheName",
  // "org.apache.jcs.engine.memory.lru.LRUMemoryCache");
  //
  // prop.setProperty("jcs.region.UriCache.cacheattributes",
  // "org.apache.jcs.engine.CompositeCacheAttributes");
  // prop.setProperty("jcs.region.UriCache.cacheattributes.MaxObjects", this.uriCacheMaxObjects
  // .toString());
  // prop.setProperty("jcs.region.UriCache.cacheattributes.ShrinkerIntervalSeconds", "60");
  // prop.setProperty("jcs.region.UriCache.cacheattributes.UseMemoryShrinker", "true");
  // prop.setProperty("jcs.region.UriCache.cacheattributes.UseDisk", "false");
  // prop.setProperty("jcs.region.UriCache.cacheattributes.UseRemote", "false");
  // prop.setProperty("jcs.region.UriCache.cacheattributes.UseLateral", "false");
  //
  // prop.setProperty("jcs.region.UriCache.elementattributes",
  // "org.apache.jcs.engine.ElementAttributes");
  // prop.setProperty("jcs.region.UriCache.elementattributes.IsEternal", "false");
  // prop.setProperty("jcs.region.UriCache.elementattributes.IsLateral", "false");
  // prop.setProperty("jcs.region.UriCache.elementattributes.MaxLifeSeconds", this.uriCacheIdleTime
  // .toString());
  // prop.setProperty("jcs.region.UriCache.elementattributes.IdleTime", this.uriCacheIdleTime
  // .toString());
  // return prop;
  // }
}
