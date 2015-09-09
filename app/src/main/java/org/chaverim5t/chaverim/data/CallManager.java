package org.chaverim5t.chaverim.data;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Stores, updates, retrieves, and provides {@link Call} objects. Currently uses only fake data.
 */
public class CallManager {
  private static CallManager callManager;
  private UserManager userManager;

  public static CallManager getCallManager(Context context) {
    if (callManager == null) {
      callManager = new CallManager(context);
    }
    return callManager;
  }

  public CallManager(Context context) {
    userManager = UserManager.getUserManager(context);
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
}
