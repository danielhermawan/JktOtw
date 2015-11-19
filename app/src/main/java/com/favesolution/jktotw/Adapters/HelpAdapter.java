package com.favesolution.jktotw.Adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.favesolution.jktotw.Dialogs.DialogMessage;
import com.favesolution.jktotw.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Daniel on 11/18/2015 for JktOtw project.
 */
public class HelpAdapter extends RecyclerView.Adapter<HelpAdapter.HelpHolder>{
    private TypedArray mQuestions;
    private TypedArray mAnswers;
    private AppCompatActivity mActivity;
    public HelpAdapter(Context context,AppCompatActivity activity) {
        mQuestions = context.getResources().obtainTypedArray(R.array.questions);
        mAnswers = context.getResources().obtainTypedArray(R.array.answers);
        mActivity =activity;
    }

    @Override
    public HelpHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.list_help,parent,false);
        return new HelpHolder(v,mActivity);
    }

    @Override
    public void onBindViewHolder(HelpHolder holder, int position) {
        String question = mQuestions.getString(position);
        String answer = mAnswers.getString(position);
        holder.bindItem(question,answer);
    }

    @Override
    public int getItemCount() {
        return mQuestions.length();
    }

    class HelpHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.question_text) TextView mTextQuestion;
        private String mAnswer;
        private String DIALOG_MESSAGE = "dialog_message";
        private AppCompatActivity mActivity;
        public HelpHolder(View itemView, AppCompatActivity activity) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mActivity = activity;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogMessage dialog = DialogMessage
                            .newInstance(mAnswer);
                    dialog.show(mActivity.getSupportFragmentManager(), DIALOG_MESSAGE);
                }
            });
        }
        public void bindItem(String question,String answer) {
            mAnswer = answer;
            mTextQuestion.setText(question);
        }
    }
}
