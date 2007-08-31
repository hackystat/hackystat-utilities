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
 * The HackyObjectCache stores all Objects loaded from HackyStat services. When the Service tries to
 * get an Object by its URL it first lookups the cache if the object has been already loaded and
 * cached.
 * 
 * <br/><br/> Using an ObjectCache has several advantages: - it increases performance as it reduces
 * URL lookups.
 * 
 * <br/><br/>This interface allows to have userdefined Cache implementations. The
 * ObjectCacheFactory is responsible for generating cache instances.
 * 
 * @author <a href="mailto:seninp@gmail.com">Pavel Senin<a>
 */
public class UriCache {

  /** The cache itself */
  private JCS uriCache;

  /** The cache default region name. */
  public static final String uriCaheRegionName = "UriCache";

  /** The cache capacity. */
  public Integer uriCacheMaxObjects = 500;

  /** The cache elements idle time. */
  public Integer uriCacheIdleTime = 1200;

  /** The UriCache configuration properties. */
  public Properties uriCacheProperties = null;

  /**
   * Constructs properties for the cache instantiation.
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
    prop.setProperty("jcs.region.UriCache.cacheattributes.ShrinkerIntervalSeconds", "600");
    prop.setProperty("jcs.region.UriCache.cacheattributes.UseMemoryShrinker", "true");
    prop.setProperty("jcs.region.UriCache.cacheattributes.UseDisk", "false");
    prop.setProperty("jcs.region.UriCache.cacheattributes.UseRemote", "false");

    prop.setProperty("jcs.region.UriCache.elementattributes",
        "org.apache.jcs.engine.ElementAttributes");
    prop.setProperty("jcs.region.UriCache.elementattributes.IsEternal", "false");
    prop.setProperty("jcs.region.UriCache.elementattributes.MaxLifeSeconds", "5");
    prop.setProperty("jcs.region.UriCache.elementattributes.IdleTime", this.uriCacheIdleTime
        .toString());
    return prop;
  }

  /**
   * Constructor, returns instance of UriCache which is configured according to the properties.
   * 
   * @param maxCacheElements specifies cache capacity.
   * @param cacheMemoryIdleTime specifies memory idle time for the cache elements.
   * @throws UriCacheException when unable instantiate a cache.
   */
  public UriCache(Integer maxCacheElements, Integer cacheMemoryIdleTime) throws UriCacheException {

    this.uriCacheMaxObjects = maxCacheElements;
    this.uriCacheIdleTime = cacheMemoryIdleTime;

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
   * Clear the cache.
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
   * Caches the object using URI string as the key for later retrieval.
   * 
   * @param urlString identity (URI) of the object to cache.
   * @param obj The object to cache.
   * @throws UriCacheException in case of error.
   */
  public void cache(String urlString, Object obj) throws UriCacheException {
    try {
      this.uriCache.put(urlString, obj);
    }
    catch (CacheException e) {
      throw new UriCacheException(e);
    }
  }

  /**
   * Puts object into cache and specifies object lifetime within the cache.
   * 
   * @param urlString Identity of the object to cache.
   * @param obj The object to cache.
   * @param expirationTime object caching expiration time.
   * @throws UriCacheException in case of error.
   */
  public void cache(String urlString, Object obj, XMLGregorianCalendar expirationTime)
      throws UriCacheException {

    // first of all calculating the life time of this object from now
    Long currTime = System.currentTimeMillis();
    Long lifeTime = ((Integer) expirationTime.getMillisecond()).longValue() - currTime;

    ElementAttributes attr = new ElementAttributes();
    attr.setMaxLifeSeconds(lifeTime);

    try {
      this.uriCache.put(urlString, obj, attr);
    }
    catch (CacheException e) {
      throw new UriCacheException(e);
    }
  }

  /**
   * Lookup object with URI 'uri' in cache.
   * 
   * @param urlString URL of the object to search for.
   * @return The cached object or <em>null</em> if no matching object for specified URL is found.
   */
  public Object lookup(String urlString) {
    return this.uriCache.get(urlString);
  }

  /**
   * This instructs the memory cache to remove the numberToFree according to its eviction policy.
   * 
   * @param numberToFree number of elements to free from memory during this sweep.
   * @return the number that were removed. if you ask to free 5, but there are only 3, you will get
   *         3.
   * @throws CacheException if an error encountered.
   */
  public Integer freeMemoryElements(int numberToFree) throws CacheException {
    return this.uriCache.freeMemoryElements(numberToFree);
  }

  /**
   * Removes an Object from the cache.
   * 
   * @param urlString Identity of the object to be removed.
   * @throws UriCacheException in case of error.
   */
  public void remove(String urlString) throws UriCacheException {
    try {
      this.uriCache.remove(urlString);
    }
    catch (CacheException e) {
      throw new UriCacheException(e.getMessage());
    }
  }

  /**
   * Cache should auto-expire elements after specified seconds to reclaim space.
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

}
