package org.chaverim5t.chaverim.ui;

import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
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

  public NewCallFragment() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_new_call, container, false);

    this.problemText = (TextView) view.findViewById(R.id.problem_text);

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
    return view;
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putInt("LAST_BUTTON", selectedQuickButtonIndex);
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

}
