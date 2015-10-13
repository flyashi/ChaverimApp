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
public class CallDispatchHotlinePreference extends CallHotlinePreference {
  public CallDispatchHotlinePreference(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  String getText() {
    return "Call Dispatch Hotline";
  }

  @Override
  String getPhoneNumber() {
    return "+17183371800";
  }
}
