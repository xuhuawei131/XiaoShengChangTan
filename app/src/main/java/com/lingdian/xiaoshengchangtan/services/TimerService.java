package com.lingdian.xiaoshengchangtan.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.lingdian.xiaoshengchangtan.MyApp;
import com.lingdian.xiaoshengchangtan.config.SwitchConfig;
import com.lingdian.xiaoshengchangtan.enums.TimerType;

import org.simple.eventbus.EventBus;

import static com.lingdian.xiaoshengchangtan.config.EventBusTag.ACTION_ALARM;
import static com.lingdian.xiaoshengchangtan.config.EventBusTag.ACTION_ALARM_TIMER_PROGRESS;
import static com.lingdian.xiaoshengchangtan.config.EventBusTag.ACTION_EXIT_ALL_LIFE;

public class TimerService extends Service {
    private int socketCheckInterval = 5 * 60 * 1000;

    private int totalSeconds=0;

    public TimerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
       return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static void startTimer(TimerType timerType){
        Context context=MyApp.application;
        Intent intent=new Intent(context,TimerService.class);
        if(timerType==TimerType.TIMER_END){
            SwitchConfig.isPlayExit=true;
            context.stopService(intent);
        }else if(timerType==TimerType.TIMER_CANCEL){
            SwitchConfig.isPlayExit=false;
            context.stopService(intent);
        }else{
            intent.putExtra("time",timerType.timer);
            context.startService(intent);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent!=null){
            String action=intent.getAction();
            if(!TextUtils.isEmpty(action)&&action.equals(ACTION_ALARM)){
                totalSeconds--;
                if(totalSeconds<=0){
                    stopAlarm();
                    LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(ACTION_EXIT_ALL_LIFE));
                }else{//播放倒计时
                    EventBus.getDefault().post(totalSeconds,ACTION_ALARM_TIMER_PROGRESS);
                }
            }else{
                int timer=intent.getIntExtra("time",0);
                if(timer!=0){
                    stopAlarm();
                    startAlarm(timer);
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopAlarm();
    }

    /***
     * 使用闹钟来做唤醒
     */
    private void startAlarm(int timer) {
        totalSeconds=timer*60;
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), TimerService.class);
        intent.setAction(ACTION_ALARM);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // 立刻执行，此后5分钟走一次//SystemClock.elapsedRealtime()
        alarm.setRepeating(AlarmManager.ELAPSED_REALTIME, totalSeconds*1000, 1000, pendingIntent);
    }

    private void stopAlarm() {
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), TimerService.class);
        intent.setAction(ACTION_ALARM);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarm.cancel(pendingIntent);
    }

}
