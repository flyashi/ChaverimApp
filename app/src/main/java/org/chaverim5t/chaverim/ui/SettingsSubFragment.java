package org.chaverim5t.chaverim.ui;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.chaverim5t.chaverim.R;
import org.chaverim5t.chaverim.data.UserManager;

/**
 * A simple {@link Fragment} subclass.
 */
@TargetApi(11)
public class SettingsSubFragment extends PreferenceFragment {

  private final UserManager userManager = UserManager.getUserManager();

  public SettingsSubFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (userManager.isDispatcher() && userManager.isResponder()) {
      addPreferencesFromResource(R.xml.pref_dispatcher_and_responder);
    } else if (userManager.isDispatcher()) {
      addPreferencesFromResource(R.xml.pref_dispatcher);
    } else {
      addPreferencesFromResource(R.xml.pref_responder);
    }
  }

}
