package io.anda.trackingapp;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Random;

public class ConfigUtil {

    private static final String PREFERENCE_NAME = "MY_PREFERENCE";
    private static final String KEY_THEMES = "THEMES";
    private static final String KEY_BALANCE = "BALANCE";
    private static final int[] THEMES= new int[]{
            R.mipmap.ic_marker_blue,
            R.mipmap.ic_marker_red,
            R.mipmap.ic_marker_green};

    public static int getMarkerThemeDrawable(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        int id = new Random().nextInt(THEMES.length);
        return sharedPref.getInt(KEY_THEMES, THEMES[id]);
    }

    public static void putMarkerThemes(Context context, int id){
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).edit();
        editor.putInt(KEY_THEMES, id);
    }

    public static int getBalanceConfig(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedPref.getInt(KEY_BALANCE, 0);
    }

    public static void putBalanceConfig(Context context, int balance){
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).edit();
        editor.putInt(KEY_BALANCE, balance);
    }
}
