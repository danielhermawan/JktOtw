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
public class DialogConfirmation extends DialogFragment {
    private static final String ARGS_MESSAGE = "args_message";
    public static final String EXTRA_CONFIRM = "extra_confirm";
    private String mMessage;
    @Bind(R.id.button_yes) Button mButtonYes;
    @Bind(R.id.button_no) Button mButtonNo;
    @Bind(R.id.text_message) TextView mTextMessage;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mMessage = getArguments().getString(ARGS_MESSAGE);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_confirmation,null);
        ButterKnife.bind(this, v);
        mTextMessage.setText(mMessage);
        mButtonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendResult(Activity.RESULT_OK, 1);
            }
        });
        mButtonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendResult(Activity.RESULT_OK,0);
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(v);
                /*.setMessage(mMessage)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_OK,1);
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_OK,0);
                    }
                });*/
        return builder.create();
    }
    public static DialogConfirmation newInstance(String message) {
        Bundle args = new Bundle();
        args.putString(ARGS_MESSAGE, message);
        DialogConfirmation dialogConfirmation = new DialogConfirmation();
        dialogConfirmation.setArguments(args);
        return dialogConfirmation;
    }
    private void sendResult(int resultCode,int confirm) {
        if (getTargetFragment() == null)
            return;
        Intent i = new Intent();
        i.putExtra(EXTRA_CONFIRM, confirm);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
        dismiss();
    }
}
