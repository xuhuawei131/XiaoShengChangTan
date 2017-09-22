package com.lingdian.xiaoshengchangtan.viewholders;

import android.content.Intent;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lingdian.xiaoshengchangtan.R;
import com.lingdian.xiaoshengchangtan.activity.DetailPageActivity;
import com.lingdian.xiaoshengchangtan.config.SwitchConfig;
import com.lingdian.xiaoshengchangtan.db.tables.DownLoadDbBean;

import org.simple.eventbus.EventBus;

import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_DOWNLOADING_DELETE;
import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_DOWNLOADING_ITEM_CLICK;

/**
 * Created by lingdian on 17/9/15.
 * 下载完成
 */

public class DownloadedViewholder extends ViewHolder implements View.OnClickListener {

    private static final int layoutId= R.layout.adapter_downloaded;
    private DownLoadDbBean bean;

    private TextView text_title;
    private TextView text_date;
    private TextView text_down;
    private Button btn_delete;
    public DownloadedViewholder(View itemView) {
        super(itemView);
        findViewByIds();
    }


    private void findViewByIds(){
        text_title=(TextView) itemView.findViewById(R.id.text_title);
        text_date=(TextView) itemView.findViewById(R.id.text_date);
        text_down =(TextView)itemView.findViewById(R.id.btn_down);

        btn_delete =(Button) itemView.findViewById(R.id.btn_delete);
        itemView.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
    }

    public void setData(DownLoadDbBean bean){
        this.bean=bean;

        text_title.setText(bean.title);
        text_date.setText(bean.date);
        text_down.setText(SwitchConfig.getDownStatusStr(bean.downStatus));
    }

    public static DownloadedViewholder getViewHolder(ViewGroup parent){
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view= inflater.inflate(layoutId,parent,false);
        return new DownloadedViewholder(view);
    }
    @Override
    public void onClick(View v) {
        if(v==btn_delete){
            EventBus.getDefault().post(bean,TAG_DOWNLOADING_DELETE);
        }else if(v==itemView){
            EventBus.getDefault().post(bean,TAG_DOWNLOADING_ITEM_CLICK);

        }
    }
}
