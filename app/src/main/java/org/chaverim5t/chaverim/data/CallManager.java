package org.chaverim5t.chaverim.data;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import org.chaverim5t.chaverim.util.NetworkUtils;

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
  private static CallManager callManager;
  private UserManager userManager;
  private SettingsManager settingsManager;
  private NetworkUtils networkUtils;

  public static CallManager getCallManager(Context context) {
    if (callManager == null) {
      callManager = new CallManager(context);
    }
    return callManager;
  }

  public CallManager(Context context) {
    settingsManager = SettingsManager.getSettingsManager(context);
    userManager = UserManager.getUserManager(context);
    networkUtils = NetworkUtils.getNetworkUtils(context);

    callsList = new ArrayList<>();
    Call call = new Call("Boost in Bayswater");
    call.coverage = Arrays.asList("T21", "W36");
    call.callerName = "FRANK";
    call.callerNumber = "7185556789";
    call.callId = 1723;
    call.callNumber = 7;
    call.createdTimestamp = (new GregorianCalendar(2015, 12, 12, 8, 44, 23)).getTimeInMillis();
    call.updatedTimestamp = (new GregorianCalendar(2015, 12, 12, 8, 45, 55)).getTimeInMillis();
    call.disptacherName = "T21";
    call.notes = "For a member";
    call.vehicle = "Black Town & Country";
    call.problem = "Boost";
    call.region = "Bayswater";
    call.status = "Covered";
    call.urgent = false;
    call.location = "BAY 24 & MOTT";

    VoiceNote voiceNote = new VoiceNote();
    voiceNote.author = "C2";
    voiceNote.duration = 12;
    voiceNote.noteID = 12345;
    call.voiceNotes = Arrays.asList(voiceNote);

    Call.Message message = call.newMessage();
    message.timestamp = (new GregorianCalendar(2015, 12, 12, 8, 44, 24)).getTimeInMillis();
    message.message = "[6] B: BOOST For a member @ BAY 24 & MOTT Black Town & Country T21";
    call.messages = Arrays.asList(message);

    callsList.add(call);
    callsList.add(new Call(1, "Flat in Far Rockaway"));
    callsList.add(new Call(2, "Car L/O in Cedarhurst"));
    callsList.add(new Call(3, "House L/O in Hewlett"));
    callsList.add(new Call(4, "Minyan needed in Mineola"));
    callsList.add(new Call(5, "Ignition problem in Inwood"));
    callsList.add(new Call(6, "Out of Gas in Oceanside"));

    callerIDList = new ArrayList<>();
    callerIDList.add(new CallerID("(718) 555-7212"));
    callerIDList.add(new CallerID("(516) 555-1324"));
    callerIDList.add(new CallerID("(917) 555-5309"));
    callerIDList.add(new CallerID("(347) 555-8264"));

    respondingCallsList = new ArrayList<>();
    updateRespondingList();
  }

  private ArrayList<Call> respondingCallsList;
  private ArrayList<Call> callsList;
  private ArrayList<CallerID> callerIDList;

  public boolean isResponding() {
    return respondingCallsList.size() != 0;
  }

  public List<Call> myRespondingCalls() {
    return respondingCallsList;
  }

  public List<Call> getAllCalls() {
    return callsList;
  }

  public List<CallerID> getCallerIDs() {
    return callerIDList;
  }

  public void updateRespondingList() {
    respondingCallsList.clear();
    for (Call call : callsList) {
      if (call.coverage != null) {
        for (String coverageUnitID : call.coverage) {
          if (coverageUnitID.equalsIgnoreCase(userManager.userID())) {
            respondingCallsList.add(call);
          }
        }
      }
    }
  }

  public void createAndDispatch(String callerName, String callerNumber, String location,
                                String problem, String area, String note, String vehicle) {
    if (settingsManager.dispatchOnNewSystem()) {
      // TODO(yakov): Switch to new system!
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
}
