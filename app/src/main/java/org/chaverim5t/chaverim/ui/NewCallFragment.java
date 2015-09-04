package org.chaverim5t.chaverim.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.chaverim5t.chaverim.R;

/**
 * A fragment that allows a dispatcher to enter call details and dispatch the call.
 */
public class NewCallFragment extends Fragment {

  public NewCallFragment() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_new_call, container, false);

    return view;
  }


}
