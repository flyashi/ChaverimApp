package org.chaverim5t.chaverim.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
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
import android.widget.Toast;

import org.chaverim5t.chaverim.R;
import org.chaverim5t.chaverim.data.UserManager;

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
  /**
   * Keep track of the login task to ensure we can cancel it if requested.
   */
  private UserLoginTask mAuthTask = null;

  // UI references.
  private AutoCompleteTextView mEmailView;
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
    mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
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

    final Button verifyButton = (Button) findViewById(R.id.verify_sms_button);
    verifyButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        /*
        Snackbar snackbar = Snackbar.make(verifyButton, "Hello", Snackbar.LENGTH_SHORT);
        snackbar.show();
        */
        Toast.makeText(getApplicationContext(), "Hello", Toast.LENGTH_SHORT).show();
        /*
        UserManager.getUserManager(getApplicationContext()).fakeSignIn();
        // TODO(yakov): Change to VerifyActivity.class, once that's implemented!
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        // prevent back
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(intent);
        */
      }
    });
    
    mLoginFormView = findViewById(R.id.login_form);
    mProgressView = findViewById(R.id.login_progress);
  }

  private void populateAutoComplete() {
    if (VERSION.SDK_INT >= 14) {
      // Use ContactsContract.Profile (API 14+)
      //getLoaderManager().initLoader(0, null, this);
    } else if (VERSION.SDK_INT >= 8) {
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
    UserManager.getUserManager(this).fakeSignIn();
    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
    // prevent back
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    finish();
    startActivity(intent);

    if (mAuthTask != null) {
      return;
    }

    // Reset errors.
    mEmailView.setError(null);
    mPasswordView.setError(null);

    // Store values at the time of the login attempt.
    String email = mEmailView.getText().toString();
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
      mEmailView.setError(getString(R.string.error_field_required));
      focusView = mEmailView;
      cancel = true;
    } else if (!isEmailValid(email)) {
      mEmailView.setError(getString(R.string.error_invalid_email));
      focusView = mEmailView;
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
      mAuthTask = new UserLoginTask(email, password);
      mAuthTask.execute((Void) null);
    }
  }

  private boolean isEmailValid(String email) {
    //TODO: Replace this with your own logic
    return email.contains("@");
  }

  private boolean isPasswordValid(String password) {
    //TODO: Replace this with your own logic
    return password.length() > 4;
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

    mEmailView.setAdapter(adapter);
  }

  /**
   * Represents an asynchronous login/registration task used to authenticate
   * the user.
   */
  public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

    private final String mEmail;
    private final String mPassword;

    UserLoginTask(String email, String password) {
      mEmail = email;
      mPassword = password;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
      // TODO: attempt authentication against a network service.

      try {
        // Simulate network access.
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        return false;
      }

      for (String credential : DUMMY_CREDENTIALS) {
        String[] pieces = credential.split(":");
        if (pieces[0].equals(mEmail)) {
          // Account exists, return true if the password matches.
          return pieces[1].equals(mPassword);
        }
      }

      // TODO: register the new account here.
      return true;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
      mAuthTask = null;
      showProgress(false);

      if (success) {
        finish();
      } else {
        mPasswordView.setError(getString(R.string.error_incorrect_password));
        mPasswordView.requestFocus();
      }
    }

    @Override
    protected void onCancelled() {
      mAuthTask = null;
      showProgress(false);
    }
  }
}

