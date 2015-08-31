package org.chaverim5t.chaverim.ui.preferences;

import android.content.Context;
import android.preference.Preference;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import org.chaverim5t.chaverim.R;

public class CallResponderHotlinePreference extends Preference {
  public CallResponderHotlinePreference(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  protected void onBindView(@NonNull View view) {
    super.onBindView(view);
    View textAndImage = view.findViewById(R.id.text_and_image);
    TextView textView = (TextView) textAndImage.findViewById(R.id.text);
    textView.setText("Call Responder Hotline");
  }
}
