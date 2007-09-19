package org.hackystat.utilities.uricache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.LogRecord;

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

  /**
   * Test objects we are going to use: three strings and one map.
   */
  private static final String testString1 = "Test String1 qwerty77";

  private static final String testString2 = "Test String2 asdfgh88";

  private static final String testString3 = "Test String3 zxcvbn99";

  private static TreeMap<Integer, String> testMap = new TreeMap<Integer, String>();

  private UriCacheProperties prop;

  private static final int cacheLoadLimit = 9999;

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
    prop.setCacheRegionName("testCache");
    prop.setCacheStoragePath("sandbox/cache");
    // constructing "complex" object for testing purposes
    testMap.put(1, testString1);
    testMap.put(2, testString2);
    testMap.put(3, testString3);
  }

  /**
   * Clears the cache and nulls it after.
   * 
   * @throws UriCacheException if error encountered.
   */
  @After
  public void tearDown() throws UriCacheException {
    if (null != testCache) {
      testCache.clear();
      testCache.shutdown();
    }
  }

  /**
   * Cache instantiation test.
   */
  @Test
  public void testCacheInstance() {
    try {
      testCache = new UriCache<String, String>(prop);
      testCache.cache("1", testString1);
      testCache.cache("2", testString2);
      testCache.cache("3", testString3);
    }
    catch (UriCacheException e) {
      fail("Unable to create cache instance: "
          + formatter.format(new LogRecord(Level.ALL, e.toString())));
    }
    assertEquals("Testing .getName() method.", "testCache", testCache.getRegionName());
    assertEquals("Testing .lookup() method.", testString1, testCache.lookup("1"));
    assertEquals("Testing .lookup() method.", testString2, testCache.lookup("2"));
    assertEquals("Testing .lookup() method.", testString3, testCache.lookup("3"));
  }

  /**
   * Cache load test.
   */
  @Test
  public void testCacheLoad() {
    try {
      // create a cache instance
      testCache = new UriCache<String, String>(prop);

      // put items in cache
      int cnt = TestUriCache.cacheLoadLimit;
      for (int i = 0; i < cnt; i++) {
        testCache.cache("key:" + i, "data:" + i);
        // System.out.println("cached: " + i);
      }

      // get items from cache
      for (int i = 0; i < cnt; i++) {
        String element = (String) testCache.lookup("key:" + i);
        assertNotNull("presave, Should have recevied an element. " + i, element);
        assertEquals("presave, element is wrong.", "data:" + i, element);
        // System.out.println("got: " + i);
      }

      // Remove all the items
      for (int i = 0; i <= cnt; i++) {
        testCache.remove("key:" + i);
      }

      // Verify removal
      for (int i = 0; i <= cnt; i++) {
        assertNull("Removed key should be null: " + i + ":key", testCache.lookup("key:" + i));
      }

    }
    catch (UriCacheException e) {
      fail("Unable to proceed with load test: "
          + formatter.format(new LogRecord(Level.ALL, e.toString())));
    }
  }

  /**
   * Cache shutdown, hot startup test.
   */
  @Test
  public void testCacheHotStart() {
    try {
      testCache = new UriCache<String, String>(prop);

      int cnt = TestUriCache.cacheLoadLimit;

      for (int i = 0; i < cnt; i++) {
        testCache.cache("key:" + i, "data:" + i);
        // System.out.println("cached: " + i);
      }

      for (int i = 0; i < cnt; i++) {
        String element = (String) testCache.lookup("key:" + i);
        assertNotNull("presave, Should have recevied an element. " + i, element);
        assertEquals("presave, element is wrong.", "data:" + i, element);
        // System.out.println("got: " + i);
      }

      // testCache.shutdown();
      //
      // Thread.yield();
      // Thread.sleep(600);
      // Thread.yield();
      //
      // testCache = new UriCache<String, String>(prop);
      //
      // for (int i = 0; i < cnt; i++) {
      // String element = (String) testCache.lookup("key:" + i);
      // assertNotNull("presave, Should have recevied an element. " + i, element);
      // assertEquals("presave, element is wrong.", "data:" + i, element);
      // // System.out.println("got: " + i);
      // }

    }
    catch (UriCacheException e) {
      fail("Unable to proceed with load test: "
          + formatter.format(new LogRecord(Level.ALL, e.toString())));
    }
    // catch (InterruptedException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
  }

  /**
   * Creates and loads three different caches simultaneously.
   */
  @Test
  public void testCaches() {

    // create three properties first
    UriCacheProperties stringCacheProp = new UriCacheProperties();
    stringCacheProp.setCacheStoragePath("sandbox/cache");
    stringCacheProp.setCacheRegionName("StringsCache");

    UriCacheProperties intCacheProp = new UriCacheProperties();
    intCacheProp.setCacheStoragePath("sandbox/cache");
    intCacheProp.setCacheRegionName("IntegerCache");

    UriCacheProperties doubleCacheProp = new UriCacheProperties();
    doubleCacheProp.setCacheStoragePath("sandbox/cache");
    doubleCacheProp.setCacheRegionName("DoubleCache");

    try {
      // create cache instances
      UriCache<String, String> stringCache = new UriCache<String, String>(stringCacheProp);
      UriCache<String, Integer> integerCache = new UriCache<String, Integer>(intCacheProp);
      UriCache<String, Double> doubleCache = new UriCache<String, Double>(doubleCacheProp);

      // put items in cache
      int cnt = TestUriCache.cacheLoadLimit;
      for (int i = 0; i < cnt; i++) {
        stringCache.cache("key:" + i, "data:" + i);
        integerCache.cache("key:" + i, i);
        doubleCache.cache("key:" + i, ((Integer) i).doubleValue());
      }

      // get items from cache
      for (int i = 0; i < cnt; i++) {
        String element = (String) stringCache.lookup("key:" + i);
        assertNotNull("presave, Should have recevied an element. " + i, element);
        assertEquals("presave, element is wrong.", "data:" + i, element);
        // System.out.println("got: " + i);

        Integer element1 = (Integer) integerCache.lookup("key:" + i);
        assertNotNull("presave, Should have recevied an element. " + i, element1);
        assertEquals("presave, element is wrong.", ((Integer) i), element1);

        Double element2 = (Double) doubleCache.lookup("key:" + i);
        assertNotNull("presave, Should have recevied an element. " + i, element2);
        assertEquals("presave, element is wrong.", ((Integer) i).doubleValue(), element2
            .doubleValue(), 0.02d);
      }

      // shutdown caches
      stringCache.clear();
      stringCache.shutdown();
      integerCache.clear();
      integerCache.shutdown();
      doubleCache.clear();
      doubleCache.shutdown();
    }
    catch (UriCacheException e) {
      fail("Unable to proceed with load test: "
          + formatter.format(new LogRecord(Level.ALL, e.toString())));
    }

  }

  // /**
  // * Cache shrinker test.
  // */
  // @Test
  // public void testCacheShrinker() {
  // try {
  // testCache = new UriCache<String, String>(prop);
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
  // Thread.sleep(5 * 100 * 15);
  //
  // Thread.sleep(5 * 100 * 15);
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

}
