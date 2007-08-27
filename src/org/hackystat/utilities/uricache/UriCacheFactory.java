package org.hackystat.utilities.uricache;

/**
 * The factory for configuring and getting UriCache instances.
 * 
 * @author <a href="mailto:seninp@gmail.com">Pavel Senin<a>
 */
public class UriCacheFactory {

  /** The cache default region name. */
  public static final String defaultRegionName = "uriCache";

  // private static CompositeCacheAttributes memoryCacheAttributes;

  // private static IndexedDiskCacheAttributes diskCacheAttributes;

  /**
   * Constructor, returns instance of UriCache which is configured according to configuration file
   * cache.ccf. <b>Note that this file must be in CLASSPATH environment.</b>
   * 
   * @return an URICache instance.
   * @throws UriCacheException when unable instantiate a cache.
   */
  public static UriCache getURICacheInstance() throws UriCacheException {
    try {
      return new UriCache();
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

}
