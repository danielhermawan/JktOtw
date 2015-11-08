package com.favesolution.jktotw.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.favesolution.jktotw.NetworkUtils.CustomJsonRequest;
import com.favesolution.jktotw.NetworkUtils.UrlEndpoint;
import com.favesolution.jktotw.NetworkUtils.RequestQueueSingleton;
import com.favesolution.jktotw.Helpers.SharedPreference;
import com.favesolution.jktotw.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
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
    @Bind(R.id.name_field_register) EditText mNameField;
    @Bind(R.id.phone_field_register) EditText mPhoneField;
    @Bind(R.id.email_field_register) EditText mEmailRegisterField;
    @Bind(R.id.password_field_register) EditText mPasswordRegisterField;
    @Bind(R.id.repassword_field_register) EditText mRepasswordField;
    @Bind(R.id.button_register) Button mRegisterButton;
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
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
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
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegister();
            }
        });
        mRepasswordField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == R.id.login || actionId == EditorInfo.IME_NULL) {
                    attemptRegister();
                    return true;
                }
                return false;
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
            showProgress(false);
            RequestQueueSingleton.getInstance(this).getRequestQueue().cancelAll(this);
            mIsRegisterOpen = false;
            changeForm();
            return;
        }
        super.onBackPressed();
    }
    private void attemptRegister() {
        mNameField.setError(null);
        mPhoneField.setError(null);
        mEmailRegisterField.setError(null);
        mPasswordRegisterField.setError(null);
        mRepasswordField.setError(null);
        final String email = mEmailRegisterField.getText().toString();
        final String phone= mPhoneField.getText().toString();
        final String name = mNameField.getText().toString();
        final String password = mPasswordRegisterField.getText().toString();
        final String repassword = mRepasswordField.getText().toString();
        boolean cancel = false;
        View focusView = null;
        if (!repassword.equals(password)) {
            mRepasswordField.setError(getString(R.string.repassword_error));
            focusView = mRepasswordField;
            cancel = true;
        }
        if (!isPasswordValid(password)) {
            mPasswordRegisterField.setError(getString(R.string.password_error));
            focusView = mPasswordRegisterField;
            cancel = true;
        }
        if (!isEmailValid(email)) {
            mEmailRegisterField.setError(getString(R.string.email_error));
            focusView = mEmailEditLogin;
            cancel = true;
        }
        if (!isPhoneValid(phone)) {
            mPhoneField.setError(getString(R.string.phone_error));
            focusView = mPhoneField;
            cancel = true;
        }
        if (name.length() < 3) {
            mNameField.setError(getString(R.string.name_error));
            focusView = mNameField;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);

        }
        HashMap<String, String> params = new HashMap<>();
        params.put("nama",name);
        params.put("email", email);
        params.put("noHP", phone);
        params.put("password", password);
        CustomJsonRequest requestRegister = new CustomJsonRequest( Method.POST, UrlEndpoint.registerUser(),params,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        showProgress(false);
                        try {
                            if (response.getString("status").equalsIgnoreCase("error")) {
                                mEmailRegisterField.setError(response.getString("message"));
                            } else {
                                Toast.makeText(LoginActivity.this, R.string.Register_Success,Toast.LENGTH_LONG).show();
                                mIsRegisterOpen = false;
                                changeForm();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this,"Network error", Toast.LENGTH_SHORT).show();
                        Log.e("error",error.networkResponse.statusCode+"");
                        showProgress(false);
                    }
                });
        requestRegister.setTag(this);
        requestRegister.setPriority(Request.Priority.HIGH);
        RequestQueueSingleton.getInstance(this).addToRequestQueue(requestRegister);
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
            HashMap<String, String> params = new HashMap<>();
            params.put("email", email);
            params.put("password", password);
            CustomJsonRequest loginRequest = new CustomJsonRequest(Method.POST, UrlEndpoint.loginUser(), params,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            showProgress(false);
                            try {
                                if (response.getString("status").equalsIgnoreCase("error")) {
                                    mEmailEditLogin.setError(response.getString("message"));
                                } else {
                                    SharedPreference.setUserToken(LoginActivity.this,response.getString("token"));
                                    Toast.makeText(LoginActivity.this, R.string.login_Success,Toast.LENGTH_LONG).show();
                                    Intent i = new Intent(LoginActivity.this,MainActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);
                                    finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(LoginActivity.this,"Network error", Toast.LENGTH_SHORT).show();
                            Log.e("error",error.getMessage());
                            showProgress(false);
                        }
                    });
            loginRequest.setTag(this);
            loginRequest.setPriority(Request.Priority.HIGH);
            RequestQueueSingleton.getInstance(this).addToRequestQueue(loginRequest);
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
                        mRegisterForm.setVisibility(mIsRegisterOpen ? View.VISIBLE : View.GONE);
                    }
                });
    }
    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_mediumAnimTime);
        if (mIsRegisterOpen) {
            mRegisterForm.animate().setDuration(shortAnimTime).alpha(show?0:1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mRepasswordField.setVisibility(show ? View.GONE : View.VISIBLE);
                        }
                    });
        } else {
            mLoginForm.animate().setDuration(shortAnimTime).alpha(show?0:1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoginForm.setVisibility(show ? View.GONE : View.VISIBLE);
                        }
                    });
        }
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
    private boolean isPhoneValid(String phone) {
        return TextUtils.isDigitsOnly(phone) && (phone.length() == 11 || phone.length() == 12);
    }
}
