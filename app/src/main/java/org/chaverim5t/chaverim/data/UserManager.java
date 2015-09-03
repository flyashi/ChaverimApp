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

  public UserManager() {
    signedIn = false;
  }

  private boolean signedIn;
  private String userID;
  private String userName;

  public void fakeSignIn() {
    signedIn = true;
    userID = "T21";
    userName = "Shlomo Markowitz";
  }

  public void signOut() {
    signedIn = false;
    userID = "x - logged out";
    userName = "xx - logged out";
  }
  public boolean isSignedIn() {
    return signedIn;
  }

  public boolean isDispatcher() {
    return true;
  }

  public boolean isResponder() {
    return true;
  }

  public String userID() {
    return userID;
  }

  public String userName() {
    return userName;
  }
}
