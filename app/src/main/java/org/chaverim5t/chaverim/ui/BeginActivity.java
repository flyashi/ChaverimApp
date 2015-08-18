package org.chaverim5t.chaverim.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.chaverim5t.chaverim.R;

public class BeginActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_begin_screen);
    Button beginButton = (Button)findViewById(R.id.begin_button);
    beginButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);

        // to prevent back (do we want to prevent it here?):
        // Answer: No.
        /*
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        */
        startActivity(intent);
      }
    });
  }

}
