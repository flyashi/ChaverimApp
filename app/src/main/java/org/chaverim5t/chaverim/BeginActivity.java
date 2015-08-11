package org.chaverim5t.chaverim;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

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
        startActivity(intent);
      }
    });
  }

}
