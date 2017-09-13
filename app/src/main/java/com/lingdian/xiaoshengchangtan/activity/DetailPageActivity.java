package com.lingdian.xiaoshengchangtan.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lingdian.xiaoshengchangtan.R;
import com.lingdian.xiaoshengchangtan.bean.FileBean;
import com.lingdian.xiaoshengchangtan.bean.PageBean;
import com.lingdian.xiaoshengchangtan.config.SwitchConfig;
import com.lingdian.xiaoshengchangtan.customview.DiscView;
import com.lingdian.xiaoshengchangtan.player.MyPlayer;
import com.lingdian.xiaoshengchangtan.services.MusicService;
import com.lingdian.xiaoshengchangtan.utils.DateUtils;

import java.io.File;

import static com.lingdian.xiaoshengchangtan.activity.NetEasyActivity.MUSIC_MESSAGE;

public class DetailPageActivity extends AppCompatActivity {

    private PageBean bean;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        setContentView(R.layout.activity_detail_page);
        findviewByIds();
        requestService();
    }

    private void initData() {
        bean = (PageBean) getIntent().getSerializableExtra("bean");
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

    private void findviewByIds() {

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
        text_currentTime = (TextView) findViewById(R.id.text_currentTime);
        text_totalTime = (TextView) findViewById(R.id.text_totalTime);

        mSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        MyPlayer.getInstance().addMediaPlayerListener(mediaPlayerCallBack);
    }

    private void requestService() {
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

       //加载数据url
        MyPlayer.getInstance().loadUri(url);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopUpdateSeekBarProgree();
    }

    private void play() {
        optMusic(MusicService.ACTION_OPT_MUSIC_PLAY);
        startUpdateSeekBarProgress();
    }

    private void pause() {
        optMusic(MusicService.ACTION_OPT_MUSIC_PAUSE);
        ivPlayOrPause.setImageResource(R.drawable.ic_play);
        stopUpdateSeekBarProgree();
    }

    private void stop() {
        ivPlayOrPause.setImageResource(R.drawable.ic_play);
        stopUpdateSeekBarProgree();
        reset();
    }

    private void next() {
        optMusic(MusicService.ACTION_OPT_MUSIC_NEXT);
        reset();
    }

    private void last() {
        optMusic(MusicService.ACTION_OPT_MUSIC_LAST);
        reset();
    }


    private void reset() {
        stopUpdateSeekBarProgree();
        mSeekBar.setProgress(0);
        text_currentTime.setText(DateUtils.duration2Time(0));
        text_totalTime.setText(DateUtils.duration2Time(0));

    }

    private void complete(boolean isOver) {
        if (isOver) {
//            mDisc.stop();
        } else {
//            mDisc.next();
        }
    }

    private void optMusic(final String action) {
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(action));
    }


    private Handler mMusicHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mSeekBar.setProgress(mSeekBar.getProgress() + 1000);
            text_currentTime.setText(DateUtils.duration2Time(mSeekBar.getProgress()));
            startUpdateSeekBarProgress();
        }
    };

    private void startUpdateSeekBarProgress() {
        /*避免重复发送Message*/
        stopUpdateSeekBarProgree();
        mMusicHandler.sendEmptyMessageDelayed(0, 1000);
    }

    private void stopUpdateSeekBarProgree() {
        mMusicHandler.removeMessages(MUSIC_MESSAGE);
    }

    private void seekTo(int position) {
        Intent intent = new Intent(MusicService.ACTION_OPT_MUSIC_SEEK_TO);
        intent.putExtra(MusicService.PARAM_MUSIC_SEEK_TO, position);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.ivPlayOrPause:
                    MyPlayer.MusicStatus status=MyPlayer.getInstance().startOrPause();
                    if(status== MyPlayer.MusicStatus.PAUSE){
                        ivPlayOrPause.setImageResource(R.drawable.ic_play);
                        stopUpdateSeekBarProgree();
                    }else{
                        ivPlayOrPause.setImageResource(R.drawable.ic_pause);
                        startUpdateSeekBarProgress();
                    }
                    break;
                case R.id.ivNext:

                    break;
                case R.id.ivLast:

                    break;

            }
        }
    };


    /**
     * 音频播放的回调
     */
    private MyPlayer.MediaPlayerCallBack mediaPlayerCallBack = new MyPlayer.MediaPlayerCallBack() {

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
            int during=mediaPlayer.getDuration();
            mSeekBar.setMax(during);
            text_totalTime.setText(DateUtils.duration2Time(during));
        }
    };


    private void playStatus(){
        MyPlayer.getInstance().startPlay();
        startUpdateSeekBarProgress();

    }
    private void pauseStatus(){


    }

    /**
     * 拖动进度条的回调
     */
    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            text_currentTime.setText(DateUtils.duration2Time(progress));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            seekTo(seekBar.getProgress());
            startUpdateSeekBarProgress();
        }
    };


}
