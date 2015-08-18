package org.chaverim5t.chaverim.data;

/**
 * Created by yakov on 8/11/15.
 */
public class CallManager {
  private static CallManager callManager;

  public static CallManager getCallManager() {
    if (callManager == null) {
      callManager = new CallManager();
    }
    return callManager;
  }

  public boolean isResponding() {
    return true;
  }
}
