package org.hackystat.utilities.tstamp;

import java.util.HashSet;
import java.util.Set;

/**
 * Provides a way to guarantee unique timestamps by keeping track of the old ones and incrementing
 * the millisecond field as needed to create a unique one. The behavior of this class is to 
 * return the passed tstamp if it is not already in the set, or else return an incremented
 * version of the tstamp which is incremented enough times to be unique. 
 * 
 * @author Philip Johnson
 */
public class TstampSet {
  
  /** Holds all of the tstamps previously passed to this set. */
  private final Set<Long> tstampSet;
  
  /**
   * Create a new TstampSet, which is initialized with no knowledge of prior timestamps.
   */
  public TstampSet() {
    tstampSet = new HashSet<Long>();
  }
  
  /**
   * Return a new unique timestamp based upon the passed timestamp.  If the passed timestamp does
   * not exist in the timestamp set, then it is returned (and it is added to the set).  If
   * the passed timestamp already exists in the set, then it is repeatedly incremented until a
   * value is obtained that did not already exist in the timestamp set.  This value is then 
   * returned (and added to the set.)
   * 
   * @param tstamp The tstamp to be used as a basis for finding a unique timestamp.
   * @return A timestamp that did not previously exist in this TstampSet. 
   */
  public long getUniqueTstamp(long tstamp) {
    long currTstamp = tstamp;
    while (tstampSet.contains(currTstamp)) {
      currTstamp++;
    }
    tstampSet.add(currTstamp);
    return currTstamp;
  }
}
