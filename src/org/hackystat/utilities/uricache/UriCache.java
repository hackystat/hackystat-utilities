package org.hackystat.utilities.uricache;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.jcs.JCS;
import org.apache.jcs.access.exception.CacheException;
import org.apache.jcs.engine.CompositeCacheAttributes;
import org.apache.jcs.engine.ElementAttributes;
import org.apache.jcs.engine.control.CompositeCache;
import org.apache.jcs.engine.control.CompositeCacheManager;

/**
 * Provides an easy caching mechanism which is backed by Apache JCS (Java Caching System). Once
 * cache configured and initialized, it caches pairs &lt;K, V&gt;.
 * 
 * <br/><br/> Using an UriCache has several advantages: it increases performance as it reduces URL
 * lookups.
 * 
 * @author <a href="mailto:seninp@gmail.com">Pavel Senin<a>
 * 
 * @param <K> type of the cache keys.
 * @param <V> type of the cache items.
 */
public class UriCache<K, V> {

  /** JCS cache handler */
  private JCS uriCache;

  /**
   * Constructor, returns a default instance of UriCache.
   * 
   * @param cacheProperties the cache configuration properties.
   * @throws UriCacheException when unable instantiate a cache.
   */
  public UriCache(UriCacheProperties cacheProperties) throws UriCacheException {
    // getting rid of DEBUG level log messages
    Logger logger = Logger.getLogger("org.apache.jcs");
    logger.setLevel(Level.OFF);
    // setup JCS
    CompositeCacheManager mgr = CompositeCacheManager.getUnconfiguredInstance();
    mgr.configure(cacheProperties.getProperties());
    // get access to bug test cache region
    try {
      this.uriCache = JCS.getInstance(cacheProperties.getCacheRegionName());
      @SuppressWarnings("unused")
      CompositeCache cache = mgr.getCache(cacheProperties.getCacheRegionName());
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
  public void cache(K uriString, V obj) throws UriCacheException {
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
  public void cache(K uriString, V obj, XMLGregorianCalendar expirationTimeStamp)
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
   * ShutDowns the cache, flushes all items from the memory to cache and clears memory region.
   * 
   */
  public void shutdown() {
    if (this.uriCache != null) {
      this.uriCache.dispose();
    }
  }

  /**
   * Retrieves object from the cache.
   * 
   * @param uriString URI of the object to search for.
   * @return The cached object or <em>null</em> if not found.
   */
  public Object lookup(K uriString) {
    return this.uriCache.get(uriString);
  }

  /**
   * Removes an Object from the cache.
   * 
   * @param uriString Identity of the object to be removed.
   * @throws UriCacheException in case of error.
   */
  public void remove(K uriString) throws UriCacheException {
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

}
