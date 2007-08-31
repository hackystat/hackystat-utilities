package org.hackystat.utilities.uricache;

import java.util.Properties;

import org.apache.jcs.JCS;
import org.apache.jcs.access.exception.CacheException;
import org.apache.jcs.engine.control.CompositeCache;
import org.apache.jcs.engine.control.CompositeCacheManager;

/**
 * The factory for configuring and getting UriCache instances.
 * 
 * @author <a href="mailto:seninp@gmail.com">Pavel Senin<a>
 */
public class UriCacheFactory {

  /** The cache default region name. */
  public static final String uriCaheRegionName = "UriCache";

  /** The cache capacity. */
  public static Integer uriCacheMaxObjects = 500;

  /** The cache elements idle time. */
  public static Integer uriCacheIdleTime = 1200;

  /**
   * Constructs properties for the cache instantiation.
   * 
   * @return cache properties.
   */
  private static Properties setupProperties() {
    Properties prop = new Properties();
    // this is JCS required part - configuring default cache properties
    prop.setProperty("jcs.default", "");
    prop.setProperty("jcs.default.cacheattributes",
        "org.apache.jcs.engine.CompositeCacheAttributes");
    prop.setProperty("jcs.default.cacheattributes.MemoryCacheName",
        "org.apache.jcs.engine.memory.lru.LRUMemoryCache");
    prop.setProperty("jcs.default.cacheattributes.MaxObjects", "500");

    // UriCache region - elements won't go to disk, but should be marked as expired after five
    // seconds of non-use
    prop.setProperty("jcs.region.UriCache", "");
    prop.setProperty("jcs.region.UriCache.cacheattributes",
        "org.apache.jcs.engine.CompositeCacheAttributes");
    prop.setProperty("jcs.region.UriCache.cacheattributes.MemoryCacheName",
        " org.apache.jcs.engine.memory.lru.LRUMemoryCache");
    prop.setProperty("jcs.region.UriCache.cacheattributes.MaxObjects", uriCacheMaxObjects
        .toString());
    prop.setProperty("jcs.region.UriCache.cacheattributes.UseMemoryShrinker ", "true");
    prop.setProperty("jcs.region.UriCache.elementattributes",
        "org.apache.jcs.engine.ElementAttributes");
    prop.setProperty("jcs.region.UriCache.elementattributes.IsEternal ", "false");
    prop.setProperty("jcs.region.UriCache.elementattributes.IdleTime", uriCacheIdleTime.toString());
    return prop;
  }

  /**
   * Constructor, returns instance of UriCache which is configured according to configuration file
   * cache.ccf. <b>Note that this file must be in CLASSPATH environment.</b>
   * 
   * @return an URICache instance.
   * @throws UriCacheException when unable instantiate a cache.
   * @throws CacheException
   */
  public static UriCache getURICacheInstance() throws CacheException {
    // setup JCS
    CompositeCacheManager mgr = CompositeCacheManager.getUnconfiguredInstance();
    mgr.configure(setupProperties());

    // get access to bug test cache region
    JCS jcs = JCS.getInstance(uriCaheRegionName);
    CompositeCache cache = mgr.getCache(uriCaheRegionName);
  }

  /**
   * Reports the JCS region name.
   * 
   * @return cache region name.
   */
  public String getRegionName() {
    return uriCaheRegionName;
  }

}
