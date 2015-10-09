package org.chaverim5t.chaverim.ui;


import android.annotation.TargetApi;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import org.chaverim5t.chaverim.R;
import org.chaverim5t.chaverim.data.UserManager;

/**
 * A fragment that shows the settings for the app.
 * <p/>
 * A the top are three custom Preferences: one which displays the logged in user and allows them to
 * sign out, and two which allow the user to initiate a phone call to the dispatcher or responder
 * hotlines, respectively.
 * <p/>
 * Below that are the other preferences. Which ones are available vary by user permissions, e.g.
 * dispatchers don't have the option of auto-playing voice notes since they can never respond to a
 * call.
 */
@TargetApi(11)
public class SettingsSubFragment extends PreferenceFragment {

  private UserManager userManager;

  public SettingsSubFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    userManager = UserManager.getUserManager(getActivity());
    if (userManager.isDispatcher() && userManager.isResponder()) {
      addPreferencesFromResource(R.xml.pref_dispatcher_and_responder);
    } else if (userManager.isDispatcher()) {
      addPreferencesFromResource(R.xml.pref_dispatcher);
    } else {
      addPreferencesFromResource(R.xml.pref_responder);
    }
  }

}
