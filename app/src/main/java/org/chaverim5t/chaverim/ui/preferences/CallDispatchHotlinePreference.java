package org.chaverim5t.chaverim.ui.preferences;

import android.content.Context;
import android.preference.Preference;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.chaverim5t.chaverim.R;

/**
 * Preference that allows the user to call the Chaverim Dispatch hotline.
 */
public class CallDispatchHotlinePreference extends Preference {
  private static final String TAG = CallDispatchHotlinePreference.class.getSimpleName();

  public CallDispatchHotlinePreference(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  protected void onBindView(@Nullable View view) {
    super.onBindView(view);
    if (view == null) {
      Log.w(TAG, "View is null!");
    }
    View textAndImage = view.findViewById(R.id.text_and_image);
    TextView textView = (TextView) textAndImage.findViewById(R.id.text);
    textView.setText("Call Dispatch Hotline");
  }
}
