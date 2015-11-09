package com.favesolution.jktotw.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.favesolution.jktotw.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Daniel on 11/8/2015 for JktOtw project.
 */
public class DialogMessage extends DialogFragment{
    private static final String ARGS_MESSAGE = "args_message";
    public static final String EXTRA_CONFIRM = "extra_confirm";
    private String mMessage;
    @Bind(R.id.button_ok) Button mButtonOk;
    @Bind(R.id.text_message) TextView mTextMessage;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mMessage = getArguments().getString(ARGS_MESSAGE);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_message,null);
        ButterKnife.bind(this, v);
        mTextMessage.setText(mMessage);
        mButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();sendResult(Activity.RESULT_OK,0);
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(v);
        return builder.create();
    }
    public static DialogMessage newInstance(String message) {
        Bundle args = new Bundle();
        args.putString(ARGS_MESSAGE, message);
        DialogMessage dialogMessage = new DialogMessage();
        dialogMessage.setArguments(args);
        return dialogMessage;
    }
    private void sendResult(int resultCode,int confirm) {
        if (getTargetFragment() == null)
            return;
        Intent i = new Intent();
        i.putExtra(EXTRA_CONFIRM, confirm);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
    }
}
