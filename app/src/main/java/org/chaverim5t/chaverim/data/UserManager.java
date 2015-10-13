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

/**
 * Manages the logged in user. Currently only provides the unitNumber, user name, whether or not the
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

  private UserManager(Context context) {
    this.context = context;
    this.networkUtils = NetworkUtils.getNetworkUtils(context);
    loadSharedPreferences();
  }

  private void loadSharedPreferences() {
    SharedPreferences settings =
        context.getSharedPreferences(USER_MANAGER_PREFS_NAME, Context.MODE_PRIVATE);
    unitNumber = settings.getString("unitNumber", "");
    userFullName = settings.getString("userFullName", "");
    authToken = settings.getString("authToken", "");
    signedIn = (authToken.length() > 0);
    fakeData = settings.getBoolean("fakeData", true);
    userIsAdmin = settings.getBoolean("isAdmin", false);
    userIsDispatcher = settings.getBoolean("isDispatcher", false);
    userIsResponder = settings.getBoolean("isResponder", false);

    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("unitNumber: " + unitNumber + "\n");
    stringBuilder.append("userFullName: " + userFullName + "\n");
    stringBuilder.append("authToken: " + authToken + "\n"); // TODO(yakov): DON'T LOG AUTH TOKENS!
    stringBuilder.append("signedIn: " + signedIn + "\n");
    stringBuilder.append("fakeData: " + fakeData + "\n");
    stringBuilder.append("userIsAdmin: " + userIsAdmin + "\n");
    stringBuilder.append("userIsDispatcher: " + userIsDispatcher + "\n");
    stringBuilder.append("userIsResponder: " + userIsResponder + "\n");
    Log.d(TAG, "Loaded shared preferences: " + stringBuilder.toString());
  }

  private void saveSharedPreferences() {
    SharedPreferences settings =
        context.getSharedPreferences(USER_MANAGER_PREFS_NAME, Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = settings.edit();
    editor.putString("unitNumber", unitNumber);
    editor.putString("userFullName", userFullName);
    editor.putString("authToken", authToken);
    editor.putBoolean("fakeData", fakeData);
    editor.putBoolean("isDispatcher", userIsDispatcher);
    editor.putBoolean("isResponder", userIsResponder);
    editor.putBoolean("isAdmin", userIsAdmin);

    // Commit the edits!
    editor.commit();

  }
  private boolean signedIn;
  private String unitNumber;
  private String userFullName;
  private String authToken;
  private boolean userIsDispatcher = false;
  private boolean userIsResponder = false;
  private boolean userIsAdmin = false;


  // TODO(yakov): Remove!
  private String oldDispatchSystemID;  // Dispatch 21 is "98"

  public void fakeSignIn() {
    signedIn = true;
    unitNumber = "Fake21";
    userFullName = "Fake User";
    oldDispatchSystemID = "98";
    userIsDispatcher = true;
    userIsResponder = true;
    userIsAdmin = true;
    fakeData = true;
    authToken = "fake_auth_token";
    saveSharedPreferences();
  }

  public void signOut() {
    signedIn = false;
    unitNumber = "";
    userFullName = "";
    authToken = "";
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

  public String unitNumber() {
    return unitNumber;
  }

  public String userFullName() {
    return userFullName;
  }

  public String oldDispatchSystemID() {
    return oldDispatchSystemID;
  }

  public Request attemptSignIn(final String requestUnitNumber, String password,
      final Response.Listener<JSONObject> userListener,
      final Response.ErrorListener userErrorListener) {
    Object[][] params = {{"unit_number", requestUnitNumber}, {"password", password}};
    return attemptSignIn(params, userListener, userErrorListener);
  }

  public Request attemptPhoneNumberVerification(final String phoneNumber, final String verificationCode,
                               final Response.Listener<JSONObject> userListener,
                               final Response.ErrorListener userErrorListener) {
    Object[][] params = {{"phone_number", phoneNumber}, {"code", verificationCode}};
    return attemptSignIn(params, userListener, userErrorListener);
  }

  private final Request attemptSignIn(Object[][] params,
                                      final Response.Listener<JSONObject> userListener,
                                      final Response.ErrorListener userErrorListener) {
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
          processAuthTokenResponse(response);
          saveSharedPreferences();
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

  public Request attemptSignIn(String requestUnitNumber, String password) {
    return attemptSignIn(requestUnitNumber, password, null, null);
  }

  private void processAuthTokenResponse(JSONObject response) throws JSONException {
    if (response.has("auth_token")) {
      authToken = response.getString("auth_token");
    } else {
      Log.w(TAG, "Expected auth_token but got none :(");
    }
    if (response.has("user")) {
      JSONObject userObject = response.getJSONObject("user");
      Log.d(TAG, "Parsing user: " + userObject.toString(2));
      if (userObject.has("unit_number")) {
        unitNumber = userObject.getString("unit_number");
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
      if (userIsAdmin) {
        Log.i(TAG, "User is admin, adding responder & dispatcher priveleges...");
        userIsResponder = userIsDispatcher = true;
      }
    } else {
      Log.w(TAG, "GOT NO USER!");
    }
  }

  public String authToken() {
    return authToken;
  }

  public Boolean isFakeData() {
    return fakeData;
  }
}
