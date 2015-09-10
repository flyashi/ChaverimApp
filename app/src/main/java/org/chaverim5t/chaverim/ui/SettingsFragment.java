package org.chaverim5t.chaverim.ui;


import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.chaverim5t.chaverim.R;


/**
 * Shows either a Text View showing that settings aren't available below API 11 (Android 3.0), or
 * the settings via a {@link SettingsSubFragment}.
 */
public class SettingsFragment extends Fragment {

  private static final String TAG = SettingsFragment.class.getSimpleName();
  private static View view;

  public SettingsFragment() {
    // Required empty public constructor
  }


  // From StackOverflow - when a fragment within a fragment goes out of view and reappears, don't
  // recreate it if not necessary.
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    if (view != null) {
      ViewGroup parent = (ViewGroup) view.getParent();
      if (parent != null)
        parent.removeView(view);
    }
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        view = inflater.inflate(R.layout.fragment_settings_3_0_and_up, container, false);
      } else {
        view = inflater.inflate(R.layout.fragment_settings_below_3_0, container, false);
      }
    } catch (InflateException e) {
        /* Fragment is already there, just return view as it is */
      //Log.d(TAG, "Probably, fragment is already there. Just to be sure, here's the error", e);
    }

    return view;
  }

}
