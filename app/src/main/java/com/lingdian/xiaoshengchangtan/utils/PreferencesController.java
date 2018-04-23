package com.lingdian.xiaoshengchangtan.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.lingdian.xiaoshengchangtan.MyApp;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by lingdian on 2018/4/23.
 */

public class PreferencesController {
    private static final String URL = "http://live.jlradio.net:80/VodFiles//am738/%s/1200005400_mp4/120000_5400_543210K.mp4";
    public static final String PREFERENCE_NAME = "xsct_pref";
    // 版本信息
    public static final String PREF_VERSION_INFO = "versionInfo";

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private static PreferencesController mPreferences;

    public PreferencesController(Context context) {
        mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME,
                Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    public static synchronized PreferencesController getPreferences(
            Context context) {
        if (mPreferences == null) {
            mPreferences = new PreferencesController(
                    context.getApplicationContext());
        }
        return mPreferences;
    }

    public static PreferencesController getInstance() {
        return getPreferences(MyApp.getAppContext());
    }

    public SharedPreferences getSharedPreference() {
        return mSharedPreferences;
    }

    private ArrayList<SharedPreferences.OnSharedPreferenceChangeListener> mListeners = new ArrayList<SharedPreferences.OnSharedPreferenceChangeListener>();

    public synchronized boolean removeListener(
            SharedPreferences.OnSharedPreferenceChangeListener listener) {
        boolean ret = false;
        if (listener != null) {
            ret = mListeners.remove(listener);
        }
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);
        return ret;
    }

    public synchronized boolean addListener(
            SharedPreferences.OnSharedPreferenceChangeListener listener) {
        boolean ret = false;
        if (listener != null) {
            if (!mListeners.contains(listener)) {
                ret = mListeners.add(listener);
            }
            mSharedPreferences
                    .registerOnSharedPreferenceChangeListener(listener);
        }
        return ret;
    }

    public void setSharePreferencesListener(
            SharedPreferences.OnSharedPreferenceChangeListener listener) {
        mSharedPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    public void delPreferencesListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

    public void setString(String key, String value) {
        mEditor.putString(key, value);
        mEditor.commit();
    }

    public String getString(String key) {
        return mSharedPreferences.getString(key, "");
    }

    public static void setInt(String key, int value) {
        getInstance().setIntValue(key, value);
    }

    public void setIntValue(String key, int value) {
        mEditor.putInt(key, value);
        mEditor.commit();
    }

    public static int getInt(String key) {
        return getInstance().getIntValue(key);
    }

    public int getIntValue(String key) {
        return mSharedPreferences.getInt(key, -1);
    }

    public static int getInt(String key, int defaultValue) {
        return getInstance().getIntValue(key, defaultValue);
    }

    private int getIntValue(String key, int defaultValue) {
        return mSharedPreferences.getInt(key, defaultValue);
    }

    public void setLong(String key, long value) {
        mEditor.putLong(key, value);
        mEditor.commit();
    }

    public long getLong(String key) {
        return mSharedPreferences.getLong(key, -1);
    }

    public static void setBoolean(String key, boolean value) {
        getInstance().setBooleanValue(key, value);
    }

    private void setBooleanValue(String key, boolean value) {
        mEditor.putBoolean(key, value);
        mEditor.commit();
    }

    public static boolean getBoolean(String key, boolean value) {
        return getInstance().getBooleanValue(key, value);
    }

    private boolean getBooleanValue(String key, boolean value) {
        return mSharedPreferences.getBoolean(key, value);
    }

    public static void setStringValue(String key, String value) {
        getInstance().setString(key, value);
    }

    public static String getStringValue(String key) {
        return getInstance().getString(key);
    }

    public static void setLongValue(String key, Long value) {
        getInstance().setLong(key, value);
    }

    public static Long getLongValue(String key) {
        return getInstance().getLong(key);
    }

    @SuppressLint("NewApi")
    public static Set<String> getStringSetValue(String key) {
        return getInstance().getSharedPreference().getStringSet(key, null);
    }

    public static void setStringSet(String key, Set<String> set) {
        getInstance().setStringSetValue(key, set);
    }

    @SuppressLint("NewApi")
    private void setStringSetValue(String key, Set<String> set) {
        mEditor.putStringSet(key, set);
        mEditor.commit();
    }

    public static void setStringList(String key, List<String> list) {
        getInstance().setStringListValue(key, list);
    }

    private void setStringListValue(String key, List<String> list) {
        if (list == null || list.isEmpty()) {
            mEditor.remove(key + "_size");
            mEditor.commit();
            return;
        }
        mEditor.putInt(key + "_size", list.size()); /* sKey is an array */
        for (int i = 0; i < list.size(); i++) {
            mEditor.remove(key + "_" + i);
            mEditor.putString(key + "_" + i, list.get(i));
        }
        mEditor.commit();
    }

    public static List<String> getStringList(String key) {
        return getInstance().getStringListValue(key);
    }

    private List<String> getStringListValue(String key) {
        int size = mSharedPreferences.getInt(key + "_size", 0);
        if (size == 0)
            return null;
        List<String> lists = new ArrayList<String>();
        for (int i = 0; i < size; i++) {
            String string = mSharedPreferences.getString(key + "_" + i, null);
            lists.add(string);
        }
        return lists;
    }

    public static void setIntList(String key, List<Integer> list) {
        getInstance().setIntListValue(key, list);
    }

    private void setIntListValue(String key, List<Integer> list) {
        if (list == null || list.isEmpty()) {
            mEditor.remove(key + "_size");
            mEditor.commit();
            return;
        }
        mEditor.putInt(key + "_size", list.size()); /* sKey is an array */
        for (int i = 0; i < list.size(); i++) {
            mEditor.remove(key + "_" + i);
            mEditor.putInt(key + "_" + i, list.get(i));
        }
        mEditor.commit();
    }

    public static List<Integer> getIntList(String key) {
        return getInstance().getIntListValue(key);
    }

    private List<Integer> getIntListValue(String key) {
        int size = mSharedPreferences.getInt(key + "_size", 0);
        if (size == 0)
            return null;
        List<Integer> lists = new ArrayList<Integer>();
        for (int i = 0; i < size; i++) {
            int string = mSharedPreferences.getInt(key + "_" + i, 0);
            lists.add(string);
        }
        return lists;
    }





    public boolean contains(String key){
        return mSharedPreferences.contains(key);
    }
}
