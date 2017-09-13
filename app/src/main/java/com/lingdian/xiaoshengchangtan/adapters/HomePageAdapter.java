package com.lingdian.xiaoshengchangtan.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.lingdian.xiaoshengchangtan.bean.PageBean;
import com.lingdian.xiaoshengchangtan.viewholders.HomeCommViewHolder;

import java.util.List;

/**
 * Created by lingdian on 17/9/11.
 */

public class HomePageAdapter extends RecyclerView.Adapter<HomeCommViewHolder> {

    private List<PageBean> arrayList;

    public HomePageAdapter(List<PageBean> arrayList){
        this.arrayList=arrayList;
    }

    @Override
    public HomeCommViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=HomeCommViewHolder.createView(parent.getContext(),parent);
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
