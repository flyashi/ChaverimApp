package org.chaverim5t.chaverim;

/**
 * Created by yakov on 8/11/15.
 */
public class UserManager {
  private static UserManager userManager;

  public static UserManager getUserManager() {
    if (userManager == null) {
      userManager = new UserManager();
    }
    return userManager;
  }

  public boolean isDispatcher() {
    return true;
  }

  public boolean isResponder() {
    return true;
  }
}
