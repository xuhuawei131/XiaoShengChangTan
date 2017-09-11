package com.lingdian.xiaoshengchangtan.player;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.lingdian.xiaoshengchangtan.config.SwitchConfig;


/**
 * Created by lingdian on 17/9/10.
 */

public class MyPlayer {
    private static MyPlayer myPlayer = null;

    private MediaPlayer player; // 定义多媒体对象
    private MediaCallBack callBack;
    private Context context;

    private MyPlayer() {
        player = new MediaPlayer();

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


    public void start(String dataSource) {
        try {
            player.reset(); //重置多媒体
//            Uri dataSource = musicList.get(songNum);//得到当前播放音乐的路径
            //为多媒体对象设置播放路径
            if (dataSource.startsWith("http")) {
                Uri uri = Uri.parse(dataSource);
                player.setDataSource(context, uri);
            } else {
                player.setDataSource(dataSource);
            }
            player.prepareAsync();//准备播放

        } catch (Exception e) {
        }
    }



    /**
     * 暂停与继续播放
     */
    public void pause() {
        if (player.isPlaying())
            player.pause();
        else
            player.start();
    }

    /**
     * 停止播放
     */
    public void stop() {
        if (player.isPlaying()) {
            player.stop();
        }
        player.release();
    }

    /**
     * 彻底销毁
     */
    public void destory() {
        if (player.isPlaying()) {
            player.stop();
        }
        player.release();
        player = null;
        myPlayer = null;
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
     * @param isLoop 是否循环播放
     */
    public void setLoop(boolean isLoop){
        player.setLooping(isLoop);
    }




    private class MediaCallBack implements MediaPlayer.OnPreparedListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnErrorListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener {
        /**
         * OnCompletionListener
         * 当流媒体播放完毕的时候回调
         *
         * @param mediaPlayer
         */
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {

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

        }

        /**
         * OnPreparedListener
         * 当装载流媒体完毕的时候回调
         *
         * @param mediaPlayer
         */
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            if(SwitchConfig.isSkipHead){
                player.seekTo(SwitchConfig.SkipHeadTime);
            }
                player.start();//开始播放
        }
    }

}
