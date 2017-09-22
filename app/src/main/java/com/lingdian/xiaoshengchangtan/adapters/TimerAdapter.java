package com.lingdian.xiaoshengchangtan.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.lingdian.xiaoshengchangtan.bean.TimerBean;
import com.lingdian.xiaoshengchangtan.viewholders.TimerViewHolder;

import java.util.List;

/**
 * Created by lingdian on 17/9/22.
 */

public class TimerAdapter extends RecyclerView.Adapter<TimerViewHolder> {
    private List<TimerBean> arrayList;

    public TimerAdapter(List<TimerBean> arrayList){
        this.arrayList=arrayList;
    }

    @Override
    public TimerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return  TimerViewHolder.newInstance(parent);
    }

    @Override
    public void onBindViewHolder(TimerViewHolder holder, int position) {
        holder.setData(arrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
