package com.lingdian.xiaoshengchangtan.player;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.lingdian.xiaoshengchangtan.config.SwitchConfig;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by lingdian on 17/9/10.
 */

public class MyPlayer {
    private static MyPlayer myPlayer = null;

    private MediaPlayer player; // 定义多媒体对象
    private MediaCallBack callBack;
    private Context context;
    private List<MediaPlayerCallBack> callBackList;
    private MusicStatus currentStatus = MusicStatus.STOP;

    private MyPlayer() {
        player = new MediaPlayer();
        callBackList = new ArrayList<>();
        callBack = new MediaCallBack();
        player.setLooping(SwitchConfig.isLoop);

        player.setOnCompletionListener(callBack);
        player.setOnPreparedListener(callBack);
        player.setOnBufferingUpdateListener(callBack);
        player.setOnErrorListener(callBack);
        player.setOnSeekCompleteListener(callBack);
    }

    public static MyPlayer getInstance() {
        if (myPlayer == null) {
            myPlayer = new MyPlayer();
        }
        return myPlayer;
    }

    public void init(Context context1) {
        context = context1.getApplicationContext();
    }


    public void addMediaPlayerListener(MediaPlayerCallBack listener) {
        if (!callBackList.contains(listener)) {
            callBackList.add(listener);
        }
    }

    public void removeMediaPlayerListener(MediaPlayerCallBack listener) {
        if (callBackList.contains(listener)) {
            callBackList.remove(listener);
        }
    }

    public void loadUri(String url) {
        try {
            player.reset(); //重置多媒体
//            Uri url = musicList.get(songNum);//得到当前播放音乐的路径
            //为多媒体对象设置播放路径
            if (url.startsWith("http")) {
                Uri uri = Uri.parse(url);
                player.setDataSource(context, uri);
            } else {
                player.setDataSource(url);
            }
            player.prepareAsync();//准备播放
            currentStatus = MusicStatus.PLAY;
        } catch (Exception e) {

        }
    }

    /**
     * 暂停与继续播放
     */
    public MusicStatus startOrPause(){
        if (player.isPlaying()) {
            player.pause();
            currentStatus = MusicStatus.PAUSE;
        } else {
            player.start();
            currentStatus = MusicStatus.PLAY;
        }
        return currentStatus;
    }


    /**
     * 停止播放
     */
    public void stop() {
        if (player.isPlaying()) {
            player.stop();
            currentStatus=MusicStatus.STOP;
        }
    }

    /**
     * 彻底销毁
     */
    public void destory() {
        stop();
        player.release();
        player = null;
        myPlayer = null;
        currentStatus=MusicStatus.STOP;
    }

    /**
     * 是否是播放中
     *
     * @return
     */
    public boolean isPlaying() {
        return player.isPlaying();
    }

    /**
     * 设置是否重复播放
     *
     * @param isLoop 是否循环播放
     */
    public void setLoop(boolean isLoop) {
        player.setLooping(isLoop);
    }


    /**
     * 音乐当前的状态：只有播放、暂停、停止三种
     */
    public enum MusicStatus {
        PLAY, PAUSE, STOP
    }

    /**
     * 播放器的状态回调
     */
    private class MediaCallBack implements MediaPlayer.OnPreparedListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnErrorListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener {
        /**
         * OnCompletionListener
         * 当流媒体播放完毕的时候回调
         *
         * @param mediaPlayer
         */
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            for (MediaPlayerCallBack item : callBackList) {
                item.onCompletion(mediaPlayer);
            }
        }

        /**
         * OnBufferingUpdateListener
         *
         * @param mediaPlayer
         * @param percent
         */
        @Override
        public void onBufferingUpdate(MediaPlayer mediaPlayer, int percent) {
            Log.v("xhw", "onBufferingUpdate percent " + percent);
            for (MediaPlayerCallBack item : callBackList) {
                item.onBufferingUpdate(mediaPlayer, percent);
            }
        }

        /**
         * OnErrorListener
         *
         * @param mediaPlayer
         * @param i
         * @param i1
         * @return
         */
        @Override
        public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
            for (MediaPlayerCallBack item : callBackList) {
                item.onError(mediaPlayer, i, i1);
            }
            return false;
        }

        /**
         * OnSeekCompleteListener
         * 当使用seekTo()设置播放位
         *
         * @param mediaPlayer
         */
        @Override
        public void onSeekComplete(MediaPlayer mediaPlayer) {
            for (MediaPlayerCallBack item : callBackList) {
                item.onSeekComplete(mediaPlayer);
            }
        }

        /**
         * OnPreparedListener
         * 当装载流媒体完毕的时候回调
         *
         * @param mediaPlayer
         */
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            for (MediaPlayerCallBack item : callBackList) {
                item.onPrepared(mediaPlayer);
            }
            if (SwitchConfig.isSkipHead) {
                player.seekTo(SwitchConfig.SkipHeadTime);
            }

        }
    }

    /**
     * 开始播放
     */
    public void startPlay(){
        player.start();
    }

    public interface MediaPlayerCallBack {
        public void onCompletion(MediaPlayer mediaPlayer);

        public void onBufferingUpdate(MediaPlayer mediaPlayer, int percent);

        public boolean onError(MediaPlayer mediaPlayer, int i, int i1);

        public void onSeekComplete(MediaPlayer mediaPlayer);

        public void onPrepared(MediaPlayer mediaPlayer);
    }

}
