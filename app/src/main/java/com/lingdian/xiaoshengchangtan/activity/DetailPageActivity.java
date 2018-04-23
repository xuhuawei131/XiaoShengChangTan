package com.lingdian.xiaoshengchangtan.activity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lingdian.xiaoshengchangtan.R;
import com.lingdian.xiaoshengchangtan.bean.FileBean;
import com.lingdian.xiaoshengchangtan.bean.TimerBean;
import com.lingdian.xiaoshengchangtan.config.SingleCacheData;
import com.lingdian.xiaoshengchangtan.customview.MyMenuDialog;
import com.lingdian.xiaoshengchangtan.db.tables.PageInfoDbBean;
import com.lingdian.xiaoshengchangtan.enums.TimerType;
import com.lingdian.xiaoshengchangtan.services.MyPlayerService;
import com.lingdian.xiaoshengchangtan.utils.DateUtils;
import com.lingdian.xiaoshengchangtan.utils.HtmlParer;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.io.File;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.lingdian.xiaoshengchangtan.config.EventBusTag.ACTION_ALARM_TIMER_UI_UPDATE;
import static com.lingdian.xiaoshengchangtan.config.EventBusTag.ACTION_VIEWHOLDER_TIMER;
import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_PLAY_UI_BUFFER;
import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_PLAY_UI_COMPLETION;
import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_PLAY_UI_ERROR;
import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_PLAY_SERVICE_PAUSE_OR_START;
import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_PLAY_UI_PREPARE;
import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_PLAY_UI_PROGRESS;
import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_PLAY_SERVICE_SEEK;
import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_PLAY_UI_SEEK_COMPLETION;
import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_PLAY_UI_STARTOR_PAUSE;
import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_PLAY_UI_START_NEW_MUSIC;

/**
 * 播放的详情页面
 */
public class DetailPageActivity extends BaseActivity {

    private PageInfoDbBean bean;
    private TextView text_title;
    private SeekBar mSeekBar;

    private TextView text_currentTime;
    private TextView text_totalTime;

    private FileBean fileBean;
    private String url;

    private ImageView ivLast;
    private ImageView ivNext;
    private ImageView ivPlayOrPause;
    private TextView text_timer;
    private View btn_list;
    private View btn_menu;
    private MyMenuDialog dialog;
    private boolean isDowned = false;

    @Override
    protected void init() {

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
        text_timer = (TextView) findViewById(R.id.text_timer);
        text_currentTime = (TextView) findViewById(R.id.text_currentTime);
        text_totalTime = (TextView) findViewById(R.id.text_totalTime);

        View btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(onClickListener);

        View btn_share = findViewById(R.id.btn_share);
        btn_share.setOnClickListener(onClickListener);

        btn_list = findViewById(R.id.btn_list);
        btn_menu = findViewById(R.id.btn_menu);
        btn_list.setOnClickListener(onClickListener);
        btn_menu.setOnClickListener(onClickListener);

        mSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
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


    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ivPlayOrPause:
                    EventBus.getDefault().post("", TAG_PLAY_SERVICE_PAUSE_OR_START);
                    break;
                case R.id.ivNext:
                    MyPlayerService.startPlayNext();
                    break;
                case R.id.ivLast:
                    MyPlayerService.startPlayLast();
                    break;
                case R.id.btn_list:
                    Intent intent = new Intent(DetailPageActivity.this, HomePageActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.btn_menu:
                    if (dialog == null) {
                        dialog = new MyMenuDialog(DetailPageActivity.this);
                        dialog.setOnDialogItemClick(dialogItemClick);
                    }
                    dialog.showDialog(isDowned);
                    break;
                case R.id.btn_back:
                    finish();
                    break;
                case R.id.btn_share:

                    break;

            }
        }
    };


    /**
     * 播放下一个音频
     * @param bean
     */
    @Subscriber(tag = TAG_PLAY_UI_START_NEW_MUSIC)
    private void onPlayNewMusic(PageInfoDbBean bean) {
        setData();
    }

    /**
     * 定时
     * @param leftTimer
     */
    @Subscriber(tag = ACTION_ALARM_TIMER_UI_UPDATE)
    private void onAlarmTimerUpate(int leftTimer) {
        if (leftTimer <= 0) {
            text_timer.setVisibility(View.GONE);
        } else {
            text_timer.setVisibility(View.VISIBLE);
            text_timer.setText(DateUtils.duration2TimeBySecond(leftTimer));
        }
    }

    /**
     * 暂停状态
     * @param isStart
     */
    @Subscriber(tag = TAG_PLAY_UI_STARTOR_PAUSE)
    private void onUIPauseOrStart(boolean isStart) {
        if (isStart) {
            ivPlayOrPause.setImageResource(R.drawable.ic_play);
        } else {
            ivPlayOrPause.setImageResource(R.drawable.ic_pause);
        }
    }

    /**
     * 更新进度
     * @param currentPosition
     */
    @Subscriber(tag = TAG_PLAY_UI_PROGRESS)
    private void onUpdateProgress(int currentPosition) {
        mSeekBar.setProgress(currentPosition);
        text_currentTime.setText(DateUtils.duration2TimeByMicSecond(currentPosition));
    }

    /**
     * 播放错误
     * @param bean
     */
    @Subscriber(tag = TAG_PLAY_UI_ERROR)
    private void onError(PageInfoDbBean bean) {
        mSeekBar.setMax(bean.totalTime);
        text_totalTime.setText(DateUtils.duration2TimeByMicSecond(bean.totalTime));
    }

    /**
     * 开始缓存
     * @param bean
     */
    @Subscriber(tag = TAG_PLAY_UI_BUFFER)
    private void onBufferingUpdate(PageInfoDbBean bean) {
        mSeekBar.setMax(bean.totalTime);
        text_totalTime.setText(DateUtils.duration2TimeByMicSecond(bean.totalTime));
    }

    /**
     * 拖动完成
     * @param bean
     */
    @Subscriber(tag = TAG_PLAY_UI_SEEK_COMPLETION)
    private void onSeekComplete(PageInfoDbBean bean) {
        mSeekBar.setMax(bean.totalTime);
        text_totalTime.setText(DateUtils.duration2TimeByMicSecond(bean.totalTime));
    }

    /**
     * 播放完成
     * @param bean
     */
    @Subscriber(tag = TAG_PLAY_UI_COMPLETION)
    private void onCompletion(PageInfoDbBean bean) {
        mSeekBar.setMax(bean.totalTime);
        text_totalTime.setText(DateUtils.duration2TimeByMicSecond(bean.totalTime));
    }

    /**
     * 播放准备好了
     * @param bean
     */
    @Subscriber(tag = TAG_PLAY_UI_PREPARE)
    private void onPrepared(PageInfoDbBean bean) {
//        MyPlayerService.startTimer(TimerType.TIMER_30);
        mSeekBar.setMax(bean.totalTime);
        text_totalTime.setText(DateUtils.duration2TimeByMicSecond(bean.totalTime));
    }

    /**
     * 选择定时器了
     * @param bean
     */
    @Subscriber(tag = ACTION_VIEWHOLDER_TIMER)
    private void onSetTimer(TimerBean bean) {
        if (bean.timerType == TimerType.TIMER_CANCEL) {
            text_timer.setVisibility(View.GONE);
        } else {
            text_timer.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 更新信息
     */
    private void setData() {
        bean = SingleCacheData.getInstance().getCurrentPlayBean();
        if (bean!=null){
            fileBean = FileBean.newInstance(bean.title);
            if (fileBean == null) {
                finish();
            } else {
                if (new File(fileBean.filePath).exists()) {
                    url = fileBean.filePath;
                    isDowned = true;
                } else {
                    url = fileBean.fileUrl;
                    isDowned = false;
                }
            }
            text_title.setText(fileBean.fileName);
        }
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

    private MyMenuDialog.OnDialogItemClick dialogItemClick = new MyMenuDialog.OnDialogItemClick() {
        @Override
        public void onDialogItem(int index) {
            if (index == 2) {
                startActivity(new Intent(DetailPageActivity.this, SelectTimerActivity.class));
            } else {
                Toast.makeText(DetailPageActivity.this, "暂不支持！", Toast.LENGTH_SHORT).show();
            }
        }
    };


}
