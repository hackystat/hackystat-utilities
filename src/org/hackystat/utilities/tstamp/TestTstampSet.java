package org.hackystat.utilities.tstamp;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Tests the TstampSet class. 
 * @author Philip Johnson
 */
public class TestTstampSet {
  /**
   * Tests the tstampset. 
   * Instantiates the set, adds some stuff,and checks to see if it's copacetic.
   */
  @Test public void testSet () {
    long tstamp = 0;
    TstampSet tstampSet = new TstampSet();
    assertEquals("Test empty set", tstamp, tstampSet.getUniqueTstamp(tstamp));
    assertEquals("Test addition", tstamp + 1, tstampSet.getUniqueTstamp(tstamp));
    tstamp = 50;
    assertEquals("Test missing entry", tstamp, tstampSet.getUniqueTstamp(tstamp));
    assertEquals("Test addition", tstamp + 1, tstampSet.getUniqueTstamp(tstamp));
    tstamp = 0;
    assertEquals("Test out of order entry", tstamp + 2, tstampSet.getUniqueTstamp(tstamp));
    assertEquals("Test addition", tstamp + 3, tstampSet.getUniqueTstamp(tstamp));
    
  }
}
