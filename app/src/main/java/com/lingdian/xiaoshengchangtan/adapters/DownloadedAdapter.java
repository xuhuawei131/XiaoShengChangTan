package com.lingdian.xiaoshengchangtan.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.lingdian.xiaoshengchangtan.db.tables.PageInfoDbBean;
import com.lingdian.xiaoshengchangtan.viewholders.DownloadedViewholder;

import java.util.List;

/**
 * Created by lingdian on 17/9/15.
 * 下载完成的适配器
 */

public class DownloadedAdapter extends RecyclerView.Adapter<DownloadedViewholder> {
    private List<PageInfoDbBean>  arrayList;
    public DownloadedAdapter(List<PageInfoDbBean>  arrayList){
        this.arrayList=arrayList;
    }

    @Override
    public DownloadedViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        DownloadedViewholder holder= DownloadedViewholder.getViewHolder(parent);
        return holder;
    }

    @Override
    public void onBindViewHolder(DownloadedViewholder holder, int position) {
        holder.setData(arrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
