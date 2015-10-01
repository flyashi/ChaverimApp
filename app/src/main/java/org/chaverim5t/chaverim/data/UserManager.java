package org.chaverim5t.chaverim.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.Request;
import com.android.volley.Response;

import org.chaverim5t.chaverim.util.NetworkUtils;

import java.util.HashMap;

/**
 * Manages the logged in user. Currently only provides the userID, user name, whether or not the
 * user is a dispatcher, and whether or not the user is a responder, as well as {@link #fakeSignIn}
 * and {@link #signOut}.
 */
public class UserManager {
  private static final String USER_MANAGER_PREFS_NAME = "MyPrefsFile";

  private static UserManager userManager;
  private NetworkUtils networkUtils;
  private Context context;

  public static UserManager getUserManager(Context context) {
    if (userManager == null) {
      userManager = new UserManager(context);
    }
    return userManager;
  }

  public UserManager(Context context) {
    this.context = context;
    this.networkUtils = NetworkUtils.getNetworkUtils(context);
    loadSharedPreferences();
  }

  private void loadSharedPreferences() {
    SharedPreferences settings =
        context.getSharedPreferences(USER_MANAGER_PREFS_NAME, Context.MODE_PRIVATE);
    userID = settings.getString("userID", "");
    userFullName = settings.getString("userFullName", "");
    signedIn = (userID.length() > 0);
  }

  private void saveSharedPreferences() {
    SharedPreferences settings =
        context.getSharedPreferences(USER_MANAGER_PREFS_NAME, Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = settings.edit();
    editor.putString("userID", userID);
    editor.putString("userFullName", userFullName);

    // Commit the edits!
    editor.commit();

  }
  private boolean signedIn;
  private String userID;
  private String userFullName;

  // TODO(yakov): Remove!
  private String oldDispatchSystemID;  // Dispatch 21 is "98"

  public void fakeSignIn() {
    signedIn = true;
    userID = "T21";
    userFullName = "Shlomo Markowitz";
    oldDispatchSystemID = "98";
    saveSharedPreferences();
  }

  public void signOut() {
    signedIn = false;
    userID = "";
    userFullName = "";
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

  public String userFullName() {
    return userFullName;
  }

  public String oldDispatchSystemID() {
    return oldDispatchSystemID;
  }

  public Request attemptSignIn(String userID, String password, Response.Listener listener,
                            Response.ErrorListener errorListener) {
    Object[][] params = {{"unit_number", userID}, {"password", password}};
    return networkUtils.makeApiRequest("getauthtoken", params, listener, errorListener);
  }

  public Request attemptSignIn(String userID, String password) {
    return attemptSignIn(userID, password, null, null);
  }
}
