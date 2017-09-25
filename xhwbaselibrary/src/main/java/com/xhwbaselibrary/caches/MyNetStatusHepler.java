package com.xhwbaselibrary.caches;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
import android.util.Log;

/**
 * Created by lingdian on 17/9/25.
 */

public class MyNetStatusHepler {
    private static final String TAG="xhw";
    private static MyNetStatusHepler instance;
    private Context context;
    private MyNetStatusHepler(){
        context=MyAppContext.getInstance().getContext();
    }
    public static MyNetStatusHepler getInstance(){
        if (instance==null){
           instance=new MyNetStatusHepler();
        }
        return instance;
    }

    public void register(){
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        filter.addAction("android.net.wifi.STATE_CHANGE");
        context.registerReceiver(mNetworkChangeListener, filter);
    }

    public void unRegister(){
        context.unregisterReceiver(mNetworkChangeListener);
    }


    /**
     * 判断当前网络状态是否可用
     *
     * @return
     */
    public  boolean isNetConnected() {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null) {
                    return info.isConnectedOrConnecting();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    /**
     * wifi是否连接
     *
     * @return
     */
    public  boolean hasWifi() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.getType() == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * 移动网络是否连接
     * @return
     */
    public  boolean hasMobileNetwork() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.getType() == ConnectivityManager.TYPE_MOBILE;
    }
    /**
     * 是否有网络连接
     *
     * @return
     */
    public  boolean hasNetwork() {
        return hasWifi() || hasMobileNetwork();
    }

    private void checkNetStatus(){
        Log.v(TAG,"*************checkNetStatus*************");
        boolean isConnected=MyNetStatusHepler.getInstance().isNetConnected();
        if(isConnected){
            boolean isWifi=MyNetStatusHepler.getInstance().hasWifi();
            if(isWifi){
                Log.v(TAG,"checkNetStatus---Wifi 连接");
            }else{
                Log.v(TAG,"checkNetStatus---4G 连接");
            }
        }else{
            Log.v(TAG,"checkNetStatus---无网络连接");
        }

    }
    private BroadcastReceiver mNetworkChangeListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkNetStatus();
            // 这个监听wifi的打开与关闭，与wifi的连接无关
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                Log.i(TAG,"1-－－WIFI变化－－－WIFI_STATE_CHANGED_ACTION---1");

                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                switch (wifiState) {
                    case WifiManager.WIFI_STATE_DISABLED:
                        Log.v(TAG,"WIFI_STATE_DISABLED");
//                        APP.getInstance().setEnablaWifi(false);
                        break;
                    case WifiManager.WIFI_STATE_DISABLING:
                        Log.v(TAG,"WIFI_STATE_DISABLING");
                        break;
                    case WifiManager.WIFI_STATE_ENABLING:
                        Log.v(TAG,"WIFI_STATE_ENABLING");
                        break;
                    case WifiManager.WIFI_STATE_ENABLED:
                        Log.v(TAG,"WIFI_STATE_ENABLED");
//                        APP.getInstance().setEnablaWifi(true);
                        break;
                    case WifiManager.WIFI_STATE_UNKNOWN:
                        Log.v(TAG,"WIFI_STATE_UNKNOWN");
                        break;
                    default:
                        break;
                }
            } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                // 这个监听wifi的连接状态即是否连上了一个有效无线路由，当上边广播的状态是WifiManager
                // .WIFI_STATE_DISABLING，和WIFI_STATE_DISABLED的时候，根本不会接到这个广播。
                // 在上边广播接到广播是WifiManager.WIFI_STATE_ENABLED状态的同时也会接到这个广播，
                // 当然刚打开wifi肯定还没有连接到有效的无线
                Log.i(TAG,"2-－－WIFI有效路由－－－NETWORK_STATE_CHANGED_ACTION---2");
                Parcelable parcelableExtra = intent
                        .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (null != parcelableExtra) {
                    NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                    NetworkInfo.State state = networkInfo.getState();
                    boolean isConnected = state == NetworkInfo.State.CONNECTED;// 当然，这边可以更精确的确定状态
                    if (isConnected) {
                        Log.v(TAG,"WIFI true");
//                            APP.getInstance().setWifi(true);
                    } else {
                        Log.v(TAG,"WIFI false");
//                            APP.getInstance().setWifi(false);
                    }
                }
            } else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                Log.i(TAG,"3-－－网络变化－－－CONNECTIVITY_ACTION---3");
                // 这个监听网络连接的设置，包括wifi和移动数据的打开和关闭。.
                // 最好用的还是这个监听。wifi如果打开，关闭，以及连接上可用的连接都会接到监听。见log
                // 这个广播的最大弊端是比上边两个广播的反应要慢，如果只是要监听wifi，我觉得还是用上边两个配合比较合适
                ConnectivityManager manager = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
                if (activeNetwork != null) { // connected to the internet
                    if (activeNetwork.isConnected()) {
                        if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                            // connected to wifi
//                        APP.getInstance().setWifi(true);
                            Log.e(TAG, "当前WiFi连接可用 ");
                        } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                            // connected to the mobile provider's data plan
//                        APP.getInstance().setMobile(true);
                            Log.e(TAG, "当前移动网络连接可用 ");
                        }
                    } else {
                        Log.e(TAG, "当前没有网络连接，请确保你已经打开网络 ");
                    }


                    Log.e(TAG, "info.getTypeName()" + activeNetwork.getTypeName());
                    Log.e(TAG, "getSubtypeName()" + activeNetwork.getSubtypeName());
                    Log.e(TAG, "getState()" + activeNetwork.getState());
                    Log.e(TAG, "getDetailedState()"
                            + activeNetwork.getDetailedState().name());
                    Log.e(TAG, "getDetailedState()" + activeNetwork.getExtraInfo());
                    Log.e(TAG, "getType()" + activeNetwork.getType());
                } else {   // not connected to the internet
                    Log.e(TAG, "当前没有网络连接，请确保你已经打开网络 ");
                }
            }
        }
    };
}
