package com.lingdian.xiaoshengchangtan.activity;


import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.lingdian.xiaoshengchangtan.R;
import com.lingdian.xiaoshengchangtan.config.SingleCacheData;
import com.lingdian.xiaoshengchangtan.db.tables.PageInfoDbBean;
import com.lingdian.xiaoshengchangtan.services.MyPlayerService;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import me.imid.swipebacklayout.lib.SwipeBackLayout;

import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_PLAY_SERVICE_PAUSE_OR_START;
import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_PLAY_UI_ERROR;
import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_PLAY_UI_PREPARE;
import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_PLAY_UI_STARTOR_PAUSE;
import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_PLAY_UI_START_NEW_MUSIC;
import static me.imid.swipebacklayout.lib.SwipeBackLayout.EDGE_BOTTOM;

/**
 * 锁屏页面 上拉关闭
 */
public class MainLockActivity extends SwipeBackBaseActivity {
    private TextView text_title;
    private TextView btn_subTitle;
    private ImageView  ivLast;
    private ImageView  ivPlayOrPause;
    private ImageView  ivNext;
    private ImageView  image_lock;
    @Override
    protected void initSwiper() {
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
    }
    @Override
    protected int setContentView() {
        return R.layout.activity_main_lock;
    }

    @Override
    protected void findViewByIds() {
        SwipeBackLayout view=getSwipeBackLayout();
        view.setEdgeTrackingEnabled(EDGE_BOTTOM);

        text_title= (TextView) findViewById(R.id.text_title);
        btn_subTitle= (TextView) findViewById(R.id.btn_subTitle);

        ivLast= (ImageView) findViewById(R.id.ivLast);
        ivPlayOrPause= (ImageView) findViewById(R.id.ivPlayOrPause);
        ivNext= (ImageView) findViewById(R.id.ivNext);
        image_lock= (ImageView) findViewById(R.id.image_lock);

        ivLast.setOnClickListener(clickListener);
        ivPlayOrPause.setOnClickListener(clickListener);
        ivNext.setOnClickListener(clickListener);

    }

    @Override
    protected void requestService() {
        EventBus.getDefault().register(this);
        setData();
    }
    @Override
    protected void onMyDestory() {
        EventBus.getDefault().unregister(this);
    }

    @Subscriber(tag=TAG_PLAY_UI_START_NEW_MUSIC)
    private void onGetMusicInfo(PageInfoDbBean bean){
        setData();
    }


    @Subscriber(tag=TAG_PLAY_UI_PREPARE)
    private void onPlayerPrepare(PageInfoDbBean bean){
        setData();
    }

    @Subscriber(tag=TAG_PLAY_UI_STARTOR_PAUSE)
    private void onUIPauseOrStart(boolean isStart){
        if(isStart){
            ivPlayOrPause.setImageResource(R.drawable.ic_play);
        }else{
            ivPlayOrPause.setImageResource(R.drawable.ic_pause);
        }
    }

    @Subscriber(tag=TAG_PLAY_UI_ERROR)
    private void onUIError(PageInfoDbBean bean){
        if(bean.isPlaying){
            ivPlayOrPause.setImageResource(R.drawable.ic_play);
        }else{
            ivPlayOrPause.setImageResource(R.drawable.ic_pause);
        }
    }

    private void setData(){
        PageInfoDbBean bean= SingleCacheData.getInstance().getCurrentPlayBean();
        if(bean!=null){
            text_title.setText(bean.title);
            btn_subTitle.setText("晓声长谈电台版");
            if(bean.isPlaying){
                ivPlayOrPause.setImageResource(R.drawable.ic_pause);
            }else{
                ivPlayOrPause.setImageResource(R.drawable.ic_play);
            }
        }
    }
    private View.OnClickListener clickListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v==ivLast){
                MyPlayerService.startPlayLast();
            }else if(v==ivPlayOrPause){
                EventBus.getDefault().post("",TAG_PLAY_SERVICE_PAUSE_OR_START);
            }else if(v==ivNext){
                MyPlayerService.startPlayNext();
            }
        }
    };
}
