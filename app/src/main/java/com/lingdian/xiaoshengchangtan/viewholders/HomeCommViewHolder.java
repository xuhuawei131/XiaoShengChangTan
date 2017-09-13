package com.lingdian.xiaoshengchangtan.viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lingdian.xiaoshengchangtan.R;
import com.lingdian.xiaoshengchangtan.bean.PageBean;

import org.simple.eventbus.EventBus;

/**
 * Created by lingdian on 17/9/11.
 */

public class HomeCommViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


    private PageBean bean;
    private TextView text_title;
    private TextView text_date;
    private ImageView image_down;
    private static final int layoutId= R.layout.adapter_home;
    public HomeCommViewHolder(View itemView) {
        super(itemView);
        text_title=(TextView) itemView.findViewById(R.id.text_title);
        text_date=(TextView) itemView.findViewById(R.id.text_date);
        image_down=(ImageView)itemView.findViewById(R.id.image_down);
        image_down.setOnClickListener(this);
        itemView.setOnClickListener(this);
    }

    public static View createView(Context context,ViewGroup parent){
        LayoutInflater inflater=LayoutInflater.from(context);
        return inflater.inflate(layoutId,parent,false);
    }
    public void setData(PageBean bean){
        this.bean=bean;
        text_title.setText(bean.title);
        text_date.setText(bean.date);
    }

    @Override
    public void onClick(View v) {

        if (v==image_down){
            EventBus.getDefault().post(bean,"downtag");
        }else{
            EventBus.getDefault().post(bean,"hometag");
        }

    }
}
