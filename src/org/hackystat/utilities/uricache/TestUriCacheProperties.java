package org.hackystat.utilities.uricache;

import static org.junit.Assert.assertEquals;

import java.util.Properties;
import java.util.logging.Level;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests UriCacheProperties object.
 * 
 * @author Pavel Senin.
 * 
 */
public class TestUriCacheProperties {

  /** The UriCache properties object under the test. */
  private UriCacheProperties prop;

  /**
   * Test set up fixture.
   * 
   * @throws Exception if error encountered.
   */
  @Before
  public void setUp() throws Exception {
    prop = new UriCacheProperties();
  }

  /**
   * Test cleanup procedure.
   * 
   * @throws Exception if error encountered.
   */
  @After
  public void tearDown() throws Exception {
    prop = null;
  }

  /**
   * Tests setters and getters functionality.
   */
  @Test
  public void testSetters() {
    //
    // exercise setters
    //
    prop.setCacheStoragePath("newPath");
    prop.setCacheRegionName("newRegion");
    prop.setLoggerLevel(Level.INFO);
    prop.setMaxIdleTime(999L);
    prop.setMaxMemoryCacpacity(999L);
    //
    // check set values
    //
    assertEquals("Should report default \"newRegion\" name", 
        "newRegion", prop.getCacheRegionName());
    assertEquals("Should report default new storage location", "newPath", prop
        .getCacheStoragePath());
    assertEquals("Should report Level.INFO logger level", Level.INFO, prop.getLoggerLevel());
    Properties p = prop.getProperties();
    assertEquals("Should report default cache config property", "86400", p
        .getProperty("jcs.region.UriCache.elementattributes.MaxLifeSeconds"));
    assertEquals("Should report default cache config property", 86400L, Long.valueOf(p
        .getProperty("jcs.region.UriCache.elementattributes.IdleTime")));

  }

  /**
   * Tests default properties setup.
   */
  @Test
  public void testDefaultProperties() {
    // test the accessible properties
    assertEquals("Should report default \"UriCache\" name", "UriCache", prop.getCacheRegionName());
    assertEquals("Should report default Level.OFF logger level", Level.OFF, prop.getLoggerLevel());
    assertEquals("Should report default storage location", System.getProperties().getProperty(
        "java.io.tmpdir"), prop.getCacheStoragePath());
    // get the default instance
    Properties p = prop.getProperties();
    assertEquals("Should report cache max size", 50000L, Long.valueOf(p
        .getProperty("jcs.default.cacheattributes.MaxObjects")));
    assertEquals("Should report cache max size", 50000L, Long.valueOf(p
        .getProperty("jcs.region.UriCache.cacheattributes.MaxObjects")));

    // common properties test
    //
    // JCS required part
    assertEquals("Should report default cache config property", "indexedDiskCache", p
        .getProperty("jcs.default"));
    assertEquals("Should report default cache config property",
        "org.apache.jcs.engine.CompositeCacheAttributes", p
            .getProperty("jcs.default.cacheattributes"));
    assertEquals("Should report default cache config property",
        "org.apache.jcs.engine.memory.lru.LRUMemoryCache", p
            .getProperty("jcs.default.cacheattributes.MemoryCacheName"));
    //
    // ------- UriCache region cache attributes ---------
    //
    assertEquals("Should report default cache config property", "indexedDiskCache", p
        .getProperty("jcs.region.UriCache"));
    assertEquals("Should report default cache config property",
        "org.apache.jcs.engine.CompositeCacheAttributes", p
            .getProperty("jcs.region.UriCache.cacheattributes"));
    assertEquals("Should report default cache config property",
        "org.apache.jcs.engine.memory.lru.LRUMemoryCache", p
            .getProperty("jcs.region.UriCache.cacheattributes.MemoryCacheName"));
    assertEquals("Should report default cache config property", "300", p
        .getProperty("jcs.region.UriCache.cacheattributes.ShrinkerIntervalSeconds"));
    assertEquals("Should report default cache config property", "true", p
        .getProperty("jcs.region.UriCache.cacheattributes.UseMemoryShrinker"));
    assertEquals("Should report default cache config property", "true", p
        .getProperty("jcs.region.UriCache.cacheattributes.UseDisk"));

    assertEquals("Should report default cache config property", "false", p
        .getProperty("jcs.region.UriCache.cacheattributes.UseRemote"));
    assertEquals("Should report default cache config property", "false", p
        .getProperty("jcs.region.UriCache.cacheattributes.UseLateral"));
    assertEquals("Should report default cache config property", 500L, Long.valueOf(p
        .getProperty("jcs.region.UriCache.cacheattributes.MaxSpoolPerRun")));
    //
    // -------- elements attributes --------
    //
    assertEquals("Should report default cache config property",
        "org.apache.jcs.engine.ElementAttributes", p
            .getProperty("jcs.region.UriCache.elementattributes"));
    assertEquals("Should report default cache config property", "false", p
        .getProperty("jcs.region.UriCache.elementattributes.IsEternal"));
    assertEquals("Should report default cache config property", "false", p
        .getProperty("jcs.region.UriCache.elementattributes.IsLateral"));
    assertEquals("Should report default cache config property", "86400", p
        .getProperty("jcs.region.UriCache.elementattributes.MaxLifeSeconds"));
    assertEquals("Should report default cache config property", 86400L, Long.valueOf(p
        .getProperty("jcs.region.UriCache.elementattributes.IdleTime")));
    assertEquals("Should report default cache config property", "true", p
        .getProperty("jcs.region.UriCache.elementattributes.isSpool"));
    //
    // -------- disk cache elements attributes --------
    //
    assertEquals("Should report default cache config property",
        "org.apache.jcs.auxiliary.disk.indexed.IndexedDiskCacheFactory", p
            .getProperty("jcs.auxiliary.indexedDiskCache"));
    assertEquals("Should report default cache config property",
        "org.apache.jcs.auxiliary.disk.indexed.IndexedDiskCacheAttributes", p
            .getProperty("jcs.auxiliary.indexedDiskCache.attributes"));
    assertEquals("Should report default cache config property", "300000", p
        .getProperty("jcs.auxiliary.indexedDiskCache.attributes.MaxPurgatorySize"));
    assertEquals("Should report default cache config property", "500000", p
        .getProperty("jcs.auxiliary.indexedDiskCache.attributes.MaxKeySize"));
    assertEquals("Should report default cache config property", "50000", p
        .getProperty("jcs.auxiliary.indexedDiskCache.attributes.MaxRecycleBinSize"));
    assertEquals("Should report default cache config property", "300000", p
        .getProperty("jcs.auxiliary.indexedDiskCache.attributes.OptimizeAtRemoveCount"));
    assertEquals("Should report default cache config property", "SINGLE", p
        .getProperty("jcs.auxiliary.indexedDiskCache.attributes.EventQueueType"));
    assertEquals("Should report default cache config property", "disk_cache_event_queue", p
        .getProperty("jcs.auxiliary.indexedDiskCache.attributes.EventQueuePoolName"));
    //
    // --------- disk cache tricks
    //

    assertEquals("Should report default cache config property", "2000", p
        .getProperty("thread_pool.default.boundarySize"));
    assertEquals("Should report default cache config property", "150", p
        .getProperty("thread_pool.default.maximumPoolSize"));
    assertEquals("Should report default cache config property", "4", p
        .getProperty("thread_pool.default.minimumPoolSize"));
    assertEquals("Should report default cache config property", "350000", p
        .getProperty("thread_pool.default.keepAliveTime"));

    assertEquals("Should report default cache config property", "RUN", p
        .getProperty("thread_pool.default.whenBlockedPolicy"));
    assertEquals("Should report default cache config property", "4", p
        .getProperty("thread_pool.default.startUpSize"));
    assertEquals("Should report default cache config property", "false", p
        .getProperty("thread_pool.disk_cache_event_queue.useBoundary"));
    assertEquals("Should report default cache config property", "2", p
        .getProperty("thread_pool.disk_cache_event_queue.minimumPoolSize"));
    assertEquals("Should report default cache config property", "3500", p
        .getProperty("thread_pool.disk_cache_event_queue.keepAliveTime"));
    assertEquals("Should report default cache config property", "10", p
        .getProperty("thread_pool.disk_cache_event_queue.startUpSize"));
    // assertEquals("Should report default cache config property", "indexedDiskCache", p
    // .getProperty("jcs.region.UriCache"));

  }

}
