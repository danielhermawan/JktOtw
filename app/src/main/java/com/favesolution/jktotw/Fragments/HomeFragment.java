package com.favesolution.jktotw.Fragments;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.favesolution.jktotw.Activities.SearchActivity;
import com.favesolution.jktotw.Models.Type;
import com.favesolution.jktotw.R;
import com.favesolution.jktotw.Utils.DividerItemDecoration;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HomeFragment extends Fragment {
    @Bind(R.id.recyclerview) RecyclerView mRecyclerView;
    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.fragment_home,container,false);
        ButterKnife.bind(this,v);
        List<Type> types = Type.getCategory(getActivity());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new CategoryAdapter(types));
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        mRecyclerView.addItemDecoration(itemDecoration);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_search, menu);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.item_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(getActivity(), SearchActivity.class)));
        searchView.setIconifiedByDefault(true);

    }


    private class CategoryHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private TextView mTextView;
        private Type mType;
        public CategoryHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.category_text);
            itemView.setOnClickListener(this);
        }
        public void bindCategoryItem(Type type) {
            mType = type;
            mTextView.setText(type.getCategoryName());
        }
        @Override
        public void onClick(View v) {
            //getActivity().startActivity(ListPlacesActivity.newIntent(getActivity(),mPosition));
        }
    }

    private class CategoryAdapter extends RecyclerView.Adapter<CategoryHolder> {
        private List<Type> mTypes;
        public CategoryAdapter(List<Type> types) {
            mTypes = types;
        }
        @Override
        public CategoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View v = inflater.inflate(R.layout.list_categories,parent,false);
            return new CategoryHolder(v);
        }

        @Override
        public void onBindViewHolder(CategoryHolder holder, int position) {
            Type type = mTypes.get(position);
            holder.bindCategoryItem(type);
        }

        @Override
        public int getItemCount() {
            return mTypes.size();
        }
    }


}
