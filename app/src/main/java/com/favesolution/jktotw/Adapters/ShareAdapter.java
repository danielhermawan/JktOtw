package com.favesolution.jktotw.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.favesolution.jktotw.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Daniel on 11/8/2015 for JktOtw project.
 */
public class ShareAdapter extends RecyclerView.Adapter<ShareAdapter.ShareHolder>  {
    private List<ResolveInfo> mInfos;
    private Context mContext;
    private String mMessage;
    private Uri mUriImage;
    public ShareAdapter(Context context,List<ResolveInfo> infos,String message,Uri uriImage) {
        mContext=context;
        mInfos = infos;
        mMessage = message;
        mUriImage = uriImage;
    }

    @Override
    public ShareHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.list_shares,parent,false);
        return new ShareHolder(v);
    }

    @Override
    public void onBindViewHolder(ShareHolder holder, int position) {
        ResolveInfo resolveInfo = mInfos.get(position);
        holder.bindShareItems(resolveInfo,mContext,mMessage,mUriImage);
    }

    @Override
    public int getItemCount() {
        return mInfos.size();
    }

    class ShareHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        @Bind(R.id.icon_share)ImageView mImageIcon;
        @Bind(R.id.text_name_share) TextView mTextName;
        private ResolveInfo mResolveInfo;
        private String mMessage;
        private Context mContext;
        private Uri mUriImage;
        public ShareHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }
        public void bindShareItems(ResolveInfo resolveInfo,Context context,String message,Uri uriImage) {
            mResolveInfo = resolveInfo;
            mMessage = message;
            mContext = context;
            mUriImage = uriImage;
            PackageManager pm = context.getPackageManager();
            String appName = resolveInfo.loadLabel(pm).toString();
            Drawable icon = resolveInfo.loadIcon(pm);
            mTextName.setText(appName);
            mImageIcon.setImageDrawable(icon);
        }

        @Override
        public void onClick(View v) {
            ActivityInfo activityInfo = mResolveInfo.activityInfo;
            Intent i = new Intent();
            i.setAction(Intent.ACTION_SEND);
            i.putExtra(Intent.EXTRA_TEXT, mMessage);
            i.putExtra(Intent.EXTRA_STREAM,mUriImage);
            i.setType("image/*");
            i.setClassName(activityInfo.applicationInfo.packageName,
                    activityInfo.name);
            mContext.startActivity(i);
        }
    }
}
