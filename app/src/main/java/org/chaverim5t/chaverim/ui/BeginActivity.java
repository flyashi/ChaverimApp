package org.chaverim5t.chaverim.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.chaverim5t.chaverim.R;
import org.chaverim5t.chaverim.data.UserManager;

/**
 * The first activity that appears when the app is launched. It provides some text info and a
 * button to proceed to the Login screen.
 */
public class BeginActivity extends AppCompatActivity {

  private UserManager userManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    userManager = UserManager.getUserManager(this.getApplicationContext());
    if (userManager.isSignedIn()) {
      Intent intent = new Intent(getApplicationContext(), MainActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      finish();
      startActivity(intent);
    }

    setContentView(R.layout.activity_begin_screen);
    Button beginButton = (Button)findViewById(R.id.begin_button);
    beginButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);

        // To prevent back, uncomment this.
        // Here, we don't want to prevent back, so this is commented.
        /*
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        */
        startActivity(intent);
      }
    });
  }

}
