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
    Call call1 = new Call();
    call1.title = "Boost in Bayswater";
    respondingCallsList.add(call1);

    Call call2 = new Call();
    call2.title = "Flat in Far Rockaway";
    respondingCallsList.add(call2);
  }

  private ArrayList<Call> respondingCallsList;

  public boolean isResponding() {
    return respondingCallsList.size() != 0;
  }

  public List<Call> myRespondingCalls() {
    return respondingCallsList;
  }
}
