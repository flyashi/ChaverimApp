package org.chaverim5t.chaverim.ui.preferences;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import org.chaverim5t.chaverim.R;

/**
 * Preference that allows the user to call the Chaverim Dispatch hotline.
 */
public class CallDispatchHotlinePreference extends Preference {
  public CallDispatchHotlinePreference(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  protected void onBindView(View view) {
    super.onBindView(view);
    View textAndImage = view.findViewById(R.id.text_and_image);
    TextView textView = (TextView) textAndImage.findViewById(R.id.text);
    textView.setText("Call Dispatch Hotline");
  }
}
