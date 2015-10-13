package org.chaverim5t.chaverim.ui.preferences;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.preference.Preference;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import org.chaverim5t.chaverim.R;

/**
 * Preference that allows the user to call one of the Chaverim hotlines.
 */
public abstract class CallHotlinePreference extends Preference {
  public CallHotlinePreference(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  protected void onBindView(@NonNull View view) {
    super.onBindView(view);
    View textAndImage = view.findViewById(R.id.text_and_image);
    TextView textView = (TextView) textAndImage.findViewById(R.id.text);
    textView.setText(getText());
    textView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Uri dialUri = Uri.parse("tel:" + getPhoneNumber());
        Intent intent = new Intent(Intent.ACTION_VIEW, dialUri);
        getContext().startActivity(intent);
      }
    });
  }

  abstract String getText();
  abstract String getPhoneNumber();
}
