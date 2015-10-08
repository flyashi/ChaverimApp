package org.chaverim5t.chaverim.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.chaverim5t.chaverim.R;
import org.chaverim5t.chaverim.data.UserManager;
import org.chaverim5t.chaverim.util.NetworkUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class VerifySMSActivity extends AppCompatActivity {

  public static final String PHONE_NUMBER_TAG = "PHONE_NUMBER";
  private static final String TAG = VerifySMSActivity.class.getSimpleName();
  private String phoneNumber;

  private EditText mCodeTextView;
  private View mInputForm;
  private View mProgressView;
  private Button mVerifyButton;

  private Request<JSONObject> verifyRequest;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_verify_sms);
    
    if (savedInstanceState != null && savedInstanceState.containsKey(PHONE_NUMBER_TAG)) {
      phoneNumber = savedInstanceState.getString(PHONE_NUMBER_TAG);
    } else {
      Bundle bundle = getIntent().getExtras();
      if (bundle != null && bundle.containsKey(PHONE_NUMBER_TAG)) {
        phoneNumber = bundle.getString(PHONE_NUMBER_TAG);
      } else {
        Log.w(TAG, "I need a phone number!");
        phoneNumber = "PHONE_NUMBER not passed in Intent bundle!";
      }
    }


    mCodeTextView = (EditText) findViewById(R.id.sms_code);

    mVerifyButton = (Button) findViewById(R.id.verify_button);
    mVerifyButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        attemptVerify();
      }
    });

    mProgressView = findViewById(R.id.verify_progress);
    mInputForm = findViewById(R.id.input_view);
  }

  private void attemptVerify() {
    // SMS code length is currently fixed at 4.
    String smsCode = mCodeTextView.getText().toString();
    if (smsCode.length() != 4) {
      mCodeTextView.setError("Code must be 4 digits long");
      mCodeTextView.requestFocus();
      return;
    }
    mCodeTextView.setError(null);
    showProgress(true);
    Log.d(TAG, "Attempting verify...");

    final Response.ErrorListener errorListener = new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {
        verifyRequest = null;
        showProgress(false);
        mCodeTextView.setError(error.getLocalizedMessage());
        Log.e(TAG, "SMS verify Volley errorListener", error);
        return;
      }
    };
    Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
      @Override
      public void onResponse(JSONObject response) {
        verifyRequest = null;
        showProgress(false);
        try {
          if (response.has("error") && !TextUtils.isEmpty(response.getString("error"))) {
            errorListener.onErrorResponse(new VolleyError(response.getString("error")));
            return;
          }
          if (!response.has("auth_token")) {
            errorListener.onErrorResponse(new VolleyError("No auth token"));
            return;
          }
          Intent intent = new Intent(getApplicationContext(), MainActivity.class);
          // prevent back
          intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
          finish();
          startActivity(intent);
        } catch (JSONException e) {
          errorListener.onErrorResponse(new VolleyError(e));
        }
      }
    };
    verifyRequest = UserManager.getUserManager(getApplicationContext())
        .attemptPhoneNumberVerification(phoneNumber, smsCode, listener, errorListener);
  }

  public void showProgress(final boolean show) {
    // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
    // for very easy animations. If available, use these APIs to fade-in
    // the progress spinner.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
      int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

      mInputForm.setVisibility(show ? View.GONE : View.VISIBLE);
      mInputForm.animate().setDuration(shortAnimTime).alpha(
          show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
          mInputForm.setVisibility(show ? View.GONE : View.VISIBLE);
        }
      });

      mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
      mProgressView.animate().setDuration(shortAnimTime).alpha(
          show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
          mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
      });
    } else {
      // The ViewPropertyAnimator APIs are not available, so simply show
      // and hide the relevant UI components.
      mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
      mInputForm.setVisibility(show ? View.GONE : View.VISIBLE);
    }
  }
}
