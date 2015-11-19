package com.favesolution.jktotw.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.favesolution.jktotw.Activities.AddReviewActivity;
import com.favesolution.jktotw.Activities.DetailPlaceActivity;
import com.favesolution.jktotw.Adapters.ReviewAdapter;
import com.favesolution.jktotw.Models.Place;
import com.favesolution.jktotw.R;
import com.favesolution.jktotw.Utils.DividerItemDecoration;
import com.favesolution.jktotw.Utils.SharedPreference;

import butterknife.Bind;
import butterknife.ButterKnife;
public class ReviewFragment extends Fragment {
    private static final String ARGS_PLACE = "args_place";
    private Place mPlace;
    private static final int ADD_REVIEW_REQUEST = 1;
    @Bind(R.id.recyclerview) RecyclerView mRecyclerView;
    @Bind(R.id.edit_review) EditText mEditText;
    @Bind(R.id.text_placeholder) TextView mTextPlaceholder;
    @Bind(R.id.content_add_review) View mViewReview;
    public static ReviewFragment newInstance(Place place) {
        Bundle args = new Bundle();
        args.putParcelable(ARGS_PLACE, place);
        ReviewFragment fragment = new ReviewFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        mPlace = getArguments().getParcelable(ARGS_PLACE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_review, container, false);
        ButterKnife.bind(this, v);
        setActionBarTitle(mPlace.getName() + " Reviews");
        if (SharedPreference.getUserToken(getActivity()) == null) {
            mEditText.setVisibility(View.GONE);
        }
        mEditText.requestFocus();
        mEditText.setRawInputType(InputType.TYPE_CLASS_TEXT);
        mEditText.setTextIsSelectable(true);
        mEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addReview();
            }
        });
        mViewReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addReview();
            }
        });
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new ReviewAdapter(mPlace.getReviews()));
        checkPlaceholderDisplay();
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode!= Activity.RESULT_OK)
            return;
        if (requestCode == ADD_REVIEW_REQUEST) {
            Intent i = DetailPlaceActivity.newIntent(getActivity(),mPlace);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            getActivity().finish();
        }
    }

    private void setActionBarTitle(String title) {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle(title);
    }
    private void checkPlaceholderDisplay() {
        if (mRecyclerView.getAdapter().getItemCount() == 0) {
            mTextPlaceholder.setVisibility(View.VISIBLE);
        }
    }
    private void addReview() {
        Intent i = new Intent(getActivity(), AddReviewActivity.class);
        i.putExtra(AddReviewActivity.EXTRA_PLACE,mPlace);
        startActivityForResult(i, ADD_REVIEW_REQUEST);
    }
}
