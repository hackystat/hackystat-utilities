package org.hackystat.utilities.uricache;

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
public interface HackyObjectCache {
  /**
   * Used to cache objects by it's URL.
   * 
   * @param urlString Identity of the object to cache.
   * @param obj The object to cache.
   * @throws RuntimeCacheException in case of error.
   */
  public void cache(String urlString, Object obj) throws RuntimeCacheException;

  /**
   * Lookup object with URL 'url' in cache.
   * 
   * @param url URL of the object to search for.
   * @return The cached object or <em>null</em> if no matching object for specified URL is found.
   */
  public Object lookup(String url);

  /**
   * Removes an Object from the cache.
   * 
   * @param url Identity of the object to be removed.
   * @throws RuntimeCacheException in case of error.
   */
  public void remove(String url) throws RuntimeCacheException;

  /**
   * Clear the cache.
   * 
   * @throws RuntimeCacheException in case of error.
   */
  public void clear() throws RuntimeCacheException;
}
