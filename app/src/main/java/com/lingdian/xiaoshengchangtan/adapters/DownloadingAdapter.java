package com.lingdian.xiaoshengchangtan.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.lingdian.xiaoshengchangtan.cache.DownloadCache;
import com.lingdian.xiaoshengchangtan.db.bean.DownLoadDbBean;
import com.lingdian.xiaoshengchangtan.viewholders.DownloadingViewholder;

import java.util.List;

/**
 * Created by lingdian on 17/9/15.
 */

public class DownloadingAdapter extends RecyclerView.Adapter<DownloadingViewholder> {
    private List<DownLoadDbBean>  arrayList;
    public DownloadingAdapter(List<DownLoadDbBean>  arrayList){
    }

    @Override
    public DownloadingViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        DownloadingViewholder holder= DownloadingViewholder.getViewHolder(parent);
        return holder;
    }

    @Override
    public void onBindViewHolder(DownloadingViewholder holder, int position) {
        holder.setData(arrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
