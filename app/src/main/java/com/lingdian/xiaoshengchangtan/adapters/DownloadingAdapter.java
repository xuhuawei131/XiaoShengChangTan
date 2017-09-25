package com.lingdian.xiaoshengchangtan.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.lingdian.xiaoshengchangtan.db.tables.PageInfoDbBean;
import com.lingdian.xiaoshengchangtan.viewholders.DownloadingViewholder;

import java.util.List;

/**
 * Created by lingdian on 17/9/15.
 */

public class DownloadingAdapter extends RecyclerView.Adapter<DownloadingViewholder> {
    private List<PageInfoDbBean>  arrayList;
    public DownloadingAdapter(List<PageInfoDbBean>  arrayList){
        this.arrayList=arrayList;
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
