package org.chaverim5t.chaverim.ui;


import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import org.chaverim5t.chaverim.R;


/**
 * Shows either a Text View showing that settings aren't available below API 11 (Android 3.0), or
 * the settings via a {@link SettingsSubFragment}.
 */
public class SettingsFragment extends Fragment {

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
      view = inflater.inflate(R.layout.fragment_settings, container, false);
      // Inflate the layout for this fragment]
      View noSettingsTextView = view.findViewById(R.id.settings_only_on_android_30_and_up_text);
      View settingsFragment = view.findViewById(R.id.settings_sub_fragment);
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        noSettingsTextView.setVisibility(View.GONE);
        settingsFragment.setVisibility(View.VISIBLE);
      } else {
        noSettingsTextView.setVisibility(View.VISIBLE);
        settingsFragment.setVisibility(View.GONE);
      }
    } catch (InflateException e) {
        /* Fragment is already there, just return view as it is */
    }

    return view;
  }

}
