package org.chaverim5t.chaverim.data;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.chaverim5t.chaverim.util.NetworkUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Manages Caller ID entries.
 */
public class CallerIDManager {
  private static final String TAG = CallerIDManager.class.getSimpleName();
  private static CallerIDManager callerIDManager;
  private final Context context;
  private final NetworkUtils networkUtils;
  private final UserManager userManager;
  private final ArrayList<CallerID> callerIDList;
  private final ArrayList<CallerID> fakeList;

  public ArrayList<CallerID> callerIDList() {
    return userManager.isFakeData() ? fakeList : callerIDList;
  }

  public CallerIDManager(Context context) {
    this.context = context;
    networkUtils = NetworkUtils.getNetworkUtils(context);
    userManager = UserManager.getUserManager(context);

    callerIDList = new ArrayList<>();

    fakeList = new ArrayList<>();
    fakeList.add(new CallerID("(718) 555-7212"));
    fakeList.add(new CallerID("(516) 555-1324"));
    fakeList.add(new CallerID("(917) 555-5309"));
    fakeList.add(new CallerID("(347) 555-8264"));
  }

  public static CallerIDManager getCallerIDManager(Context context) {
    if (callerIDManager == null) {
      callerIDManager = new CallerIDManager(context);
    }
    return callerIDManager;
  }

  public Request<JSONObject> updateCallerIDList(Response.Listener<JSONObject> userListener,
                                                Response.ErrorListener userErrorListener) {
    return updateCallerIDList(false, userListener, userErrorListener);
  }

  public Request<JSONObject> updateCallerIDList(boolean force,
                                                final Response.Listener<JSONObject> userListener,
                                                final Response.ErrorListener userErrorListener) {
    Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
      @Override
      public void onResponse(JSONObject response) {
        try {
          if (response.has("error") && !TextUtils.isEmpty(response.getString("error"))) {
            if (userListener != null) {
              userListener.onResponse(response);
            }
            return;
          }
          if (!response.has("caller_ids")) {
            Log.w(TAG, "Expected caller_ids[] but got none :(");
            if (userErrorListener != null) {
              userErrorListener.onErrorResponse(new VolleyError("No caller_ids[]"));
            }
            return;
          }
          JSONArray callerIDs = response.getJSONArray("caller_ids");
          callerIDList.clear();
          SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

          for (int callerIDNum = 0; callerIDNum < callerIDs.length(); callerIDNum++) {
            JSONObject callerIDObj = callerIDs.getJSONObject(callerIDNum);
            CallerID callerID = new CallerID();
            // 2015-10-12 18:02:12
            callerID.timestamp = sdf.parse(optionalString(callerIDObj, "time_received")).getTime();
            // TODO(yakov): implement
            callerID.notes = new CallerID.Note[0];
            callerID.phoneNumber = optionalString(callerIDObj, "phone_number");
            //callsList.add(call);
            // will this destroy notes, etc.?
            callerIDList.add(callerID);
          }
          Collections.sort(callerIDList, new Comparator<CallerID>() {
            @Override
            public int compare(CallerID lhs, CallerID rhs) {
              return - Long.compare(lhs.timestamp, rhs.timestamp);
            }
          });
          //updateRespondingList();
        } catch (JSONException e) {
          Log.e(TAG, "CallManager updateCalls listener got error", e);
        } catch (ParseException e) {
          Log.e(TAG, "Date parser failed", e);
        }
        if (userListener != null) {
          userListener.onResponse(response);
        }
      }
    };
    Object[][] arr = {{"auth_token", userManager.authToken()}, {"update", force}};
    return networkUtils.makeApiRequest("getcallerids", arr, listener, userErrorListener);
  }

  private String optionalString(JSONObject obj, String key) throws JSONException {
    return optionalString(obj, key, "");
  }

  private String optionalString(JSONObject obj, String key, String defaultValue) throws JSONException {
    return obj.has(key) ? obj.getString(key) : defaultValue;
  }
}
