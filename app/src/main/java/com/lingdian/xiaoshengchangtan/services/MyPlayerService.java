package com.lingdian.xiaoshengchangtan.services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.IntDef;

import com.lingdian.xiaoshengchangtan.bean.PageBean;
import com.lingdian.xiaoshengchangtan.player.MyPlayer;

/**
 * 播放的服务
 */
public class MyPlayerService extends Service {

    public MyPlayerService() {

    }

    @Override
    public IBinder onBind(Intent intent) {

       return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        MyPlayer.getInstance().addMediaPlayerListener(listener);
    }

    public static void startPlay(PageBean bean){


    }



    @Override
    public int onStartCommand(Intent intent,  int flags, int startId) {


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyPlayer.getInstance().removeMediaPlayerListener(listener);
    }

    private MyPlayer.MediaPlayerCallBack listener=new MyPlayer.MediaPlayerCallBack(){


        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {

        }

        @Override
        public void onBufferingUpdate(MediaPlayer mediaPlayer, int percent) {

        }

        @Override
        public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
            return false;
        }

        @Override
        public void onSeekComplete(MediaPlayer mediaPlayer) {

        }

        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {

        }
    };
}
