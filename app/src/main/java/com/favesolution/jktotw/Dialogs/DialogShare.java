package com.favesolution.jktotw.Dialogs;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.favesolution.jktotw.Adapters.ShareAdapter;
import com.favesolution.jktotw.Helpers.DividerItemDecoration;
import com.favesolution.jktotw.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Daniel on 11/8/2015 for JktOtw project.
 */
public class DialogShare extends DialogFragment{
    @Bind(R.id.recyclerview_share) RecyclerView mRecyclerView;
    private static final String ARGS_MESSAGE = "args_message";
    private static final String ARGS_URI_IMAGE = "args_uri_message";
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String mMessage = getArguments().getString(ARGS_MESSAGE);
        Uri uriImage = Uri.parse(getArguments().getString(ARGS_URI_IMAGE));
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_share, null);
        ButterKnife.bind(this, v);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, mMessage);
        sendIntent.putExtra(Intent.EXTRA_STREAM,uriImage);
        sendIntent.setType("image/*");
        PackageManager pm = getActivity().getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(sendIntent, 0);
        mRecyclerView.setAdapter(new ShareAdapter(getActivity(),activities,mMessage,uriImage));
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(v);
        return builder.create();
    }

    public static DialogShare newInstance(String message,Uri uriImage) {
        Bundle args = new Bundle();
        args.putString(ARGS_MESSAGE,message);
        args.putString(ARGS_URI_IMAGE,uriImage.toString());
        DialogShare fragment = new DialogShare();
        fragment.setArguments(args);
        return fragment;
    }
}
