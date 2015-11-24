package com.favesolution.jktotw.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.favesolution.jktotw.Models.Place;
import com.favesolution.jktotw.Networks.UrlEndpoint;
import com.favesolution.jktotw.R;
import com.favesolution.jktotw.Utils.SharedPreference;
import com.favesolution.jktotw.Utils.UIHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class AddReviewActivity extends AppCompatActivity {
    public static String EXTRA_PLACE = "extra_place";
    private Place mPlace;
    boolean isAdding;
    @Bind(R.id.edit_review) EditText mEditReview;
    @Bind(R.id.text_count_word) TextView mTextCountWord;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);
        ButterKnife.bind(this);
        mPlace = getIntent().getParcelableExtra(EXTRA_PLACE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        UIHelper.showOverflowMenu(this);
        mEditReview.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    addReview();
                }
                return handled;
            }
        });
        mEditReview.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTextCountWord.setText(mEditReview.getText().length()+"");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_review, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_add_review:
                if(!isAdding)
                    addReview();
                return false;
        }
        return super.onOptionsItemSelected(item);
    }
    private void addReview() {
        if ((mEditReview.getText().toString().length()) != 0) {
            String url;
            isAdding = true;
            if (mPlace.getType().getCategoryName().equals(getString(R.string.category_indosat))) {
                url = UrlEndpoint.insertIndosatReview();
            } else {
                url = UrlEndpoint.insertPlaceReview();
            }
            RequestParams params = new RequestParams();
            params.put("id", mPlace.getId());
            params.put("Review", mEditReview.getText().toString());
            AsyncHttpClient client = new AsyncHttpClient();
            client.addHeader("token", SharedPreference.getUserToken(this));
            final ProgressDialog progress = ProgressDialog.show(this, "Uploading", "Upload your review", true);
            client.post(url, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    try {
                        progress.dismiss();
                        String result = response.getString("status");
                        if (result.equalsIgnoreCase("success")) {
                            Toast.makeText(AddReviewActivity.this, "Add Review Success", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent();
                            setResult(RESULT_OK, i);
                            finish();
                        } else {
                            Toast.makeText(AddReviewActivity.this, "Add Review Failure", Toast.LENGTH_SHORT).show();
                            isAdding = false;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    isAdding = false;
                    progress.dismiss();
                    Log.e("AddReviewActivity", responseString);
                }
            });
        }
    }
}
