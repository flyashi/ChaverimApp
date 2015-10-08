package org.chaverim5t.chaverim.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.chaverim5t.chaverim.R;
import org.chaverim5t.chaverim.data.UserManager;
import org.chaverim5t.chaverim.util.NetworkUtils;
import org.chaverim5t.chaverim.util.PhoneNumberFormatter;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A login screen that offers login via SMS or username and password.
 *
 * NOTE: There's a LOT of unneeded boilerplate left over from Android Studio's default LoginAcivity.
 * I removed all the Loaders since we have at least one user on API 10; we'll have to settle for
 * {@link AsyncTask}s. However there's still a lot of boilerplate left.
 */
public class LoginActivity extends Activity {

  /**
   * A dummy authentication store containing known user names and passwords.
   * TODO: remove after connecting to a real authentication system.
   */
  private static final String[] DUMMY_CREDENTIALS = new String[]{
      "foo@example.com:hello", "bar@example.com:world"
  };
  private static final String TAG = LoginActivity.class.getSimpleName();
  // Keep track of the login request to ensure we can cancel it if requested.
  private Request loginOrSmsRequest = null;

  // UI references.
  private EditText mPhoneNumberView;
  private AutoCompleteTextView mUsernameView;
  private EditText mPasswordView;
  private View mProgressView;
  private View mLoginFormView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    //setupActionBar();

    // don't auto-show keyboard
    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    // Set up the login form.
    mUsernameView = (AutoCompleteTextView) findViewById(R.id.username);
    populateAutoComplete();

    mPasswordView = (EditText) findViewById(R.id.password);
    mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
        if (id == R.id.login || id == EditorInfo.IME_NULL) {
          attemptLogin();
          return true;
        }
        return false;
      }
    });

    Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
    mEmailSignInButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        attemptLogin();
      }
    });

    final Button verifyButton = (Button) findViewById(R.id.sms_button);
    verifyButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        attemptRequestSMS();

      }
    });
    
    mLoginFormView = findViewById(R.id.login_form);
    mProgressView = findViewById(R.id.login_progress);
    mPhoneNumberView = (EditText) findViewById(R.id.phone_number_text);
  }

  private void populateAutoComplete() {
    if (VERSION.SDK_INT >= 8) {
      // Use AccountManager (API 8+)
      new SetupEmailAutoCompleteTask().execute(null, null);
    }
  }

  /**
   * Set up the {@link android.app.ActionBar}, if the API is available.
   */
  @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  private void setupActionBar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      // Show the Up button in the action bar.
      getActionBar().setDisplayHomeAsUpEnabled(true);
    }
  }

  /**
   * Attempts to sign in or register the account specified by the login form.
   * If there are form errors (invalid email, missing fields, etc.), the
   * errors are presented and no actual login attempt is made.
   */
  public void attemptLogin() {

    // just do the fake login
    // TODO(yakov): Do the real login!
    /* this works for fake signin
    UserManager.getUserManager(this).fakeSignIn();
    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
    // prevent back
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    finish();
    startActivity(intent);
    */

    if (loginOrSmsRequest != null) {
      return;
    }

    // Reset errors.
    mUsernameView.setError(null);
    mPasswordView.setError(null);

    // Store values at the time of the login attempt.
    String email = mUsernameView.getText().toString();
    String password = mPasswordView.getText().toString();

    boolean cancel = false;
    View focusView = null;

    // Check for a valid password, if the user entered one.
    if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
      mPasswordView.setError(getString(R.string.error_invalid_password));
      focusView = mPasswordView;
      cancel = true;
    }

    // Check for a valid email address.
    if (TextUtils.isEmpty(email)) {
      mUsernameView.setError(getString(R.string.error_field_required));
      focusView = mUsernameView;
      cancel = true;
    } else if (!isEmailValid(email)) {
      mUsernameView.setError(getString(R.string.error_invalid_email));
      focusView = mUsernameView;
      cancel = true;
    }

    if (cancel) {
      // There was an error; don't attempt login and focus the first
      // form field with an error.
      focusView.requestFocus();
    } else {
      // Show a progress spinner, and kick off a background task to
      // perform the user login attempt.
      showProgress(true);
      //mAuthTask = new UserLoginTask(email, password);
      //mAuthTask.execute((Void) null);

      /*
            mAuthTask = null;
      showProgress(false);

      if (success) {
        finish();
      } else {
        mPasswordView.setError(getString(R.string.error_incorrect_password));
        mPasswordView.requestFocus();
      }
       */
      final Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
          loginOrSmsRequest = null;
          showProgress(false);
          Snackbar.make(mLoginFormView, "Error: " + error.getLocalizedMessage(), Snackbar.LENGTH_SHORT).show();
          mPasswordView.setError(error.getLocalizedMessage() /*getString(R.string.error_incorrect_password)*/);
          mPasswordView.requestFocus();
        }
      };
      Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
          loginOrSmsRequest = null;
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
            Log.e(TAG, "Error getting JSON in response", e);
            errorListener.onErrorResponse(new VolleyError(e));
          }
        }
      };
      loginOrSmsRequest =
          UserManager.getUserManager(this).attemptSignIn(email, password, listener, errorListener);
    }
  }

  private void attemptRequestSMS() {
    /*
        Snackbar snackbar = Snackbar.make(verifyButton, "Hello", Snackbar.LENGTH_SHORT);
        snackbar.show();
        */
    //Toast.makeText(getApplicationContext(), "Hello", Toast.LENGTH_SHORT).show();
        /*
        UserManager.getUserManager(getApplicationContext()).fakeSignIn();

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        // prevent back
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(intent);
        */


    if (loginOrSmsRequest != null) {
      Log.d(TAG, "loginOrSmsRequest is not null. Quitting...");
      return;
    }

    String phoneNumber = mPhoneNumberView.getText().toString();
    if (!PhoneNumberFormatter.isValid(phoneNumber)) {
      Log.d(TAG, "Not a valid phone number: " + phoneNumber + ". Quitting...");
      mPhoneNumberView.setError(getString(R.string.error_invalid_phone_number));
      mPhoneNumberView.requestFocus();
      return;
    }
    mPhoneNumberView.setError(null);
    showProgress(true);

    String[][] params = {{"phone_number", phoneNumber}};
    final Response.ErrorListener errorListener = new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {
        loginOrSmsRequest = null;
        showProgress(false);
        Log.e(TAG, "SMSRequest Volley errorListener", error);
        mPhoneNumberView.setError(error.getLocalizedMessage());
        mPhoneNumberView.requestFocus();
      }
    };
    Response.Listener<JSONObject> listener = new Response.Listener<JSONObject>() {
      @Override
      public void onResponse(JSONObject response) {
        loginOrSmsRequest = null;
        try {
          if (!response.has("error") || (response.has("error") && !TextUtils.isEmpty(response.getString("error")))) {
            if (response.has("error")) {
              errorListener.onErrorResponse(new VolleyError(response.getString("error")));
            } else {
              errorListener.onErrorResponse(new VolleyError("Empty Response"));
            }
          } else {
            showProgress(false);

            Intent intent = new Intent(getApplicationContext(), VerifySMSActivity.class);
            intent.putExtra(VerifySMSActivity.PHONE_NUMBER_TAG, mPhoneNumberView.getText().toString());
            // Allow the user to go back? Yes. Therefore, comment this out.
            /*
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            */
            startActivity(intent);
          }
        } catch (JSONException e) {
          errorListener.onErrorResponse(new VolleyError(e));
        }
      }
    };
    loginOrSmsRequest = NetworkUtils.getNetworkUtils(getApplicationContext())
        .makeApiRequest("smsrequest", params, listener, errorListener);
  }
  private boolean isEmailValid(String email) {
    //TODO: Replace this with your own logic
    return true; //email.contains("@");
  }

  private boolean isPasswordValid(String password) {
    //TODO: Replace this with your own logic
    return true; //password.length() > 4;
  }

  /**
   * Shows the progress UI and hides the login form.
   */
  @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
  public void showProgress(final boolean show) {
    // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
    // for very easy animations. If available, use these APIs to fade-in
    // the progress spinner.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
      int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

      mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
      mLoginFormView.animate().setDuration(shortAnimTime).alpha(
          show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
          mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
      mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
    }
  }

  /**
   * Use an AsyncTask to fetch the user's email addresses on a background thread, and update
   * the email text field with results on the main UI thread.
   */
  class SetupEmailAutoCompleteTask extends AsyncTask<Void, Void, List<String>> {

    @Override
    protected List<String> doInBackground(Void... voids) {
      ArrayList<String> emailAddressCollection = new ArrayList<String>();

      // Get all emails from the user's contacts and copy them to a list.
      ContentResolver cr = getContentResolver();
      Cursor emailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
          null, null, null);
      while (emailCur.moveToNext()) {
        String email = emailCur.getString(emailCur.getColumnIndex(ContactsContract
            .CommonDataKinds.Email.DATA));
        emailAddressCollection.add(email);
      }
      emailCur.close();

      return emailAddressCollection;
    }

    @Override
    protected void onPostExecute(List<String> emailAddressCollection) {
      addEmailsToAutoComplete(emailAddressCollection);
    }
  }

  private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
    //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
    ArrayAdapter<String> adapter =
        new ArrayAdapter<String>(LoginActivity.this,
            android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

    mUsernameView.setAdapter(adapter);
  }

}

