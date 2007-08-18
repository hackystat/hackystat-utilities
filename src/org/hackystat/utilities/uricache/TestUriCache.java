package org.hackystat.utilities.uricache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.hackystat.utilities.logger.OneLineFormatter;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests the HackyObjectCacheJCSImplementation class.
 * 
 * @author <a href="mailto:seninp@gmail.com">Pavel Senin<a>
 * 
 */
public class TestUriCache {

  /** The cache itself */
  private static UriCache testCache;

  /**
   * Test objects we gonna use, three strings and one map.
   */
  private static final String testString1 = "Test String1 qwerty77";

  private static final String testString2 = "Test String2 asdfgh88";

  private static final String testString3 = "Test String3 zxcvbn99";

  private static TreeMap<Integer, String> testMap = new TreeMap<Integer, String>();

  // PMD is killing me....
  private static final String string2Name = "string2";

  /** The formatter to use for formatting exceptions */
  private static OneLineFormatter formatter = new OneLineFormatter();

  /**
   * This one instantiates the cache and puts test strings within test map.
   */
  @BeforeClass
  public static void oneTimeSetUp() {
    testMap.put(1, testString1);
    testMap.put(2, testString2);
    testMap.put(3, testString3);
    try {
      testCache = new UriCache();
    }
    catch (UriCacheException e) {
      fail("Unable to instantiate cache: "
          + formatter.format(new LogRecord(Level.ALL, e.toString())));
    }
  }

  /**
   * Tests proper region name getter.
   */
  @Test
  public void testGetRegionName() {
    assertEquals("Testing region name", "hackyCache", testCache.getRegionName());
  }

  /**
   * Complex cache test, tests getters, remove and clear.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testCache() {

    //
    // caching objects first.
    //
    try {
      testCache.cache("string1", testString1);
      testCache.cache(string2Name, testString2);
      testCache.cache("string3", testString3);
      testCache.cache("testMap", testMap);
    }
    catch (UriCacheException e) {
      fail("Unable to put objects into cache: "
          + formatter.format(new LogRecord(Level.ALL, e.toString())));
    }

    // looking up for the string
    Object object1 = testCache.lookup(string2Name);
    if (object1 instanceof String) {
      assertEquals("Testing .lookup() method.", testString2, (String) object1);
    }
    else {
      fail("Unable to get result from .lookup() method.");
    }

    // looking up for the map
    Object object2 = testCache.lookup("testMap");
    if (object2 instanceof TreeMap) {
      TreeMap<Integer, String> cachedMap = (TreeMap<Integer, String>) object2;
      assertEquals("Testing .lookup() method.", testString2, cachedMap.get(2));
    }
    else {
      fail("Unable to get result from .lookup() method.");
    }

    //
    // now we will try to remove one of the strings
    //
    try {
      testCache.remove(string2Name);
      assertNull("Testing .remove() method", testCache.lookup(string2Name));
    }
    catch (UriCacheException e) {
      fail("Unable to remove object from the cache: "
          + formatter.format(new LogRecord(Level.ALL, e.toString())));
    }

    //
    // and finally clean the cache.
    //
    try {
      testCache.clear();
      assertNull("Testing .clear() method", testCache.lookup("string1"));
      assertNull("Testing  .clear() method", testCache.lookup(string2Name));
      assertNull("Testing   .clear() method", testCache.lookup("string3"));
      assertNull("Testing    .clear() method", testCache.lookup("testMap"));
    }
    catch (UriCacheException e) {
      fail("Unable to remove object from the cache: "
          + formatter.format(new LogRecord(Level.ALL, e.toString())));
    }
  }

}
