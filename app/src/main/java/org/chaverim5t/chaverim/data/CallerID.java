package org.chaverim5t.chaverim.data;

/**
 * Models a phone call to the Chaverim hotline.
 */
public class CallerID {
  public String phoneNumber;
  public long timestamp;

  public CallerID() {
    timestamp = 1234567890123l;  // millis!
  }

  public CallerID(String phoneNumber) {
    this();
    this.phoneNumber = phoneNumber;
  }
}
