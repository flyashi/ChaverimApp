package org.chaverim5t.chaverim.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.chaverim5t.chaverim.util.NetworkUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Manages the logged in user. Currently only provides the userID, user name, whether or not the
 * user is a dispatcher, and whether or not the user is a responder, as well as {@link #fakeSignIn}
 * and {@link #signOut}.
 */
public class UserManager {
  private static final String USER_MANAGER_PREFS_NAME = "MyPrefsFile";
  private static final String TAG = UserManager.class.getSimpleName();

  private static UserManager userManager;
  private NetworkUtils networkUtils;
  private Context context;

  private Boolean fakeData = true;

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
    authToken = settings.getString("authToken", "");
    signedIn = (authToken.length() > 0);
    fakeData = settings.getBoolean("fakeData", true);
    userIsAdmin = settings.getBoolean("isDispatcher", false);
    userIsDispatcher = settings.getBoolean("isDispatcher", false);
    userIsResponder = settings.getBoolean("isResponder", false);

  }

  private void saveSharedPreferences() {
    SharedPreferences settings =
        context.getSharedPreferences(USER_MANAGER_PREFS_NAME, Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = settings.edit();
    editor.putString("userID", userID);
    editor.putString("userFullName", userFullName);
    editor.putString("authToken", authToken);
    editor.putBoolean("fakeData", fakeData);
    // Commit the edits!
    editor.commit();

  }
  private boolean signedIn;
  private String userID;
  private String userFullName;
  private String authToken;
  private boolean userIsDispatcher = false;
  private boolean userIsResponder = false;
  private boolean userIsAdmin = false;


  // TODO(yakov): Remove!
  private String oldDispatchSystemID;  // Dispatch 21 is "98"

  public void fakeSignIn() {
    signedIn = true;
    userID = "T21";
    userFullName = "Shlomo Markowitz";
    oldDispatchSystemID = "98";
    userIsDispatcher = true;
    userIsResponder = true;
    userIsAdmin = true;
    fakeData = true;
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
    return userIsDispatcher;
  }

  public boolean isResponder() {
    return userIsResponder;
  }

  public boolean isAdmin() {
    return userIsAdmin;
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

  public Request attemptSignIn(final String requestUserID, String password,
      final Response.Listener<JSONObject> userListener,
      final Response.ErrorListener userErrorListener) {
    Object[][] params = {{"unit_number", requestUserID}, {"password", password}};
    Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
      @Override
      public void onResponse(JSONObject response) {
        try {
          if (response.has("error") && !TextUtils.isEmpty(response.getString("error"))) {
            if (userErrorListener != null) {
              userErrorListener.onErrorResponse(new VolleyError(response.getString("error")));
            }
            return;
          }
          if (response.has("auth_token")) {
            authToken = response.getString("auth_token");
          } else {
            Log.w(TAG, "Expected auth_token but got none :(");
          }
          if (response.has("user")) {
            JSONObject userObject = response.getJSONObject("user");
            Log.d(TAG, "Parsing user: " + userObject.toString(2));
            if (userObject.has("unit_number")) {
              userID = userObject.getString("unit_number");
            }
            if (userObject.has("name")) {
              userFullName = userObject.getString("name");
            }
            if (userObject.has("is_responder")) {
              userIsResponder = userObject.getBoolean("is_responder");
            }
            if (userObject.has("is_dispatcher")) {
              userIsDispatcher = userObject.getBoolean("is_dispatcher");
            }
            if (userObject.has("is_admin")) {
              userIsAdmin = userObject.getBoolean("is_admin");
            }
            if (!userIsResponder && !userIsDispatcher) {
              Log.w(TAG, "WARNING: not responder or dispatcher! App will crash!");
            }
          } else {
            Log.w(TAG, "GOT NO USER!");
          }
        } catch (JSONException e) {
          Log.e(TAG, "attemptSignIn's listener got error", e);
        }
        if (userListener != null) {
          userListener.onResponse(response);
        }
        fakeData = false;
      }
    };
    return networkUtils.makeApiRequest("getauthtoken", params, listener, userErrorListener);
  }

  public Request attemptSignIn(String requestUserID, String password) {
    return attemptSignIn(requestUserID, password, null, null);
  }

  public String authToken() {
    return authToken;
  }

  public Boolean isFakeData() {
    return fakeData;
  }
}
