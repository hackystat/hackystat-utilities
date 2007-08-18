package org.hackystat.utilities.uricache;

import org.apache.jcs.JCS;
import org.apache.jcs.access.exception.CacheException;

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
  public static final String defaultRegionName = "hackyCache";

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

}
