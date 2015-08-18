package org.chaverim5t.chaverim.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.chaverim5t.chaverim.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RespondingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RespondingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RespondingFragment extends Fragment {


  /**
   * Use this factory method to create a new instance of
   * this fragment using the provided parameters.
   *
   * //@param param1 Parameter 1.
   * //@param param2 Parameter 2.
   * @return A new instance of fragment RespondingFragment.
   */
  // TODO: Rename and change types and number of parameters
  public static RespondingFragment newInstance() {
    RespondingFragment fragment = new RespondingFragment();
    Bundle args = new Bundle();
    //args.putString(ARG_PARAM1, param1);
    //args.putString(ARG_PARAM2, param2);
    fragment.setArguments(args);
    return fragment;
  }

  public RespondingFragment() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      //mParam1 = getArguments().getString(ARG_PARAM1);
      //mParam2 = getArguments().getString(ARG_PARAM2);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_responding, container, false);
  }
}
