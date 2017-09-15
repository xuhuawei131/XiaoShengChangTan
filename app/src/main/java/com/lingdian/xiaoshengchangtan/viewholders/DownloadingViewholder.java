package com.lingdian.xiaoshengchangtan.viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.lingdian.xiaoshengchangtan.R;
import com.lingdian.xiaoshengchangtan.config.SwitchConfig;
import com.lingdian.xiaoshengchangtan.db.bean.DownLoadDbBean;

/**
 * Created by lingdian on 17/9/15.
 */

public class DownloadingViewholder extends ViewHolder implements View.OnClickListener {

    private static final int layoutId= R.layout.adapter_downloading;
    private DownLoadDbBean bean;

    private TextView text_title;
    private TextView text_date;
    private TextView text_down;
    private TextView text_percent;
    private Button btn_pause;
    private Button btn_delete;
    public DownloadingViewholder(View itemView) {
        super(itemView);
        findViewByIds();
    }


    private void findViewByIds(){
        text_title=(TextView) itemView.findViewById(R.id.text_title);
        text_date=(TextView) itemView.findViewById(R.id.text_date);
        text_down =(TextView)itemView.findViewById(R.id.btn_down);
        text_percent =(TextView)itemView.findViewById(R.id.text_percent);

        btn_pause =(Button) itemView.findViewById(R.id.btn_pause);
        btn_delete =(Button) itemView.findViewById(R.id.btn_delete);


        btn_pause.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
    }

    public void setData(DownLoadDbBean bean){
        this.bean=bean;

        text_title.setText(bean.title);
        text_date.setText(bean.date);
        text_down.setText(SwitchConfig.getDownStatusStr(bean.downStatus));
        text_percent.setText(((int)bean.percent*100)+"%");
    }

    public static DownloadingViewholder getViewHolder(ViewGroup parent){
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view= inflater.inflate(layoutId,parent,false);
        return new DownloadingViewholder(view);
    }

    @Override
    public void onClick(View v) {
        if(v==btn_delete){

        }else if(v==btn_pause){

        }
    }
}
