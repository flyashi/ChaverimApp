package org.chaverim5t.chaverim.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yakov on 8/11/15.
 */
public class CallManager {
  private static CallManager callManager;

  public static CallManager getCallManager() {
    if (callManager == null) {
      callManager = new CallManager();
    }
    return callManager;
  }

  public CallManager() {
    respondingCallsList = new ArrayList<>();
    respondingCallsList.add(new Call("Boost in Bayswater"));
    respondingCallsList.add(new Call("Flat in Far Rockaway"));

    callsList = new ArrayList<>();
    callsList.add(new Call("Boost in Bayswater"));
    callsList.add(new Call("Flat in Far Rockaway"));
    callsList.add(new Call("Car L/O in Cedarhurst"));
    callsList.add(new Call("House L/O in Hewlett"));
    callsList.add(new Call("Minyan needed in Mineola"));
    callsList.add(new Call("Ignition problem in Inwood"));
    callsList.add(new Call("Out of Gas in Oceanside"));

    callerIDList = new ArrayList<>();
    callerIDList.add(new CallerID("(718) 555-7212"));
    callerIDList.add(new CallerID("(516) 555-1324"));
    callerIDList.add(new CallerID("(917) 555-5309"));
    callerIDList.add(new CallerID("(347) 555-8264"));

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
}
