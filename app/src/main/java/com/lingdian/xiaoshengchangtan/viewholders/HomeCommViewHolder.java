package com.lingdian.xiaoshengchangtan.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lingdian.xiaoshengchangtan.R;
import com.lingdian.xiaoshengchangtan.config.SwitchConfig;
import com.lingdian.xiaoshengchangtan.db.tables.PageInfoDbBean;

import org.simple.eventbus.EventBus;

import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_DOWNLOADING_ADD;
import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_HOME_ITEM_CLICK;
import static com.lingdian.xiaoshengchangtan.config.SwitchConfig.DOWNLOAD_STATUS_NO;

/**
 * Created by lingdian on 17/9/11.
 */

public class HomeCommViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private PageInfoDbBean bean;
    private TextView text_title;
    private TextView text_date;
    private TextView text_down;
    private ImageView image_down;
    private static final int layoutId= R.layout.adapter_home;
    public HomeCommViewHolder(View itemView) {
        super(itemView);
        text_title=(TextView) itemView.findViewById(R.id.text_title);
        text_date=(TextView) itemView.findViewById(R.id.text_date);

        text_down =(TextView)itemView.findViewById(R.id.btn_down);

        image_down =(ImageView)itemView.findViewById(R.id.image_down);

        image_down.setOnClickListener(this);
        itemView.setOnClickListener(this);
    }

    public static View createView(ViewGroup parent){
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        return inflater.inflate(layoutId,parent,false);
    }
    public void setData(PageInfoDbBean bean){
        this.bean=bean;
        text_title.setText(bean.title);
        text_date.setText(bean.date);

        if(bean.downStatus==DOWNLOAD_STATUS_NO){
            image_down.setEnabled(true);
        }else{
            image_down.setEnabled(false);
        }
        text_down.setText(SwitchConfig.getDownStatusStr(bean.downStatus));
    }

    @Override
    public void onClick(View v) {
        if (v== image_down){
            EventBus.getDefault().post(bean,TAG_DOWNLOADING_ADD);
        }else{
            EventBus.getDefault().post(bean,TAG_HOME_ITEM_CLICK);
        }

    }
}
