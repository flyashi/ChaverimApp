package org.chaverim5t.chaverim.data;

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

  public String userID() {
    return "T21";
  }
  
  public String userName() {
    return "Shlomo Markowitz";
  }
}
