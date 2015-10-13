package org.chaverim5t.chaverim.data;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import org.chaverim5t.chaverim.util.NetworkUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores, updates, retrieves, and provides {@link Call} objects. Currently uses only fake data.
 */
public class CallManager {
  private static final String TAG = CallManager.class.getSimpleName();
  private static CallManager callManager;
  private final UserManager userManager;
  private final SettingsManager settingsManager;
  private final NetworkUtils networkUtils;

  public static CallManager getCallManager(Context context) {
    if (callManager == null) {
      callManager = new CallManager(context);
    }
    return callManager;
  }

  private CallManager(Context context) {
    settingsManager = SettingsManager.getSettingsManager(context);
    userManager = UserManager.getUserManager(context);
    networkUtils = NetworkUtils.getNetworkUtils(context);

    callsList = new ArrayList<>();
    respondingCallsList = new ArrayList<>();

    if (userManager.isFakeData()) {

      Call call = new Call("Boost in Bayswater");
      call.coverage = new ArrayList<>(Arrays.asList("T21", "W36"));
      call.callerName = "FRANK";
      call.phoneNumber = "7185556789";
      call.callId = 1723;
      call.callNumber = 7;
      call.createdTimestamp = (new GregorianCalendar(2015, 12, 12, 8, 44, 23)).getTimeInMillis();
      call.updatedTimestamp = (new GregorianCalendar(2015, 12, 12, 8, 45, 55)).getTimeInMillis();
      call.dispatcherName = "T21";
      call.notes = "For a member";
      call.vehicle = "Black Town & Country";
      call.problem = "Boost";
      call.area = "B";  // Bayswater
      call.status = "Covered";
      call.urgent = false;
      call.location = "BAY 24 & MOTT";

      VoiceNote voiceNote = new VoiceNote();
      voiceNote.author = "C2";
      voiceNote.duration = 12;
      voiceNote.noteID = 12345;
      call.voiceNotes = new ArrayList<>(Arrays.asList(voiceNote));

      Call.Message message = call.newMessage();
      message.timestamp = (new GregorianCalendar(2015, 12, 12, 8, 44, 24)).getTimeInMillis();
      message.message = "[6] B: BOOST For a member @ BAY 24 & MOTT Black Town & Country T21";
      call.messages = new ArrayList<>(Arrays.asList(message));

      callsList.add(call);
      callsList.add(new Call(1, "Flat in Far Rockaway"));
      callsList.add(new Call(2, "Car L/O in Cedarhurst"));
      callsList.add(new Call(3, "House L/O in Hewlett"));
      callsList.add(new Call(4, "Minyan needed in Mineola"));
      callsList.add(new Call(5, "Ignition problem in Inwood"));
      callsList.add(new Call(6, "Out of Gas in Oceanside"));

      updateRespondingList();
    }
  }

  private ArrayList<Call> respondingCallsList;
  private ArrayList<Call> callsList;

  public boolean isResponding() {
    return respondingCallsList.size() != 0;
  }

  public List<Call> myRespondingCalls() {
    return respondingCallsList;
  }

  public List<Call> getAllCalls() {
    return callsList;
  }

  public void updateRespondingList() {
    respondingCallsList.clear();
    for (Call call : callsList) {
      if (call.coverage != null) {
        for (String coverageUnitNumber : call.coverage) {
          if (coverageUnitNumber.equalsIgnoreCase(userManager.unitNumber())) {
            respondingCallsList.add(call);
          }
        }
      }
    }
  }

  public Request updateCalls(final Response.Listener<JSONObject> userListener,
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
          if (!response.has("calls")) {
            Log.w(TAG, "Expected calls[] but got none :(");
            return;
          }
          JSONArray calls = response.getJSONArray("calls");
          callsList.clear();
          for (int callNum = 0; callNum < calls.length(); callNum++) {
            JSONObject callObj = calls.getJSONObject(callNum);
            Call call = new Call();
            call.status = optionalString(callObj, "call_status");
            call.callerName = optionalString(callObj, "caller_name");
            //call.createdTimestamp = // We get it in "2015-10-04 20:55:24.942102" :-/
            call.area = optionalString(callObj, "call_area");
            call.phoneNumber = optionalString(callObj, "phone_number");
            call.urgent = callObj.has("is_urgent") && callObj.getBoolean("is_urgent");
            //call.updatedTimestamp = // again, need to agree on a time format
            call.notes = optionalString(callObj, "notes");
            call.callId = callObj.has("id") ? callObj.getLong("id") : -1;
            call.vehicle = optionalString(callObj, "vehicle_description");
            call.location = optionalString(callObj, "location");
            call.callNumber = (callObj.has("call_number") && !callObj.isNull("call_number")) ? callObj.getInt("call_number") : -1;
            /*
            JSONObject userObject = response.getJSONObject("user");
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
            */
            callsList.add(call);
          }
          updateRespondingList();
        } catch (JSONException e) {
          Log.e(TAG, "CallManager updateCalls listener got error", e);
        }
        if (userListener != null) {
          userListener.onResponse(response);
        }
      }
    };
    Object[][] arr = {{"auth_token", userManager.authToken()}};
    return networkUtils.makeApiRequest("getcalls", arr, listener, userErrorListener);
  }

  public void createAndDispatch(String callerName, String callerNumber, String location,
                                String problem, String area, String note, String vehicle) {
    if (settingsManager.dispatchOnNewSystem()) {
      // TODO(yakov): Switch to new system!
      Log.d(TAG, "New system is not available yet. Not dispatching.");
    } else {
      Map<String, String> params = makePostData(callerName, callerNumber, problem, location, area, vehicle, note);
      MyRequest myRequest = new MyRequest(Request.Method.POST, "URL", null, null, params);
      networkUtils.addRequest(myRequest);
    }
  }

  // TODO(yakov): Remove this once no longer needed.
  private Map<String, String> makePostData(String callerName, String callerNumber,
                                           String problem, String location,
                                           String area, String vehicle, String note) {
    Map<String, String> map = new HashMap<>();

    map.put("CallDispatcherID", userManager.oldDispatchSystemID());
    map.put("CallAutoRouteToDispatcher", "1");
    map.put("CallEmailDispatchArea", "9");
    map.put("CallerName", callerName);
    map.put("CallerPhoneNumber", callerNumber);
    // TODO(yakov): Allow urgent calls!
    map.put("CallIsUrgent", "0");
    map.put("CallAutoSend", "1");
    map.put("AC_CallDetails", problem);
    map.put("CallDetails1", problem);
    map.put("AC_AddPre", location);
    // TODO(yakov): Apartment?
    map.put("AC_apt", "");
    // TODO(yakov): Cross streets?
    map.put("Between1", "");
    map.put("Between2", "");
    map.put("Combination", "");
    map.put("Area", area);
    map.put("AC_AreaID", area);
    map.put("AC_Color", "");
    map.put("AC_Model", vehicle);
    map.put("notes", note);
    map.put("CallDetails", "");
    map.put("AddPre", "");
    map.put("apt", "");
    map.put("AreaID", "");
    map.put("Color", "");
    map.put("Model", "");

    return map;
  }

  class MyRequest extends StringRequest {
    private Map<String, String> params;

    public MyRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener, Map<String, String> params) {
      super(method, url, listener, errorListener);
      this.params = params;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
      return params;
    }
  }

  private String optionalString(JSONObject obj, String key) throws JSONException {
    return optionalString(obj, key, "");
  }

  private String optionalString(JSONObject obj, String key, String defaultValue) throws JSONException {
    return obj.has(key) ? obj.getString(key) : defaultValue;
  }
}
