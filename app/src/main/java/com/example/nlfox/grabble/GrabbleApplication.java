package com.example.nlfox.grabble;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;

/**
 * Created by nlfox on 12/23/16.
 */


public class GrabbleApplication extends Application {

    //private static GrabbleApplication mContext;
    private WebModel webModel;
    private DataHolder dataHolder;

    public WebModel getWebModel() {
        return webModel;
    }

    public DataHolder getDataHolder() {
        return dataHolder;
    }

    public void onCreate() {
        super.onCreate();
        dataHolder = DataHolder.getInstance();
        webModel = new WebModel(this);

    }

    public static GrabbleApplication getAppContext(Context x) {

        return (GrabbleApplication) x;
    }

    public boolean collectPoint(String letter, String point) throws IOException {
        try {
            webModel.collectPoint(letter, point);
            dataHolder.collectPoint(letter, point);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean makeWord(String s) throws IOException {
        webModel.makeWord(s);
        dataHolder.removeWord(s);
        return true;
    }

    public void fromJSON(String s) {

    }
    Boolean ready = false;

    Boolean isReady() {
        return ready;
    }

    public void initialize() {
        try {
            String data = webModel.get("info");
            Gson gson = new Gson();
            LinkedTreeMap result = gson.fromJson(data, LinkedTreeMap.class);
            DataHolder.getInstance().initialize(result);
            Log.v("data", data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ready = true;

    }


}

