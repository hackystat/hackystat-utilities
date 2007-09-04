package org.hackystat.utilities.uricache;

import java.util.Properties;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.jcs.JCS;
import org.apache.jcs.access.exception.CacheException;
import org.apache.jcs.engine.CompositeCacheAttributes;
import org.apache.jcs.engine.ElementAttributes;
import org.apache.jcs.engine.control.CompositeCache;
import org.apache.jcs.engine.control.CompositeCacheManager;

/**
 * Provides an easy caching mechanism which is backed by Apache JCS (Java Caching System). Once
 * cache configured and initialized, it caches pairs &lt;String, Object&gt;.
 * 
 * <br/><br/> Using an UriCache has several advantages: it increases performance as it reduces URL
 * lookups.
 * 
 * @author <a href="mailto:seninp@gmail.com">Pavel Senin<a>
 */
public class UriCache {

  /** Cache default region name. */
  private static final String uriCaheRegionName = "UriCache";

  /** JCS cache handler */
  private JCS uriCache;

  /** Cache elements idle time. */
  private Integer uriCacheIdleTime = 86400;

  /** Cache default capacity. */
  private Integer uriCacheMaxObjects = 500;

  /** JCS cache configuration properties. */
  private Properties uriCacheProperties = null;

  /**
   * Constructor, returns a default instance of UriCache.
   * 
   * @throws UriCacheException when unable instantiate a cache.
   */
  public UriCache() throws UriCacheException {
    // setup JCS
    CompositeCacheManager mgr = CompositeCacheManager.getUnconfiguredInstance();
    uriCacheProperties = setupProperties();
    mgr.configure(uriCacheProperties);
    // get access to bug test cache region
    try {
      this.uriCache = JCS.getInstance(uriCaheRegionName);
      @SuppressWarnings("unused")
      CompositeCache cache = mgr.getCache(uriCaheRegionName);
    }
    catch (CacheException e) {
      throw new UriCacheException(e.toString());
    }
  }

  /**
   * Constructor, returns a customized instance of UriCache.
   * 
   * @param maxCacheElements specifies the cache capacity.
   * @param maxLifeSeconds specifies the memory idle time for the cache elements, once this time
   *        exceeded, cached object will be removed from the cache.
   * @throws UriCacheException when unable instantiate a cache.
   */
  public UriCache(Integer maxCacheElements, Integer maxLifeSeconds) throws UriCacheException {

    this.uriCacheMaxObjects = maxCacheElements;
    this.uriCacheIdleTime = maxLifeSeconds;

    // setup JCS
    CompositeCacheManager mgr = CompositeCacheManager.getUnconfiguredInstance();
    uriCacheProperties = setupProperties();
    mgr.configure(uriCacheProperties);

    // get access to bug test cache region
    try {
      this.uriCache = JCS.getInstance(uriCaheRegionName);
      @SuppressWarnings("unused")
      CompositeCache cache = mgr.getCache(uriCaheRegionName);
    }
    catch (CacheException e) {
      throw new UriCacheException(e.toString());
    }
  }

  /**
   * Place a new object in the cache, associated with uriString. If there is currently an object
   * associated with the same URI it is replaced.
   * 
   * @param uriString identity (URI) of the object to cache.
   * @param obj The object to cache.
   * @throws UriCacheException in case of error.
   */
  public void cache(String uriString, Object obj) throws UriCacheException {
    try {
      this.uriCache.put(uriString, obj);
    }
    catch (CacheException e) {
      throw new UriCacheException(e);
    }
  }

  /**
   * Puts object into cache and specifies object lifetime within the cache.
   * 
   * @param uriString Identity of the object to cache.
   * @param obj Object to cache.
   * @param expirationTimeStamp Object expiration timestamp, object will be removed from the cache
   *        once System time is greater than the timestamp value.
   * @throws UriCacheException in case of error.
   */
  public void cache(String uriString, Object obj, XMLGregorianCalendar expirationTimeStamp)
      throws UriCacheException {

    // first of all calculating the life time of this object from now
    Long currTime = System.currentTimeMillis();
    Long lifeTime = ((Integer) expirationTimeStamp.getMillisecond()).longValue() - currTime;

    ElementAttributes attr = new ElementAttributes();
    attr.setMaxLifeSeconds(lifeTime);

    try {
      this.uriCache.put(uriString, obj, attr);
    }
    catch (CacheException e) {
      throw new UriCacheException(e);
    }
  }

  /**
   * Removes all elements from the cache.
   * 
   * @throws UriCacheException in case of error.
   */
  public void clear() throws UriCacheException {
    if (this.uriCache != null) {
      try {
        this.uriCache.clear();
      }
      catch (CacheException e) {
        throw new UriCacheException(e);
      }
    }
  }

  /**
   * Retrieves object from the cache.
   * 
   * @param uriString URI of the object to search for.
   * @return The cached object or <em>null</em> if not found.
   */
  public Object lookup(String uriString) {
    return this.uriCache.get(uriString);
  }

  /**
   * Removes an Object from the cache.
   * 
   * @param uriString Identity of the object to be removed.
   * @throws UriCacheException in case of error.
   */
  public void remove(String uriString) throws UriCacheException {
    try {
      this.uriCache.remove(uriString);
    }
    catch (CacheException e) {
      throw new UriCacheException(e.getMessage());
    }
  }

  /**
   * Sets the cache auto-expire time. Cache will auto expire elements after specified seconds to
   * reclaim space.
   * 
   * @param seconds The new ShrinkerIntervalSeconds value.
   */
  public void setMaxMemoryIdleTimeSeconds(int seconds) {
    CompositeCacheAttributes attr = (CompositeCacheAttributes) this.uriCache.getCacheAttributes();
    attr.setUseMemoryShrinker(true);
    attr.setMaxMemoryIdleTimeSeconds(seconds);
    attr.setShrinkerIntervalSeconds(seconds);
    this.uriCache.setCacheAttributes(attr);
  }

  /**
   * Constructs configuration properties for the cache instance.
   * 
   * @return cache properties.
   */
  private Properties setupProperties() {
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
    prop.setProperty("jcs.region.UriCache.cacheattributes.MemoryCacheName",
        "org.apache.jcs.engine.memory.lru.LRUMemoryCache");

    prop.setProperty("jcs.region.UriCache.cacheattributes",
        "org.apache.jcs.engine.CompositeCacheAttributes");
    prop.setProperty("jcs.region.UriCache.cacheattributes.MaxObjects", this.uriCacheMaxObjects
        .toString());
    prop.setProperty("jcs.region.UriCache.cacheattributes.ShrinkerIntervalSeconds", "60");
    prop.setProperty("jcs.region.UriCache.cacheattributes.UseMemoryShrinker", "true");
    prop.setProperty("jcs.region.UriCache.cacheattributes.UseDisk", "false");
    prop.setProperty("jcs.region.UriCache.cacheattributes.UseRemote", "false");
    prop.setProperty("jcs.region.UriCache.cacheattributes.UseLateral", "false"); 

    prop.setProperty("jcs.region.UriCache.elementattributes",
        "org.apache.jcs.engine.ElementAttributes");
    prop.setProperty("jcs.region.UriCache.elementattributes.IsEternal", "false");
    prop.setProperty("jcs.region.UriCache.elementattributes.IsLateral", "false");
    prop.setProperty("jcs.region.UriCache.elementattributes.MaxLifeSeconds", this.uriCacheIdleTime
        .toString());
    prop.setProperty("jcs.region.UriCache.elementattributes.IdleTime", this.uriCacheIdleTime
        .toString());
    return prop;
  }

}
