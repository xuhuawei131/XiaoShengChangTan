package com.lingdian.xiaoshengchangtan.services;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.lingdian.xiaoshengchangtan.activity.MainLockActivity;
import com.lingdian.xiaoshengchangtan.config.SingleCacheData;
import com.lingdian.xiaoshengchangtan.config.SwitchConfig;
import com.lingdian.xiaoshengchangtan.db.impls.DownloadInfoImple;
import com.lingdian.xiaoshengchangtan.db.impls.PageInfoImple;
import com.lingdian.xiaoshengchangtan.db.tables.PageInfoDbBean;
import com.lingdian.xiaoshengchangtan.enums.TimerType;
import com.lingdian.xiaoshengchangtan.player.MyPlayerApi;
import com.lingdian.xiaoshengchangtan.utils.HtmlParer;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.xhwbaselibrary.caches.MyAppContext;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import static com.lingdian.xiaoshengchangtan.config.EventBusTag.ACTION_ALARM_TIMER_UI_UPDATE;
import static com.lingdian.xiaoshengchangtan.config.EventBusTag.ACTION_EXIT_ALL_LIFE;
import static com.lingdian.xiaoshengchangtan.config.EventBusTag.SERVICE_ACTION_TIMER_ALARM;
import static com.lingdian.xiaoshengchangtan.config.EventBusTag.SERVICE_ACTION_PLAYER;
import static com.lingdian.xiaoshengchangtan.config.EventBusTag.SERVICE_ACTION_TIMER_ADD;
import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_PLAY_UI_BUFFER;
import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_PLAY_UI_COMPLETION;
import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_PLAY_UI_ERROR;
import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_PLAY_SERVICE_PAUSE_OR_START;
import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_PLAY_UI_PREPARE;
import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_PLAY_UI_PROGRESS;
import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_PLAY_SERVICE_SEEK;
import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_PLAY_UI_SEEK_COMPLETION;
import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_PLAY_UI_STARTOR_PAUSE;

/**
 * 播放的服务
 */
public class MyPlayerService extends Service {
    public static final int MUSIC_MESSAGE = 0;
    private int totalSeconds = 0;

    /**
     * 设置定时器
     *
     * @param timerType
     */
    public static void startTimer(TimerType timerType) {
        SingleCacheData.getInstance().setCurrentTimerType(timerType);
        Context context = MyAppContext.getInstance().getContext();
        Intent intent = new Intent(context, MyPlayerService.class);
        intent.setAction(SERVICE_ACTION_TIMER_ADD);
        context.startService(intent);
    }

    /**
     * 播放音频
     *
     * @param bean
     */
    public static void startPlay(PageInfoDbBean bean) {
        Context context = MyAppContext.getInstance().getContext();
        Intent intent = new Intent(context, MyPlayerService.class);
        intent.setAction(SERVICE_ACTION_PLAYER);
        intent.putExtra("bean", bean);
        context.startService(intent);
    }

    /**
     * 播放下一个
     */
    public static void startPlayNext() {
        PageInfoDbBean bean = SingleCacheData.getInstance().getNextMusic();
        startPlay(bean);
    }

    /**
     * 播放上一个
     */
    public static void startPlayLast() {
        PageInfoDbBean bean = SingleCacheData.getInstance().getLastMusic();
        startPlay(bean);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        MyPlayerApi.getInstance().addMediaPlayerListener(listener);

        EventBus.getDefault().register(this);

        //接受系统广播 监视屏幕是否锁屏
        IntentFilter lockIntentFilter = new IntentFilter();
        lockIntentFilter.addAction(Intent.ACTION_SCREEN_ON);
        lockIntentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(broadcastReceiver, lockIntentFilter);


        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_EXIT_ALL_LIFE);
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();

            if (!TextUtils.isEmpty(action)) {
                //播放音频
                if (SERVICE_ACTION_PLAYER.equals(action)) {
                    PageInfoDbBean bean = (PageInfoDbBean) intent.getSerializableExtra("bean");
                    if (bean != null) {
                        playItem(bean);
                    }
                    //添加新的定时器
                } else if (SERVICE_ACTION_TIMER_ADD.equals(action)) {
                    TimerType currentTimerType = SingleCacheData.getInstance().getCurrentTimerType();
                    if (currentTimerType != null) {
                        stopAlarm();
                        if (currentTimerType == TimerType.TIMER_END) {
                            PageInfoDbBean currentBean = SingleCacheData.getInstance().getCurrentPlayBean();
                            if (currentBean != null) {
                                startAlarm(currentBean.totalTime);
                            }
                        } else if (currentTimerType == TimerType.TIMER_CANCEL) {
                            totalSeconds = 0;
                            EventBus.getDefault().post(0, ACTION_ALARM_TIMER_UI_UPDATE);
                        } else {
                            startAlarm(currentTimerType.timer);
                        }
                    }
                    //更新闹钟
                } else if (SERVICE_ACTION_TIMER_ALARM.equals(action)) {
                    totalSeconds--;
                    EventBus.getDefault().post(totalSeconds, ACTION_ALARM_TIMER_UI_UPDATE);
                    if (totalSeconds <= 0) {
                        stopAlarm();
                        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(ACTION_EXIT_ALL_LIFE));
                    }
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }


    /**
     * 播放  * @param bean
     */
    public void playItem(PageInfoDbBean bean) {
        if (TextUtils.isEmpty(bean.fileUrl)) {
            PageInfoDbBean dbBean = PageInfoImple.getInstance().getPageItemInfo(bean.itemId);
            if (dbBean == null || TextUtils.isEmpty(dbBean.fileUrl)) {
                getPageDownFilePath(bean, "http://gb.jlradio.net/"+bean.link);
            } else {
                bean.fileUrl = dbBean.fileUrl;
                SingleCacheData.getInstance().playNewMusic(bean);
            }
        } else {
            SingleCacheData.getInstance().playNewMusic(bean);
        }
    }

    /**
     * 获取下载的路径
     *
     * @param bean
     * @param url
     */
    private void getPageDownFilePath(final PageInfoDbBean bean, String url) {

        OkGo.<String>post(url).tag(this).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                playAudio(response);
            }

            @Override
            public void onCacheSuccess(Response<String> response) {
                playAudio(response);
            }

            private void playAudio(Response<String> response) {
                String html = response.body();
                String fileUrl = HtmlParer.getPageDownFile(html);
                bean.fileUrl = fileUrl;
                PageInfoImple.getInstance().updateDownloadFileUrl(bean.itemId, fileUrl);
                SingleCacheData.getInstance().playNewMusic(bean);
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        SingleCacheData.getInstance().clearCurrentList();


        stopAlarm();

        PageInfoDbBean currentBean = SingleCacheData.getInstance().getCurrentPlayBean();
        PageInfoImple.getInstance().updateDownloadPlayerStatus(currentBean);

        MyPlayerApi.getInstance().removeMediaPlayerListener(listener);
        MyPlayerApi.getInstance().destory();

        EventBus.getDefault().unregister(this);

        stopUpdateSeekBarProgree();
        mMusicHandler = null;

        unregisterReceiver(broadcastReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }


    /***
     * 启动定时
     */
    private void startAlarm(int timer) {
        totalSeconds = timer * 60;
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), MyPlayerService.class);
        intent.setAction(SERVICE_ACTION_TIMER_ALARM);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // 立刻执行，此后5分钟走一次//SystemClock.elapsedRealtime()
        alarm.setRepeating(AlarmManager.ELAPSED_REALTIME, totalSeconds * 1000, 1000, pendingIntent);
    }

    /**
     * 关闭定时器
     */
    private void stopAlarm() {
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), MyPlayerService.class);
        intent.setAction(SERVICE_ACTION_TIMER_ALARM);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarm.cancel(pendingIntent);
    }


    @Subscriber(tag = TAG_PLAY_SERVICE_SEEK)
    private void onHandSeek(int position) {
        MyPlayerApi.getInstance().seekTo(position);
    }

    @Subscriber(tag = TAG_PLAY_SERVICE_PAUSE_OR_START)
    private void onHandPauseOrStart(String item) {
        MyPlayerApi.MusicStatus status = MyPlayerApi.getInstance().startOrPause();

        PageInfoDbBean currentBean = SingleCacheData.getInstance().getCurrentPlayBean();
        if (status == MyPlayerApi.MusicStatus.PAUSE) {
            currentBean.isPlaying = false;

            EventBus.getDefault().post(true, TAG_PLAY_UI_STARTOR_PAUSE);
        } else {
            currentBean.isPlaying = true;
            EventBus.getDefault().post(false, TAG_PLAY_UI_STARTOR_PAUSE);
        }
    }


    /**
     * 进度条定时器
     */
    private Handler mMusicHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int currentPosition = MyPlayerApi.getInstance().getCurrentPosition();
            startUpdateSeekBarProgress();
            EventBus.getDefault().post(currentPosition, TAG_PLAY_UI_PROGRESS);
        }
    };

    /**
     * 开始更新进度条
     */
    private void startUpdateSeekBarProgress() {
        /*避免重复发送Message*/
        stopUpdateSeekBarProgree();
        mMusicHandler.sendEmptyMessageDelayed(MUSIC_MESSAGE, 1000);
    }

    private void stopUpdateSeekBarProgree() {
        mMusicHandler.removeMessages(MUSIC_MESSAGE);
    }


    /**
     * 播放器监听
     */
    private MyPlayerApi.MediaPlayerCallBack listener = new MyPlayerApi.MediaPlayerCallBack() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer, PageInfoDbBean bean) {
            EventBus.getDefault().post(bean, TAG_PLAY_UI_COMPLETION);

            if (SingleCacheData.getInstance().getCurrentTimerType() == TimerType.TIMER_END) {
                LocalBroadcastManager.getInstance(MyPlayerService.this).sendBroadcast(new Intent(ACTION_EXIT_ALL_LIFE));
            } else {//播放完 下一个
                MyPlayerService.startPlayNext();
            }
        }

        @Override
        public void onBufferingUpdate(MediaPlayer mediaPlayer, int percent, PageInfoDbBean bean) {
            bean.buffet_percent = percent;
            EventBus.getDefault().post(bean, TAG_PLAY_UI_BUFFER);
        }

        @Override
        public boolean onError(MediaPlayer mediaPlayer, int i, int i1, PageInfoDbBean bean) {
            bean.isPlaying = false;
            EventBus.getDefault().post(bean, TAG_PLAY_UI_ERROR);
            return false;
        }

        @Override
        public void onSeekComplete(MediaPlayer mediaPlayer, PageInfoDbBean bean) {
            EventBus.getDefault().post(bean, TAG_PLAY_UI_SEEK_COMPLETION);
        }

        @Override
        public void onPrepared(MediaPlayer mediaPlayer, PageInfoDbBean bean) {
            int during = mediaPlayer.getDuration();
            //设置当前的数据 总共时间
//            PageInfoDbBean currentBean= SingleCacheData.getInstance().getCurrentPlayBean();
//            currentBean.totalTime = during;
//            currentBean.isPlaying = true;

            bean.totalTime = during;
            bean.isPlaying = true;

            //跳过广告头
            if (SwitchConfig.isSkipHead && bean.currentTime == 0) {
                MyPlayerApi.getInstance().seekTo(SwitchConfig.SkipHeadTime);
            } else {
                MyPlayerApi.getInstance().seekTo(bean.currentTime);
            }

            MyPlayerApi.getInstance().startPlay();

            EventBus.getDefault().post(bean, TAG_PLAY_UI_PREPARE);
            startUpdateSeekBarProgress();
        }
    };

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_EXIT_ALL_LIFE.equals(action)) {
                stopSelf();
            } else if (Intent.ACTION_SCREEN_ON.equals(action)) {

            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                Intent activityIntent = new Intent();
                activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activityIntent.setClass(context, MainLockActivity.class);
                context.startActivity(activityIntent);
            }
        }
    };
}
