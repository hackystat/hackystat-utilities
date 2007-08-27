package org.hackystat.utilities.uricache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.GregorianCalendar;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

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
    //
    // making cache instance first
    //
    try {
      testCache = UriCacheFactory.getURICacheInstance();
    }
    catch (UriCacheException e) {
      fail("Unable to instantiate cache: "
          + formatter.format(new LogRecord(Level.ALL, e.toString())));
    }
    // constructing "complex" object for testing purposes
    testMap.put(1, testString1);
    testMap.put(2, testString2);
    testMap.put(3, testString3);
  }

  /**
   * Complex cache test, tests getters, remove and clear.
   */
  @SuppressWarnings("unchecked")
  @Test
  public void testCache() {

    //
    // clean the cache first
    //
    try {
      testCache.clear();
    }
    catch (UriCacheException e) {
      fail("Unable to remove object from the cache: "
          + formatter.format(new LogRecord(Level.ALL, e.toString())));
    }

    //
    // caching objects at second.
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

    // now test the load
    // try {
    // int cnt = 525;
    // for (int i = 0; i < cnt; i++) {
    // IElementAttributes eAttr = new ElementAttributes();
    // eAttr.setIsSpool(true);
    // testCache.cache("key:" + i, "data:" + i);
    // }
    //
    // for (int i = 0; i < cnt; i++) {
    // String element = (String) testCache.lookup("key:" + i);
    // assertNotNull("presave, Should have recevied an element.", element);
    // assertEquals("presave, element is wrong.", "data:" + i, element);
    // }
    // }
    // catch (UriCacheException e) {
    // fail("Unable to proceed with load test: "
    // + formatter.format(new LogRecord(Level.ALL, e.toString())));
    //    }
  }

  /**
   * Tests cache autodeprecation.
   */
  @Test
  public void testTimedCache() {
    // clean cache first
    try {
      testCache.clear();
    }
    catch (UriCacheException e) {
      fail("Unable to remove object from the cache: "
          + formatter.format(new LogRecord(Level.ALL, e.toString())));
    }

    //
    // caching object.
    //
    try {
      DatatypeFactory factory = null;
      factory = DatatypeFactory.newInstance();
      GregorianCalendar calendar = new GregorianCalendar();
      calendar.setTimeInMillis(System.currentTimeMillis() + 1);

      testCache.setMaxMemoryIdleTimeSeconds(5);

      testCache.cache("string1", testString1, factory.newXMLGregorianCalendar(calendar));

      System.out.println("Testing caching system: waiting for cache to be autocleaned.");

      Thread.sleep(12000);

      // testCache.freeMemoryElements(1);

      // test that objects is within the cache
      assertNull("Testing cache with expiration", testCache.lookup("string1"));

    }
    catch (UriCacheException e) {
      fail("Unable to put objects into cache: "
          + formatter.format(new LogRecord(Level.ALL, e.toString())));
    }
    catch (DatatypeConfigurationException e) {
      fail("Cannot get dataFactory working: "
          + formatter.format(new LogRecord(Level.ALL, e.toString())));
    }
    catch (InterruptedException e) {
      fail("Cannot really do the sleep() stuff: "
          + formatter.format(new LogRecord(Level.ALL, e.toString())));
    }

  }

}
