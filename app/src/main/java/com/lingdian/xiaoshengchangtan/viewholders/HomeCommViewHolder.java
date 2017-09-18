package com.lingdian.xiaoshengchangtan.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lingdian.xiaoshengchangtan.R;
import com.lingdian.xiaoshengchangtan.config.SwitchConfig;
import com.lingdian.xiaoshengchangtan.db.tables.DownLoadDbBean;

import org.simple.eventbus.EventBus;

/**
 * Created by lingdian on 17/9/11.
 */

public class HomeCommViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


    private DownLoadDbBean bean;
    private TextView text_title;
    private TextView text_date;
    private TextView text_down;
    private ImageView image_selected;
    private static final int layoutId= R.layout.adapter_home;
    public HomeCommViewHolder(View itemView) {
        super(itemView);
        text_title=(TextView) itemView.findViewById(R.id.text_title);
        text_date=(TextView) itemView.findViewById(R.id.text_date);

        text_down =(TextView)itemView.findViewById(R.id.btn_down);

        image_selected=(ImageView)itemView.findViewById(R.id.image_selected);


        image_selected.setOnClickListener(this);
        itemView.setOnClickListener(this);
    }

    public static View createView(ViewGroup parent){
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        return inflater.inflate(layoutId,parent,false);
    }
    public void setData(DownLoadDbBean bean){
        this.bean=bean;
        text_title.setText(bean.title);
        text_date.setText(bean.date);

        if(bean.isEditStatus){
            image_selected.setVisibility(View.VISIBLE);

            if(bean.isSelected){
                image_selected.setImageResource(R.drawable.icon_msg_toggle_true);
            }else{
                image_selected.setImageResource(R.drawable.icon_msg_toggle_false);
            }
        }else{
            image_selected.setVisibility(View.GONE);
        }

        text_down.setText(SwitchConfig.getDownStatusStr(bean.downStatus));
    }

    @Override
    public void onClick(View v) {
        if (v== image_selected){
            EventBus.getDefault().post(bean,"itemChange");
        }else{
            EventBus.getDefault().post(bean,"hometag");
        }

    }
}
