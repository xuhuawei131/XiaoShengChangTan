package com.lingdian.xiaoshengchangtan.viewholders;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lingdian.xiaoshengchangtan.R;
import com.lingdian.xiaoshengchangtan.config.SwitchConfig;
import com.lingdian.xiaoshengchangtan.db.tables.PageInfoDbBean;

import org.simple.eventbus.EventBus;

import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_DOWNLOADING_DELETE;

/**
 * Created by lingdian on 17/9/15.
 */

public class DownloadingViewholder extends ViewHolder implements View.OnClickListener {

    private static final int layoutId= R.layout.adapter_downloading;
    private PageInfoDbBean bean;

    private TextView text_title;
    private TextView text_date;
    private TextView text_down;
    private TextView text_percent;
    private Button btn_pause;
    private Button btn_delete;
    private int position;
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

    public void setData(PageInfoDbBean bean){
        this.bean=bean;

        text_title.setText(bean.title);
        text_date.setText(bean.date);
        text_down.setText(SwitchConfig.getDownStatusStr(bean.downStatus));
        text_percent.setText(((int)(bean.percent*100))+"%");
    }

    public static DownloadingViewholder getViewHolder(ViewGroup parent){
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view= inflater.inflate(layoutId,parent,false);
        return new DownloadingViewholder(view);
    }

    @Override
    public void onClick(View v) {
        if(v==btn_delete){
            EventBus.getDefault().post(bean,TAG_DOWNLOADING_DELETE);
        }else if(v==btn_pause){
            Toast.makeText(itemView.getContext(),"暂不支持暂停，后续开放",Toast.LENGTH_SHORT).show();
        }
    }
}
