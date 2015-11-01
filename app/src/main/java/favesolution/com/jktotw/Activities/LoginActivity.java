package favesolution.com.jktotw.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import favesolution.com.jktotw.Helpers.SharedPreference;
import favesolution.com.jktotw.R;
import icepick.Icepick;

public class LoginActivity extends AppCompatActivity {
    @Bind(R.id.email_edit_login) EditText mEmailEditLogin;
    @Bind(R.id.password_edit_login)  EditText mPasswordEditLogin;
    @Bind(R.id.button_login) Button mLoginbutton;
    @Bind(R.id.text_link_skip) TextView mSkipLink;
    @Bind(R.id.text_link_register)TextView mRegisterLink;
    @Bind(R.id.login_form) View mLoginForm;
    @Bind(R.id.register_form) View mRegisterForm;
    @Bind(R.id.login_progress)ProgressBar mLoginProgressBar;
    @PlaybackStateCompat.State private boolean mIsRegisterOpen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mLoginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });
        mPasswordEditLogin.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == R.id.login || actionId == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        mSkipLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreference.setSkipLogin(LoginActivity.this, true);
                Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
        });
        mRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsRegisterOpen = true;
                changeForm();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public void onBackPressed() {
        if (mIsRegisterOpen) {
            mIsRegisterOpen = false;
            changeForm();
            return;
        }
        super.onBackPressed();
    }

    private void attemptLogin() {
        mEmailEditLogin.setError(null);
        mPasswordEditLogin.setError(null);
        String email = mEmailEditLogin.getText().toString();
        String password = mPasswordEditLogin.getText().toString();
        boolean cancel = false;
        View focusView = null;
        if (!isPasswordValid(password)) {
            mPasswordEditLogin.setError(getString(R.string.password_error));
            focusView = mPasswordEditLogin;
            cancel = true;
        }
        if (!isEmailValid(email)) {
            mEmailEditLogin.setError(getString(R.string.email_error));
            focusView = mEmailEditLogin;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            //TODO: login user with web service using Volley
        }
    }
    private void changeForm() {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_mediumAnimTime);
        mLoginForm.animate().setDuration(shortAnimTime).alpha(mIsRegisterOpen ? 0 : 1)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mLoginForm.setVisibility(mIsRegisterOpen ? View.GONE : View.VISIBLE);
                    }
                });
        mRegisterForm.animate().setDuration(shortAnimTime).alpha(mIsRegisterOpen ? 1 : 0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mLoginForm.setVisibility(mIsRegisterOpen ? View.VISIBLE : View.GONE);
                    }
                });
    }
    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_mediumAnimTime);
        mLoginForm.animate().setDuration(shortAnimTime).alpha(show?0:1)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mLoginForm.setVisibility(show ? View.GONE : View.VISIBLE);
                    }
                });
        mLoginProgressBar.animate().setDuration(shortAnimTime).alpha(show?1:0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mLoginProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                    }
                });
    }
    private boolean isEmailValid(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }
}
