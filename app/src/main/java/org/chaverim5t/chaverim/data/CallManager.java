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
}
