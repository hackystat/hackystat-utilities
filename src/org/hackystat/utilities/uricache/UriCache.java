package org.hackystat.utilities.uricache;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.jcs.JCS;
import org.apache.jcs.access.exception.CacheException;
import org.apache.jcs.engine.CompositeCacheAttributes;
import org.apache.jcs.engine.ElementAttributes;

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

  /** The cache default region name. */
  public static final String defaultRegionName = "uriCache";

  /** The cache itself */
  private JCS jcsCache;

  /**
   * Constructor.
   * 
   * @throws UriCacheException when unable instantiate a cache.
   */
  public UriCache() throws UriCacheException {
    try {
      this.jcsCache = JCS.getInstance(defaultRegionName);
    }
    catch (Exception e) {
      throw new UriCacheException("Can't instantiate JCS ObjectCacheImplementation", e);
    }
  }

  /**
   * Reports the JCS region name.
   * 
   * @return cache region name.
   */
  public String getRegionName() {
    return defaultRegionName;
  }

  /**
   * Used to cache objects by it's URL.
   * 
   * @param urlString Identity of the object to cache.
   * @param obj The object to cache.
   * @throws UriCacheException in case of error.
   */
  public void cache(String urlString, Object obj) throws UriCacheException {
    try {
      this.jcsCache.put(urlString, obj);
    }
    catch (CacheException e) {
      throw new UriCacheException(e);
    }
  }

  /**
   * Puts object into cache and fix the objecy lifetime.
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
      this.jcsCache.put(urlString, obj, attr);
    }
    catch (CacheException e) {
      throw new UriCacheException(e);
    }
  }

  /**
   * Clear the cache.
   * 
   * @throws UriCacheException in case of error.
   */
  public void clear() throws UriCacheException {
    if (this.jcsCache != null) {
      try {
        this.jcsCache.clear();
      }
      catch (CacheException e) {
        throw new UriCacheException(e);
      }
    }
  }

  /**
   * Lookup object with URL 'url' in cache.
   * 
   * @param url URL of the object to search for.
   * @return The cached object or <em>null</em> if no matching object for specified URL is found.
   */
  public Object lookup(String url) {
    return this.jcsCache.get(url);
  }

  /**
   * Removes an Object from the cache.
   * 
   * @param url Identity of the object to be removed.
   * @throws UriCacheException in case of error.
   */
  public void remove(String url) throws UriCacheException {
    try {
      this.jcsCache.remove(url);
    }
    catch (CacheException e) {
      throw new UriCacheException(e.getMessage());
    }
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
    return this.jcsCache.freeMemoryElements(numberToFree);
  }

  /**
   * Cache should auto-expire elements after seconds to reclaim space.
   * 
   * @param seconds The new ShrinkerIntervalSeconds value.
   */
  public void setMaxMemoryIdleTimeSeconds(int seconds) {
    CompositeCacheAttributes attr = new CompositeCacheAttributes();
    attr.setUseMemoryShrinker(true);
    attr.setMaxMemoryIdleTimeSeconds(seconds);
    attr.setShrinkerIntervalSeconds(seconds);
    this.jcsCache.setCacheAttributes(attr);
  }

}
