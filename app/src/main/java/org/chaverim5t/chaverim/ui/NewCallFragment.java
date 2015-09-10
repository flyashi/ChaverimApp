package org.chaverim5t.chaverim.ui;

import android.graphics.LightingColorFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.chaverim5t.chaverim.R;

import java.util.ArrayList;

/**
 * A fragment that allows a dispatcher to enter call details and dispatch the call.
 */
public class NewCallFragment extends Fragment {

  private final LightingColorFilter quickButtonSelectedColorFilter =
      new LightingColorFilter(-1, 255);

  private int selectedQuickButtonIndex = -1;
  private TextView problemText;
  private TextView otherCallAreaText;

  private View.OnClickListener quickButtonOnClickListener;

  private final int[] quickButtonIds = {
      R.id.quick_button_flat,
      R.id.quick_button_boost,
      R.id.quick_button_car_lockout,
      R.id.quick_button_house_lockout,
      R.id.quick_button_more
  };

  private final String[] quickButtonStrings = {
      "Flat",
      "Boost",
      "Car L/O",
      "House L/O",
      ""
  };

  private final ArrayList<ImageView> quickButtons = new ArrayList<>();

  private View.OnClickListener callAreaButtonOnClickListener;

  private final int[] callAreaButtonIds = {
      R.id.new_call_area_878,
      R.id.new_call_area_ab,
      R.id.new_call_area_b,
      R.id.new_call_area_bh,
      R.id.new_call_area_c,
      R.id.new_call_area_f,
      R.id.new_call_area_fh,
      R.id.new_call_area_gn,
      R.id.new_call_area_h,
      R.id.new_call_area_i,
      R.id.new_call_area_jfk,
      R.id.new_call_area_kgh,
      R.id.new_call_area_l,
      R.id.new_call_area_lb,
      R.id.new_call_area_o,
      R.id.new_call_area_vs,
      R.id.new_call_area_w,
      R.id.new_call_area_other // Keep "Other" as last.
  };

  private final ArrayList<TextView> callAreaButtons = new ArrayList<>();
  private int selectedCallAreaButtonIndex = -1;

  public NewCallFragment() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_new_call, container, false);

    this.problemText = (TextView) view.findViewById(R.id.problem_text);
    this.otherCallAreaText = (TextView) view.findViewById(R.id.other_call_area_text);

    setupQuickButtons(view, savedInstanceState);
    setupCallAreaButtons(view, savedInstanceState);
    return view;
  }

  private void setupQuickButtons(View view, Bundle savedInstanceState) {
    quickButtons.clear();
    quickButtonOnClickListener = new QuickButtonOnClickListener();
    for (int i = 0; i < quickButtonIds.length; i++) {
      ImageView imageView = (ImageView) view.findViewById(quickButtonIds[i]);
      imageView.setOnClickListener(quickButtonOnClickListener);
      quickButtons.add(imageView);
    }

    int index = 0;
    if (savedInstanceState != null) {
      index = savedInstanceState.getInt("LAST_BUTTON", 0);
    }
    quickButtonOnClickListener.onClick(quickButtons.get(index));
  }

  private void setupCallAreaButtons(View view, Bundle savedInstanceState) {
    callAreaButtons.clear();
    callAreaButtonOnClickListener = new CallAreaButtonOnClickListener();
    for (int i = 0; i < callAreaButtonIds.length; i++) {
      View button = view.findViewById(callAreaButtonIds[i]);
      button.setOnClickListener(callAreaButtonOnClickListener);
      callAreaButtons.add((TextView)button);
    }
    int index = 2; // Bayswater
    if (savedInstanceState != null) {
      index = savedInstanceState.getInt("LAST_AREA", 2);
    }
    callAreaButtonOnClickListener.onClick(callAreaButtons.get(index));
  }
  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putInt("LAST_BUTTON", selectedQuickButtonIndex);
    outState.putInt("LAST_AREA", selectedCallAreaButtonIndex);
  }


  private class QuickButtonOnClickListener implements ImageView.OnClickListener {
    @Override
    public void onClick(View v) {
      int index = quickButtons.indexOf(v);
      ImageView imageView = (ImageView)v;
      if (index == selectedQuickButtonIndex) {
        // Already selected, do nothing.
        return;
      }
      selectedQuickButtonIndex = index;
      for (ImageView quickButton : quickButtons) {
        if (quickButton.equals(imageView)) {
          quickButton.setColorFilter(quickButtonSelectedColorFilter);
        } else {
          quickButton.setColorFilter(null);
        }
      }
      /*
      if (imageView == quickButtons.get(quickButtons.size() - 1)) { // "More"
        problemText.setVisibility(View.VISIBLE);
      } else {
        problemText.setVisibility(View.GONE);
      }
      */
      problemText.setText(quickButtonStrings[index]);
    }
  }

  private class CallAreaButtonOnClickListener implements View.OnClickListener {

    @Override
    public void onClick(View v) {
      int index = callAreaButtons.indexOf(v);
      if (index == selectedCallAreaButtonIndex) {
        // Already selected, do nothing
        return;
      }
      selectedCallAreaButtonIndex = index;
      TextView selectedTextView = (TextView) v;
      for (TextView otherTextView : callAreaButtons) {
        if (selectedTextView.equals(otherTextView)) {
          if (Build.VERSION.SDK_INT >= 23) {
            selectedTextView.setBackgroundColor(getResources().getColor(R.color.primary_material_dark, null));
            selectedTextView.setTextColor(getResources().getColor(R.color.primary_material_light, null));
          } else {
            selectedTextView.setBackgroundColor(getResources().getColor(R.color.primary_material_dark));
            selectedTextView.setTextColor(getResources().getColor(R.color.primary_material_light));
          }
        } else {
          if (Build.VERSION.SDK_INT >= 23) {
            otherTextView.setBackgroundColor(getResources().getColor(R.color.primary_material_light, null));
            otherTextView.setTextColor(getResources().getColor(R.color.primary_material_dark, null));
          } else {
            otherTextView.setBackgroundColor(getResources().getColor(R.color.primary_material_light));
            otherTextView.setTextColor(getResources().getColor(R.color.primary_material_dark));
          }
        }
      }
      if (index == callAreaButtonIds.length - 1) {
        otherCallAreaText.setVisibility(View.VISIBLE);
      } else {
        otherCallAreaText.setVisibility(View.GONE);
      }
    }
  }
}
