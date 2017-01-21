package com.example.nlfox.grabble;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.nlfox.grabble.dummy.ScoreboardContent;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.Lock;

/**
 * Created by nlfox on 12/23/16.
 */


public class GrabbleApplication extends Application {

    //private static GrabbleApplication mContext;
    private WebModel webModel;
    private DataHolder dataHolder;
    private Trie t;
    private String letterMap;

    public boolean isNightMode() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getBoolean("night_mode", false);
    }

    public boolean isHardMode() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getBoolean("hard_mode", false);
    }


    public WebModel getWebModel() {
        return webModel;
    }

    public DataHolder getDataHolder() {
        return dataHolder;
    }

    public Trie getTrie() {
        return t;
    }

    public void onCreate() {
        super.onCreate();
        dataHolder = DataHolder.getInstance();
        webModel = new WebModel(this);


    }


    public void logout() {
        SharedPreferences preferences = getSharedPreferences("app", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    public static GrabbleApplication getAppContext(Context x) {

        return (GrabbleApplication) x;
    }


    public boolean collectPoint(String letter, String point) throws IOException {

        webModel.collectPoint(letter, point);
        dataHolder.collectPoint(letter, point);

        return true;
    }

    public boolean makeWord(String s) throws IOException {
        webModel.makeWord(s);
        dataHolder.removeWord(s);
        return true;
    }

    void updateScoreboard() {
        try {
            scoreItems = webModel.getScoreboardItem();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    List<ScoreboardContent.ScoreItem> scoreItems;

    Boolean ready = false;

    Boolean isReady() {
        return ready;
    }

    public void initialize() throws Exception {

        String data = webModel.get("info");
        Gson gson = new Gson();
        scoreItems = webModel.getScoreboardItem();
        t = new Trie(getResources().openRawResource(R.raw.grabble));
        LinkedTreeMap result = gson.fromJson(data, LinkedTreeMap.class);
        DataHolder.getInstance().initialize(result);
        if (!webModel.has_letter_map()) {
            webModel.downloadLetterMap();
        }
        Log.v("data", data);

        ready = true;

    }


}

