package org.hackystat.utilities.uricache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.hackystat.utilities.logger.OneLineFormatter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the UriCache class.
 * 
 * @author <a href="mailto:seninp@gmail.com">Pavel Senin<a>
 * 
 */
public class TestUriCache {

  /** The cache itself */
  private UriCache<String, String> testCache;

  /** The default properties to use. */
  private UriCacheProperties prop;

  /** The formatter to use for formatting exceptions */
  private static OneLineFormatter formatter = new OneLineFormatter();

  /**
   * Instantiates default cache properties and creates objects to be cached.
   * 
   * @throws Exception if error encountered.
   */
  @Before
  public void setUp() throws Exception {
    prop = new UriCacheProperties();
    prop.setCacheStoragePath("sandbox/cache");
  }

  /**
   * Clears the cache and nulls it after.
   * 
   * @throws UriCacheException if error encountered.
   */
  @After
  public void tearDown() throws UriCacheException {
    testCache.shutdown();
  }

  // /**
  // * Cache exception test.
  // */
  // @Test
  // public void testCacheException() {
  // try {
  // //
  // // get cache instance and dump some data.
  // //
  // testCache = new UriCache<String, String>("testCache", prop);
  // testCache.clear();
  // int cnt = 10000;
  // for (int i = 0; i < cnt; i++) {
  // testCache.cache("key:" + i, "data:" + i);
  // }
  // //
  // // now try to another cache with the same properties.
  // // Should not be able to do this.
  // //
  // @SuppressWarnings("unused")
  // UriCache<String, String> testCache2 = new UriCache<String, String>("testCache", prop);
  // fail("Able to get the cache instance with the same name.");
  // }
  // catch (UriCacheException e) {
  // fail("Unable to create cache instance: "
  // + formatter.format(new LogRecord(Level.ALL, e.toString())));
  // }
  // }

  /**
   * Cache persistence test.
   */
  @Test
  public void testCachePersistence() {
    try {
      //
      // get cache instance, clear any leftovers and load cache with new data
      //
      testCache = new UriCache<String, String>("testOptimizerCache", prop);
      testCache.clear();
      int cnt = 10000;
      for (int i = 0; i < cnt; i++) {
        testCache.cache("key:" + i, "data:" + i);
      }
      //
      // now, shut it down
      //
      testCache.shutdown();
      //
      // chill a little
      //
      // System.err.println("UriCache test: testing persistence. Sleeping for 10 seconds.");
      Thread.yield();
      Thread.sleep(1000);
      Thread.yield();
      //
      // get cache back alive
      //
      testCache = new UriCache<String, String>("testOptimizerCache", prop);
      //
      // should be ABLE to read data back
      //
      for (int i = 0; i < cnt; i++) {
        String element = (String) testCache.lookup("key:" + i);
        assertNotNull("Should have recevied an element. " + i, element);
        assertEquals("Element is wrong.", "data:" + i, element);
      }
    }
    catch (UriCacheException e) {
      fail("Unable to create cache instance: "
          + formatter.format(new LogRecord(Level.ALL, e.toString())));
    }
    catch (InterruptedException e) {
      fail("Unable to sleep >:-!```: " + formatter.format(new LogRecord(Level.ALL, e.toString())));
    }

  }

  /**
   * Cache shrinker test #1, test shrinking of particular elements.
   */
  @Test
  public void testCacheOptimizer1() {
    try {
      //
      // a little set up.
      //
      DatatypeFactory factory = null;
      factory = DatatypeFactory.newInstance();

      //
      // get cache instance, clear any leftovers and load cache with new data
      //
      testCache = new UriCache<String, String>("testOptimizerCache", prop);
      testCache.clear();
      int cnt = 5000;
      for (int i = 0; i < cnt; i++) {
        testCache.cache("key:" + i, "data:" + i);
      }
      // put "hot" items in cache now
      GregorianCalendar calendar = new GregorianCalendar();
      calendar.setTimeInMillis(System.currentTimeMillis() + 500);
      for (int i = cnt; i < cnt * 2; i++) {
        testCache.cache("key:" + i, "data:" + i, factory.newXMLGregorianCalendar(calendar));
      }
      //
      // chill a little, need time to close cache file.
      //
      // System.out.println("UriCache test: testing shrinker #1. Sleeping for 10 seconds.");
      Thread.yield();
      Thread.sleep(1000);
      Thread.yield();
      //
      // should be unable to read first data block back
      //
      for (int i = 0; i < cnt; i++) {
        String element = (String) testCache.lookup("key:" + i);
        assertNotNull("Should have recevied an element. " + i, element);
        assertEquals("Element is wrong.", "data:" + i, element);
      }
      //
      // and should be unable to read second data block back
      //
      for (int i = cnt; i < cnt * 2; i++) {
        testCache.remove("key:" + i);
        assertNull("Should have NOT recevied an element. " + i, testCache.lookup("key:" + i));
      }
    }
    catch (UriCacheException e) {
      fail("Unable to create cache instance: "
          + formatter.format(new LogRecord(Level.ALL, e.toString())));
    }
    catch (InterruptedException e) {
      fail("Unable to sleep >:-!```: " + formatter.format(new LogRecord(Level.ALL, e.toString())));
    }
    catch (DatatypeConfigurationException e) {
      fail("Unable to get DataFactory instance: "
          + formatter.format(new LogRecord(Level.ALL, e.toString())));
    }

  }

  /**
   * Cache shrinker test.
   */
  @Test
  public void testCacheOptimizer2() {
    try {
      //
      // get cache instance, clear any leftovers and load cache with new data
      //
      prop.setMaxIdleTime(5L);
      testCache = new UriCache<String, String>("testOptimizerCache", prop);
      testCache.clear();
      int cnt = 10000;
      for (int i = 0; i < cnt; i++) {
        testCache.cache("key:" + i, "data:" + i);
      }
      //
      // chill a little, need time to close cache file.
      //
      // System.out.println("UriCache test: testing shrinker #2. Sleeping for 10 seconds.");
      Thread.yield();
      Thread.sleep(1000);
      Thread.yield();
      //
      // should be unable to read data back
      //
      for (int i = 0; i < cnt; i++) {
        testCache.remove("key:" + i);
        assertNull("Should have NOT recevied an element. " + i, testCache.lookup("key:" + i));
      }
    }
    catch (UriCacheException e) {
      fail("Unable to create cache instance: "
          + formatter.format(new LogRecord(Level.ALL, e.toString())));
    }
    catch (InterruptedException e) {
      fail("Unable to sleep >:-!```: " + formatter.format(new LogRecord(Level.ALL, e.toString())));
    }

  }

  /**
   * Cache instantiation and simple routines.
   */
  @Test
  public void testCacheInstance() {
    try {
      //
      // get cache instance and clear any leftovers
      //
      testCache = new UriCache<String, String>("testCache", prop);
      testCache.clear();
      //
      // load cache with new data
      //
      int cnt = 10000;
      for (int i = 0; i < cnt; i++) {
        testCache.cache("key:" + i, "data:" + i);
      }
      //
      // read data back
      //
      for (int i = 0; i < cnt; i++) {
        String element = (String) testCache.lookup("key:" + i);
        assertNotNull("Should have recevied an element. " + i, element);
        assertEquals("Element is wrong.", "data:" + i, element);
      }
      //
      // clean cache one by one and check removal
      //
      for (int i = 0; i < cnt; i++) {
        testCache.remove("key:" + i);
        assertNull("Should have NOT recevied an element. " + i, testCache.lookup("key:" + i));
      }
      //
      // load cache again
      //
      for (int i = 0; i < cnt; i++) {
        testCache.cache("key:" + i, "data:" + i);
      }
      //
      // clean it
      //
      testCache.clear();
      //
      // check if it clean
      //
      for (int i = 0; i < cnt; i++) {
        assertNull("Should have NOT recevied an element. " + i, testCache.lookup("key:" + i));
      }
    }
    catch (UriCacheException e) {
      fail("Unable to create cache instance: "
          + formatter.format(new LogRecord(Level.ALL, e.toString())));
    }

  }

  // /**
  // * Cache load test.
  // */
  // @Test
  // public void testCacheLoad() {
  // try {
  // // create a cache instance
  // testCache = new UriCache<String, String>("testCache", prop);
  //
  // // put items in cache
  // int cnt = TestUriCache.cacheLoadLimit;
  // for (int i = 0; i < cnt; i++) {
  // testCache.cache("key:" + i, "data:" + i);
  // // System.out.println("cached: " + i);
  // }
  //
  // // get items from cache
  // for (int i = 0; i < cnt; i++) {
  // String element = (String) testCache.lookup("key:" + i);
  // assertNotNull("presave, Should have recevied an element. " + i, element);
  // assertEquals("presave, element is wrong.", "data:" + i, element);
  // // System.out.println("got: " + i);
  // }
  //
  // // Remove all the items
  // for (int i = 0; i <= cnt; i++) {
  // testCache.remove("key:" + i);
  // }
  //
  // // Verify removal
  // for (int i = 0; i <= cnt; i++) {
  // assertNull("Removed key should be null: " + i + ":key", testCache.lookup("key:" + i));
  // }
  //
  // }
  // catch (UriCacheException e) {
  // fail("Unable to proceed with load test: "
  // + formatter.format(new LogRecord(Level.ALL, e.toString())));
  // }
  // }
  //
  // /**
  // * Cache shutdown, hot startup test.
  // * @throws InterruptedException
  // */
  // @Test
  // public void testCacheHotStart() throws InterruptedException {
  // try {
  // testCache = new UriCache<String, String>("testCache", prop);
  //      
  // int cnt = TestUriCache.cacheLoadLimit;
  //
  // for (int i = 0; i < cnt; i++) {
  // testCache.cache("key:" + i, "data:" + i);
  // // System.out.println("cached: " + i);
  // }
  //
  // // for (int i = 0; i < cnt; i++) {
  // // String element = (String) testCache.lookup("key:" + i);
  // // assertNotNull("presave, Should have recevied an element. " + i, element);
  // // assertEquals("presave, element is wrong.", "data:" + i, element);
  // // // System.out.println("got: " + i);
  // // }
  //
  // testCache.shutdown();
  //
  // Thread.yield();
  // Thread.sleep(6000);
  // Thread.yield();
  //
  // testCache = new UriCache<String, String>("testCache", prop);
  //
  // for (int i = 0; i < cnt; i++) {
  // String element = (String) testCache.lookup("key:" + i);
  // assertNotNull("presave, Should have recevied an element. " + i, element);
  // assertEquals("presave, element is wrong.", "data:" + i, element);
  // // System.out.println("got: " + i);
  // }
  //
  // }
  // catch (UriCacheException e) {
  // fail("Unable to proceed with load test: "
  // + formatter.format(new LogRecord(Level.ALL, e.toString())));
  // }
  // // catch (InterruptedException e) {
  // // // TODO Auto-generated catch block
  // // e.printStackTrace();
  // // }
  // }
  //
  // /**
  // * Creates and loads three different caches simultaneously.
  // */
  // @Test
  // public void testCaches() {
  //
  // // create three properties first
  // UriCacheProperties stringCacheProp = new UriCacheProperties();
  // stringCacheProp.setCacheStoragePath("sandbox/cache");
  // // stringCacheProp.setCacheRegionName("StringsCache");
  //
  // UriCacheProperties intCacheProp = new UriCacheProperties();
  // intCacheProp.setCacheStoragePath("sandbox/cache");
  // // intCacheProp.setCacheRegionName("IntegerCache");
  //
  // UriCacheProperties doubleCacheProp = new UriCacheProperties();
  // doubleCacheProp.setCacheStoragePath("sandbox/cache");
  // // doubleCacheProp.setCacheRegionName("DoubleCache");
  //
  // try {
  // // create cache instances
  // UriCache<String, String> stringCache = new UriCache<String, String>("StringsCache",
  // stringCacheProp);
  // UriCache<String, Integer> integerCache = new UriCache<String, Integer>("IntegerCache",
  // intCacheProp);
  // UriCache<String, Double> doubleCache = new UriCache<String, Double>("DoubleCache",
  // doubleCacheProp);
  //
  // // put items in cache
  // int cnt = TestUriCache.cacheLoadLimit;
  // for (int i = 0; i < cnt; i++) {
  // stringCache.cache("key:" + i, "data:" + i);
  // integerCache.cache("key:" + i, i);
  // doubleCache.cache("key:" + i, ((Integer) i).doubleValue());
  // }
  //
  // // get items from cache
  // for (int i = 0; i < cnt; i++) {
  // String element = (String) stringCache.lookup("key:" + i);
  // assertNotNull("presave, Should have recevied an element. " + i, element);
  // assertEquals("presave, element is wrong.", "data:" + i, element);
  // // System.out.println("got: " + i);
  //
  // Integer element1 = (Integer) integerCache.lookup("key:" + i);
  // assertNotNull("presave, Should have recevied an element. " + i, element1);
  // assertEquals("presave, element is wrong.", ((Integer) i), element1);
  //
  // Double element2 = (Double) doubleCache.lookup("key:" + i);
  // assertNotNull("presave, Should have recevied an element. " + i, element2);
  // assertEquals("presave, element is wrong.", ((Integer) i).doubleValue(), element2
  // .doubleValue(), 0.02d);
  // }
  //
  // // shutdown caches
  // stringCache.clear();
  // stringCache.shutdown();
  // integerCache.clear();
  // integerCache.shutdown();
  // doubleCache.clear();
  // doubleCache.shutdown();
  // }
  // catch (UriCacheException e) {
  // fail("Unable to proceed with load test: "
  // + formatter.format(new LogRecord(Level.ALL, e.toString())));
  // }
  //
  // }
  //
  // /**
  // * Cache shrinker test.
  // */
  // @Test
  // public void testCacheShrinker() {
  // try {
  // testCache = new UriCache<String, String>("testCache", prop);
  //
  // DatatypeFactory factory = null;
  // factory = DatatypeFactory.newInstance();
  // GregorianCalendar calendar = new GregorianCalendar();
  // calendar.setTimeInMillis(System.currentTimeMillis() + 100);
  //
  // testCache.setMaxMemoryIdleTimeSeconds(5);
  //
  // testCache.cache("string1", testString1, factory.newXMLGregorianCalendar(calendar));
  //
  // System.out.println("Testing caching system: waiting for cache to be autocleaned.");
  //
  // Thread.yield();
  // Thread.sleep(1000 * 15);
  // Thread.yield();
  //
  // // testCache.freeMemoryElements(1);
  //
  // // test that objects is within the cache
  // assertNull("Testing cache with expiration", testCache.lookup("string1"));
  //
  // }
  // catch (UriCacheException e) {
  // fail("Unable to put objects into cache: "
  // + formatter.format(new LogRecord(Level.ALL, e.toString())));
  // }
  // catch (DatatypeConfigurationException e) {
  // fail("Cannot get dataFactory working: "
  // + formatter.format(new LogRecord(Level.ALL, e.toString())));
  // }
  // catch (InterruptedException e) {
  // fail("Cannot really do the sleep() stuff: "
  // + formatter.format(new LogRecord(Level.ALL, e.toString())));
  // }
  // }
  //
}
