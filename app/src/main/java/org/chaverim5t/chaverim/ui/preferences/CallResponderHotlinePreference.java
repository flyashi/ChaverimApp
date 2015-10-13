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
 * Preference that allows the user to call the Chaverim Responder hotline.
 */
public class CallResponderHotlinePreference extends CallHotlinePreference {
  public CallResponderHotlinePreference(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  String getText() {
    return "Call Responder Hotline";
  }

  @Override
  String getPhoneNumber() {
    return "+16468324285";
  }
}
