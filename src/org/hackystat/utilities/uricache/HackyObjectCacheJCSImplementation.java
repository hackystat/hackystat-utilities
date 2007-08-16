package org.hackystat.utilities.uricache;

import org.apache.jcs.JCS;
import org.apache.jcs.access.exception.CacheException;

/**
 * This local {@link HackyObjectCache} implementation using <a
 * href="http://jakarta.apache.org/jcs/">apache-JCS</a> to cache HackyStat objects.
 * 
 * @author <a href="mailto:seninp@gmail.com">Pavel Senin<a>
 */
public class HackyObjectCacheJCSImplementation implements HackyObjectCache {

  /** The cache default region name. */
  public static final String defaultRegionName = "hackyCache";

  /** The cache itself */
  private JCS jcsCache;

  /**
   * Constructor.
   * 
   * @throws RuntimeCacheException when unable instantiate a cache.
   */
  public HackyObjectCacheJCSImplementation() throws RuntimeCacheException {
    try {
      this.jcsCache = JCS.getInstance(defaultRegionName);
    }
    catch (Exception e) {
      throw new RuntimeCacheException("Can't instantiate JCS ObjectCacheImplementation", e);
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
   * {@inheritDoc}
   */
  @Override
  public void cache(String urlString, Object obj) throws RuntimeCacheException {
    try {
      this.jcsCache.put(urlString, obj);
    }
    catch (CacheException e) {
      throw new RuntimeCacheException(e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void clear() throws RuntimeCacheException {
    if (this.jcsCache != null) {
      try {
        this.jcsCache.clear();
      }
      catch (CacheException e) {
        throw new RuntimeCacheException(e);
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object lookup(String url) {
    return this.jcsCache.get(url);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void remove(String url) throws RuntimeCacheException {
    try {
      this.jcsCache.remove(url);
    }
    catch (CacheException e) {
      throw new RuntimeCacheException(e.getMessage());
    }
  }

}
