package org.chaverim5t.chaverim.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.chaverim5t.chaverim.R;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RespondingTileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RespondingTileFragment extends Fragment {
  // TODO: Rename parameter arguments, choose names that match
  // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
  private static final String ARG_CALL_ID = "call_id";

  // TODO: Rename and change types of parameters
  private int callID;

  /**
   * Use this factory method to create a new instance of
   * this fragment using the provided parameters.
   *
   * @param callID The ID number of the call the member is responding to.
   * @return A new instance of fragment RespondingTileFragment.
   */
  // TODO: Rename and change types and number of parameters
  public static RespondingTileFragment newInstance(int callID) {
    RespondingTileFragment fragment = new RespondingTileFragment();
    Bundle args = new Bundle();
    args.putInt(ARG_CALL_ID, callID);
    fragment.setArguments(args);
    return fragment;
  }

  public RespondingTileFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      callID = getArguments().getInt(ARG_CALL_ID);
    }
    if (callID == 0) {
      throw new IllegalArgumentException("Call ID not provided.");
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_responding_tile, container, false);
  }
}
