package com.lingdian.xiaoshengchangtan.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lingdian.xiaoshengchangtan.R;
import com.lingdian.xiaoshengchangtan.bean.TimerBean;

import org.simple.eventbus.EventBus;

import static com.lingdian.xiaoshengchangtan.config.EventBusTag.ACTION_VIEWHOLDER_TIMER;

/**
 * Created by lingdian on 17/9/22.
 */

public class TimerViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

    private TimerBean bean;
    private TextView textName;
    private ImageView image_select;
    public TimerViewHolder(View itemView) {
        super(itemView);
        textName= (TextView) itemView.findViewById(R.id.textName);
        image_select= (ImageView) itemView.findViewById(R.id.image_select);
        itemView.setOnClickListener(this);
    }

    public void setData(TimerBean bean){
        this.bean=bean;
        textName.setText(bean.name);
        if (bean.isSelect){
            image_select.setImageResource(R.drawable.icon_msg_toggle_true);
        }else{
            image_select.setImageResource(R.drawable.icon_msg_toggle_false);
        }
    }

    public static TimerViewHolder newInstance(ViewGroup parent){
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view= inflater.inflate(R.layout.adapter_timer,parent,false);
        return new TimerViewHolder(view);
    }

    @Override
    public void onClick(View v) {
        EventBus.getDefault().post(bean,ACTION_VIEWHOLDER_TIMER);
    }
}
