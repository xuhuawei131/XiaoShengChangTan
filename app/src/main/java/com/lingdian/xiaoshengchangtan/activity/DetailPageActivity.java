package com.lingdian.xiaoshengchangtan.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lingdian.xiaoshengchangtan.R;
import com.lingdian.xiaoshengchangtan.bean.FileBean;
import com.lingdian.xiaoshengchangtan.db.tables.DownLoadDbBean;
import com.lingdian.xiaoshengchangtan.config.SwitchConfig;
import com.lingdian.xiaoshengchangtan.enums.TimerType;
import com.lingdian.xiaoshengchangtan.services.MyPlayerService;
import com.lingdian.xiaoshengchangtan.services.TimerService;
import com.lingdian.xiaoshengchangtan.utils.DateUtils;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.io.File;

import static com.lingdian.xiaoshengchangtan.config.EventBusTag.ACTION_ALARM_TIMER_PROGRESS;
import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_PLAY_UI_BUFFER;
import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_PLAY_UI_COMPLETION;
import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_PLAY_UI_ERROR;
import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_PLAY_SERVICE_PAUSE_OR_START;
import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_PLAY_UI_PREPARE;
import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_PLAY_UI_PROGRESS;
import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_PLAY_SERVICE_SEEK;
import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_PLAY_UI_SEEK_COMPLETION;
import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_PLAY_UI_STARTOR_PAUSE;

public class DetailPageActivity extends BaseActivity {

    private DownLoadDbBean bean;
    private TextView text_title;
    private TextView text_date;
    private TextView text_status;
    private SeekBar mSeekBar;


    private TextView text_currentTime;
    private TextView text_totalTime;

    private FileBean fileBean;
    private String url;

    private ImageView ivLast;
    private ImageView ivNext;
    private ImageView ivPlayOrPause;
    private boolean isContinue=false;
    private TextView text_timer;
    private View btn_list;
    private View btn_menu;
    @Override
    protected void init() {
        bean = (DownLoadDbBean) getIntent().getSerializableExtra("bean");

        if (bean == null) {
            Toast.makeText(this, "bean not empty", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        fileBean = FileBean.checkData(bean.title);

        if (fileBean == null) {
            finish();
        } else {
            if (new File(fileBean.filePath).exists()) {
                url = fileBean.filePath;
            } else {
                url = fileBean.fileUrl;
            }
        }
    }

    @Override
    protected int setContentView() {
        return R.layout.activity_detail_page;
    }

    @Override
    protected void findViewByIds() {
        ivLast = (ImageView) findViewById(R.id.ivLast);
        ivPlayOrPause = (ImageView) findViewById(R.id.ivPlayOrPause);
        ivNext = (ImageView) findViewById(R.id.ivNext);

        ivLast.setOnClickListener(onClickListener);
        ivPlayOrPause.setOnClickListener(onClickListener);
        ivNext.setOnClickListener(onClickListener);

        mSeekBar = (SeekBar) findViewById(R.id.musicSeekBar);
        text_title = (TextView) findViewById(R.id.text_title);
        text_date = (TextView) findViewById(R.id.text_date);
        text_status = (TextView) findViewById(R.id.text_status);
        text_timer=(TextView)findViewById(R.id.text_timer);
        text_currentTime = (TextView) findViewById(R.id.text_currentTime);
        text_totalTime = (TextView) findViewById(R.id.text_totalTime);

        btn_list=findViewById(R.id.btn_list);
        btn_menu=findViewById(R.id.btn_menu);
        btn_list.setOnClickListener(onClickListener);
        btn_menu.setOnClickListener(onClickListener);

        mSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
    }

    @Override
    protected void requestService() {
        EventBus.getDefault().register(this);

        text_title.setText(fileBean.fileName);
        text_date.setText(bean.date);

        StringBuilder sb = new StringBuilder();
        sb.append("url:")
                .append(SwitchConfig.URL_HOME).append(bean.link)
                .append("\n")
                .append("fileName:").append(fileBean.fileName)
                .append("\n")
                .append("filePath:").append(fileBean.filePath)
                .append("\n")
                .append("fileUrl:").append(fileBean.fileUrl);
        text_status.setText(sb.toString());

        MyPlayerService.startPlay(bean);
    }

    @Override
    protected void onMyDestory() {
        EventBus.getDefault().unregister(this);

    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.ivPlayOrPause:
                    EventBus.getDefault().post("",TAG_PLAY_SERVICE_PAUSE_OR_START);
                    break;
                case R.id.ivNext:

                    break;
                case R.id.ivLast:

                    break;
                case R.id.btn_list:

                    break;
                case R.id.btn_menu:

                    break;
            }
        }
    };

    @Subscriber(tag = ACTION_ALARM_TIMER_PROGRESS)
    private void onAlarmTimerUpate(int leftTimer){
        text_timer.setText(DateUtils.duration2TimeBySecond(leftTimer));
    }

    @Subscriber(tag=TAG_PLAY_UI_STARTOR_PAUSE)
    private void onUIPauseOrStart(boolean isStart){
        if(isStart){
            ivPlayOrPause.setImageResource(R.drawable.ic_play);
        }else{
            ivPlayOrPause.setImageResource(R.drawable.ic_pause);
        }
    }
    @Subscriber(tag= TAG_PLAY_UI_PROGRESS)
    private void onUpdateProgress(int currentPosition){
        mSeekBar.setProgress(currentPosition);
        text_currentTime.setText(DateUtils.duration2TimeByMicSecond(currentPosition));
    }
    @Subscriber(tag= TAG_PLAY_UI_ERROR)
    private void onError(DownLoadDbBean bean){
        mSeekBar.setMax(bean.totalTime);
        text_totalTime.setText(DateUtils.duration2TimeByMicSecond(bean.totalTime));
    }
    @Subscriber(tag= TAG_PLAY_UI_BUFFER)
    private void onBufferingUpdate(DownLoadDbBean bean){
        mSeekBar.setMax(bean.totalTime);
        text_totalTime.setText(DateUtils.duration2TimeByMicSecond(bean.totalTime));
    }
    @Subscriber(tag= TAG_PLAY_UI_SEEK_COMPLETION)
    private void onSeekComplete(DownLoadDbBean bean){
        mSeekBar.setMax(bean.totalTime);
        text_totalTime.setText(DateUtils.duration2TimeByMicSecond(bean.totalTime));
    }

    @Subscriber(tag= TAG_PLAY_UI_COMPLETION)
    private void onCompletion(DownLoadDbBean bean){
        mSeekBar.setMax(bean.totalTime);
        text_totalTime.setText(DateUtils.duration2TimeByMicSecond(bean.totalTime));
    }

    @Subscriber(tag= TAG_PLAY_UI_PREPARE)
    private void onPrepared(DownLoadDbBean bean){
        TimerService.startTimer(TimerType.TIMER_TEST);
        mSeekBar.setMax(bean.totalTime);
        text_totalTime.setText(DateUtils.duration2TimeByMicSecond(bean.totalTime));
    }


    /**
     * 拖动进度条的回调
     */
    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            text_currentTime.setText(DateUtils.duration2TimeByMicSecond(progress));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            EventBus.getDefault().post(seekBar.getProgress(), TAG_PLAY_SERVICE_SEEK);
        }
    };


}
