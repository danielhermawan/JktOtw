package com.favesolution.jktotw.Fragments;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.favesolution.jktotw.Activities.ListPlacesActivity;
import com.favesolution.jktotw.Helpers.DividerItemDecoration;
import com.favesolution.jktotw.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HomeFragment extends Fragment {
    @Bind(R.id.recyclerview) RecyclerView mRecyclerView;
    private TypedArray mCategoryList;
    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.fragment_home,container,false);
        ButterKnife.bind(this,v);
        mCategoryList = getResources().obtainTypedArray(R.array.categories);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new CategoryAdapter(mCategoryList));
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        mRecyclerView.addItemDecoration(itemDecoration);
        return v;
    }
    private class CategoryHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private TextView mTextView;
        private int mPosition;
        public CategoryHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.category_text);
            itemView.setOnClickListener(this);
        }
        public void bindCategoryItem(String category,int position) {
            mPosition = position;
            mTextView.setText(category);
        }
        @Override
        public void onClick(View v) {
            getActivity().startActivity(ListPlacesActivity.newIntent(getActivity(),mPosition));
        }
    }

    private class CategoryAdapter extends RecyclerView.Adapter<CategoryHolder> {
        private TypedArray mCategoryItems;
        public CategoryAdapter(TypedArray categoryItems) {
            mCategoryItems = categoryItems;
        }
        @Override
        public CategoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View v = inflater.inflate(R.layout.list_categories,parent,false);
            return new CategoryHolder(v);
        }

        @Override
        public void onBindViewHolder(CategoryHolder holder, int position) {
            String category = mCategoryItems.getString(position);
            holder.bindCategoryItem(category,position);
        }

        @Override
        public int getItemCount() {
            return mCategoryItems.length();
        }
    }


}
