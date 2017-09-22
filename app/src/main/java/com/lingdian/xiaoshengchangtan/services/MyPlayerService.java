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

import com.lingdian.xiaoshengchangtan.MyApp;
import com.lingdian.xiaoshengchangtan.activity.MainLockActivity;
import com.lingdian.xiaoshengchangtan.config.SingleData;
import com.lingdian.xiaoshengchangtan.config.SwitchConfig;
import com.lingdian.xiaoshengchangtan.db.impls.DownLoadImple;
import com.lingdian.xiaoshengchangtan.db.tables.DownLoadDbBean;
import com.lingdian.xiaoshengchangtan.enums.TimerType;
import com.lingdian.xiaoshengchangtan.player.MyPlayerApi;

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


    public static void startTimer(TimerType timerType) {
        SingleData.getInstance().setCurrentTimerType(timerType);
        Context context = MyApp.application;
        Intent intent = new Intent(context, MyPlayerService.class);
        intent.setAction(SERVICE_ACTION_TIMER_ADD);
        context.startService(intent);
    }


    /**
     * 播放音频
     *
     * @param bean
     */
    public static void startPlay(DownLoadDbBean bean) {
        Context context = MyApp.application;
        Intent intent = new Intent(context, MyPlayerService.class);
        intent.setAction(SERVICE_ACTION_PLAYER);
        intent.putExtra("bean", bean);
        context.startService(intent);
    }

    public static void startPlayNext(){
        DownLoadDbBean bean=SingleData.getInstance().getNextMusic();
        startPlay(bean);
    }

    public static void startPlayLast(){
        DownLoadDbBean bean=SingleData.getInstance().getLastMusic();
        startPlay(bean);
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();

        MyPlayerApi.getInstance().addMediaPlayerListener(listener);

        EventBus.getDefault().register(this);


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
                    DownLoadDbBean bean = (DownLoadDbBean) intent.getSerializableExtra("bean");
                    if (bean != null) {
                        SingleData.getInstance().playNewMusic(bean);
                    }
                    //添加新的定时器
                } else if (SERVICE_ACTION_TIMER_ADD.equals(action)) {
                    TimerType currentTimerType = SingleData.getInstance().getCurrentTimerType();
                    if (currentTimerType != null) {
                        stopAlarm();
                        if(currentTimerType ==TimerType.TIMER_END){
                            DownLoadDbBean currentBean=SingleData.getInstance().getDownLoadDbBean();
                            if(currentBean!=null){
                                startAlarm(currentBean.totalTime);
                            }
                        }else if(currentTimerType ==TimerType.TIMER_CANCEL){
                            totalSeconds=0;
                            EventBus.getDefault().post(0, ACTION_ALARM_TIMER_UI_UPDATE);
                        }else{
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

    @Override
    public void onDestroy() {
        super.onDestroy();

        SingleData.getInstance().clearCurrentList();


        stopAlarm();

        DownLoadDbBean currentBean=SingleData.getInstance().getDownLoadDbBean();
        DownLoadImple.getInstance().updateDownloadPlayerStatus(currentBean);

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

        DownLoadDbBean currentBean=SingleData.getInstance().getDownLoadDbBean();
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
        public void onCompletion(MediaPlayer mediaPlayer, DownLoadDbBean bean) {
            EventBus.getDefault().post(bean, TAG_PLAY_UI_COMPLETION);

            if (SingleData.getInstance().getCurrentTimerType() ==TimerType.TIMER_END) {
                LocalBroadcastManager.getInstance(MyPlayerService.this).sendBroadcast(new Intent(ACTION_EXIT_ALL_LIFE));
            } else {//播放完 下一个
                MyPlayerService.startPlayNext();
            }
        }

        @Override
        public void onBufferingUpdate(MediaPlayer mediaPlayer, int percent, DownLoadDbBean bean) {
            bean.buffet_percent = percent;
            EventBus.getDefault().post(bean, TAG_PLAY_UI_BUFFER);
        }

        @Override
        public boolean onError(MediaPlayer mediaPlayer, int i, int i1, DownLoadDbBean bean) {

            DownLoadDbBean currentBean=SingleData.getInstance().getDownLoadDbBean();
            currentBean.isPlaying = false;

            EventBus.getDefault().post(bean, TAG_PLAY_UI_ERROR);
            return false;
        }

        @Override
        public void onSeekComplete(MediaPlayer mediaPlayer, DownLoadDbBean bean) {
            EventBus.getDefault().post(bean, TAG_PLAY_UI_SEEK_COMPLETION);
        }

        @Override
        public void onPrepared(MediaPlayer mediaPlayer, DownLoadDbBean bean) {
            int during = mediaPlayer.getDuration();

            DownLoadDbBean currentBean=SingleData.getInstance().getDownLoadDbBean();
            currentBean.totalTime = during;
            currentBean.isPlaying = true;

            if (SwitchConfig.isSkipHead&& currentBean.currentTime==0) {
                MyPlayerApi.getInstance().seekTo(SwitchConfig.SkipHeadTime);
            }else{
                MyPlayerApi.getInstance().seekTo(currentBean.currentTime);
            }

            DownLoadImple.getInstance().updateDownloadDuring(currentBean);

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
