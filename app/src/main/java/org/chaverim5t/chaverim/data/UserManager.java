package org.chaverim5t.chaverim.data;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by yakov on 8/11/15.
 */
public class UserManager {
  private static final String USER_MANAGER_PREFS_NAME = "MyPrefsFile";

  private static UserManager userManager;
  private Context context;

  public static UserManager getUserManager(Context context) {
    if (userManager == null) {
      userManager = new UserManager(context);
    }
    return userManager;
  }

  public UserManager(Context context) {
    this.context = context;

    loadSharedPreferences();
  }

  private void loadSharedPreferences() {
    SharedPreferences settings =
        context.getSharedPreferences(USER_MANAGER_PREFS_NAME, Context.MODE_PRIVATE);
    userID = settings.getString("userID", "");
    userName = settings.getString("userName", "");
    signedIn = (userID.length() > 0);
  }

  private void saveSharedPreferences() {
    SharedPreferences settings =
        context.getSharedPreferences(USER_MANAGER_PREFS_NAME, Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = settings.edit();
    editor.putString("userID", userID);
    editor.putString("userName", userName);

    // Commit the edits!
    editor.commit();

  }
  private boolean signedIn;
  private String userID;
  private String userName;

  public void fakeSignIn() {
    signedIn = true;
    userID = "T21";
    userName = "Shlomo Markowitz";
    saveSharedPreferences();
  }

  public void signOut() {
    signedIn = false;
    userID = "";
    userName = "";
    saveSharedPreferences();
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
