package com.lingdian.xiaoshengchangtan.services;


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

import com.lingdian.xiaoshengchangtan.MyApp;
import com.lingdian.xiaoshengchangtan.bean.FileBean;
import com.lingdian.xiaoshengchangtan.config.SwitchConfig;
import com.lingdian.xiaoshengchangtan.db.impls.DownLoadImple;
import com.lingdian.xiaoshengchangtan.db.tables.DownLoadDbBean;
import com.lingdian.xiaoshengchangtan.player.MyPlayerApi;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.io.File;

import static com.lingdian.xiaoshengchangtan.config.EventBusTag.ACTION_EXIT_ALL_LIFE;
import static com.lingdian.xiaoshengchangtan.config.EventBusTag.TAG_PLAY_SERVICE_TIMER;
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

    private String url;
    private DownLoadDbBean currentBean;
    public MyPlayerService() {

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

        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(ACTION_EXIT_ALL_LIFE);
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,intentFilter);
    }

    public static void startPlay(DownLoadDbBean bean) {
        Context context = MyApp.application;
        Intent intent = new Intent(context, MyPlayerService.class);
        intent.putExtra("bean", bean);
        context.startService(intent);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            DownLoadDbBean bean = (DownLoadDbBean) intent.getSerializableExtra("bean");
            if (bean != null) {
                if(currentBean!=null&&currentBean.title.equals(bean.title)){

                }else{
                    currentBean=bean;
                    FileBean fileBean = FileBean.checkData(bean.title);
                    if (new File(fileBean.filePath).exists()) {
                        url = fileBean.filePath;
                    } else {
                        url = fileBean.fileUrl;
                    }
                    //加载数据url
                    MyPlayerApi.getInstance().loadUri(bean, url);
                }

            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DownLoadImple.getInstance().updateDownloadPlayerStatus(currentBean);

        MyPlayerApi.getInstance().removeMediaPlayerListener(listener);
        MyPlayerApi.getInstance().destory();

        EventBus.getDefault().unregister(this);

        stopUpdateSeekBarProgree();
        mMusicHandler=null;

        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }


    @Subscriber(tag = TAG_PLAY_SERVICE_TIMER)
    private void onUpdateSeekBarProgress(boolean isStart) {
        if (isStart) {
            startUpdateSeekBarProgress();
        } else {
            stopUpdateSeekBarProgree();
        }
    }

    @Subscriber(tag = TAG_PLAY_SERVICE_SEEK)
    private void onHandSeek(int position) {
        MyPlayerApi.getInstance().seek(position);
    }

    @Subscriber(tag = TAG_PLAY_SERVICE_PAUSE_OR_START)
    private void onHandPauseOrStart(String item) {
        MyPlayerApi.MusicStatus status = MyPlayerApi.getInstance().startOrPause();
        if (status == MyPlayerApi.MusicStatus.PAUSE) {
            EventBus.getDefault().post(true, TAG_PLAY_UI_STARTOR_PAUSE);
        } else {
            EventBus.getDefault().post(false, TAG_PLAY_UI_STARTOR_PAUSE);
        }
    }

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
            if(SwitchConfig.isPlayExit){
                LocalBroadcastManager.getInstance(MyPlayerService.this).sendBroadcast(new Intent(ACTION_EXIT_ALL_LIFE));
            }else{//播放完

            }
        }
        @Override
        public void onBufferingUpdate(MediaPlayer mediaPlayer, int percent, DownLoadDbBean bean) {
            bean.buffet_percent = percent;
            EventBus.getDefault().post(bean, TAG_PLAY_UI_BUFFER);
        }
        @Override
        public boolean onError(MediaPlayer mediaPlayer, int i, int i1, DownLoadDbBean bean) {
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

            MyPlayerApi.getInstance().startPlay();
            bean.totalTime = during;
            EventBus.getDefault().post(bean, TAG_PLAY_UI_PREPARE);
            startUpdateSeekBarProgress();
        }
    };

    private BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            stopSelf();
        }
    };
}
