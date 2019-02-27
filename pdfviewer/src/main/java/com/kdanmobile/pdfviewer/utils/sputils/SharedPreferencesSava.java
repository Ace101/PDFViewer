package com.kdanmobile.pdfviewer.utils.sputils;

import android.content.Context;
import android.content.SharedPreferences;

import com.kdanmobile.pdfviewer.base.ProApplication;

public class SharedPreferencesSava {
    public static final String DEFAULT_SPNAME = "pdf_reader_pro.pref";
    private SharedPreferences sharedPreferences;

    private SharedPreferencesSava() {
    }

    private final static class SingleTon {
        private final static SharedPreferencesSava instance = new SharedPreferencesSava();
    }

    public static SharedPreferencesSava getInstance() {
        return SingleTon.instance;
    }

    public void savaIntValue(String spName, String key, int value) {
        sharedPreferences = ProApplication.getContext().getSharedPreferences(spName, Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(key, value).apply();
    }

    public int getIntValue(String spName, String key) {
        return getIntValue(spName, key, 0);
    }

    public int getIntValue(String spName, String key, int defaultValue) {
        sharedPreferences = ProApplication.getContext().getSharedPreferences(spName, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, defaultValue);
    }

    public void savaFloatValue(String spName, String key, float value) {
        SharedPreferences preferences = ProApplication.getContext().getSharedPreferences(spName, Context.MODE_PRIVATE);
        preferences.edit().putFloat(key, value).apply();
    }

    public float getFloatValue(String spName, String key) {
        return getFloatValue(spName, key, 0);
    }

    public float getFloatValue(String spName, String key, float defaultValue) {
        SharedPreferences preferences = ProApplication.getContext().getSharedPreferences(spName, Context.MODE_PRIVATE);
        return preferences.getFloat(key, defaultValue);
    }

    public void savaBooleanValue(String spName, String key, boolean value) {
        sharedPreferences = ProApplication.getContext().getSharedPreferences(spName, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(key, value).commit();
    }

    public boolean getBooleanValue(String spName, String key) {
        return getBooleanValue(spName, key, false);
    }

    public boolean getBooleanValue(String spName, String key, boolean isDefault) {
        SharedPreferences preferences = ProApplication.getContext().getSharedPreferences(spName, Context.MODE_PRIVATE);
        return preferences.getBoolean(key, isDefault);
    }

    public synchronized void savaLongValue(String spName, String key, long value) {
        SharedPreferences preferences = ProApplication.getContext().getSharedPreferences(spName, Context.MODE_PRIVATE);
        preferences.edit().putLong(key, value).apply();
    }

    public long getLongValue(String spName, String key) {
        return getLongValue(spName, key, 0L);
    }

    public long getLongValue(String spName, String key, long defaultValue) {
        SharedPreferences preferences = ProApplication.getContext().getSharedPreferences(spName, Context.MODE_PRIVATE);
        return preferences.getLong(key, defaultValue);
    }

    public void savaStringValue(String spName, String key, String value) {
        SharedPreferences preferences = ProApplication.getContext().getSharedPreferences(spName, Context.MODE_PRIVATE);
        preferences.edit().putString(key, value).apply();
    }

    public String getStringValue(String spName, String key) {
        return getStringValue(spName, key, "");
    }

    public String getStringValue(String spName, String key, String defvalue) {
        SharedPreferences preferences = ProApplication.getContext().getSharedPreferences(spName, Context.MODE_PRIVATE);
        return preferences.getString(key, defvalue);
    }

    public void clear(String spName) {
        SharedPreferences preferences = ProApplication.getContext().getSharedPreferences(spName, Context.MODE_PRIVATE);
        preferences.edit().clear();
    }

    public void removeAct(String spName, String key) {
        SharedPreferences preferences = ProApplication.getContext().getSharedPreferences(spName, Context.MODE_PRIVATE);
        preferences.edit().remove(key);
    }
}
