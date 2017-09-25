package com.lingdian.xiaoshengchangtan.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.lingdian.xiaoshengchangtan.db.tables.PageInfoDbBean;
import com.lingdian.xiaoshengchangtan.viewholders.HomeCommViewHolder;

import java.util.List;

/**
 * Created by lingdian on 17/9/11.
 */

public class HomePageAdapter extends RecyclerView.Adapter<HomeCommViewHolder> {

    private List<PageInfoDbBean> arrayList;

    public HomePageAdapter(List<PageInfoDbBean> arrayList){
        this.arrayList=arrayList;
    }

    @Override
    public HomeCommViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=HomeCommViewHolder.createView(parent);
        HomeCommViewHolder homeCommViewHolder=new HomeCommViewHolder(view);
        return homeCommViewHolder;
    }

    @Override
    public void onBindViewHolder(HomeCommViewHolder holder, int position) {
        holder.setData(arrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
