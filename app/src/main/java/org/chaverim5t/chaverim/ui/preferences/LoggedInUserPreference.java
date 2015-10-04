package org.chaverim5t.chaverim.ui.preferences;

import android.content.Context;
import android.content.Intent;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.chaverim5t.chaverim.R;
import org.chaverim5t.chaverim.data.UserManager;
import org.chaverim5t.chaverim.ui.BeginActivity;

/**
 * Preference that displays the currently logged in user, and allows the user to sign out.
 */
public class LoggedInUserPreference extends Preference {
  UserManager userManager;

  @SuppressWarnings("unused")
  public LoggedInUserPreference(Context context) {
    super(context);
    userManager = UserManager.getUserManager(getContext());
  }

  @SuppressWarnings("unused")
  public LoggedInUserPreference(Context context, AttributeSet attributeSet) {
    super(context, attributeSet);
    userManager = UserManager.getUserManager(getContext());
  }

  @Override
  protected void onBindView(View view) {
    super.onBindView(view);
    TextView textView = (TextView) view.findViewById(R.id.text);
    Button button = (Button) view.findViewById(R.id.log_out_button);
    textView.setText("You are logged in as: " + userManager.userID() + "\n" + userManager.userFullName());
    button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        userManager.signOut();
        Intent intent = new Intent(getContext(), BeginActivity.class);
        // prevent back
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // should call finish() but need an activity
        getContext().startActivity(intent);
      }
    });
  }
}
