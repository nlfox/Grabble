package com.example.nlfox.grabble;

import android.content.SharedPreferences;

import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by nlfox on 16-10-22.
 */

public class WebModel {
    private static OkHttpClient client = null;
    private static String site = "http://192.168.137.222:5000/";
    private static String token;
    private Context c;

    class Message {
        String message;
        int code;
    }

    public WebModel(Context context) {
        c = context;
        token = SpUtils.getString(c, "token");
        if (client == null) {
            client = new OkHttpClient();
        }
    }

    String getToken() {
        return token;
    }

    String collectPoint(String letter, String point) throws IOException {
        // TODO: 12/23/16
        Map<String, String> paras = new HashMap<>();
        paras.put("point", point);
        paras.put("letter", letter);
        return post(site + "letter/add", paras);
    }

    String makeWord(String word) throws IOException {
        Map<String, String> paras = new HashMap<>();
        paras.put("word", word);
        return post(site + "letter/word", paras);
    }

    String get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(site + url + "?token=" + token)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    Boolean has_token() {
        return token.equals("");
    }

    String post(String url, Map<String, String> paras) throws IOException {

        FormBody.Builder formBodyBuilder = new FormBody.Builder();

        for (Map.Entry<String, String> entry : paras.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            formBodyBuilder.add(key, value);
        }


        Request request = new Request.Builder()
                .url(url + "?token=" + token)
                .post(formBodyBuilder.build())
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public int register(String user, String password) {
        Map<String, String> m = new HashMap<>();
        m.put("username", user);
        m.put("password", password);
        String res = "";

        try {
            res = post(site + "user/register", m);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Gson obj = new Gson();

        login(user, password);
        Message msgObj = obj.fromJson(res, Message.class);

        return msgObj.code;
    }

    public int login(String user, String password) {
        Map<String, String> m = new HashMap<>();
        m.put("username", user);
        m.put("password", password);
        String res = "";

        try {
            res = post(site + "user/login", m);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Gson obj = new Gson();
        Message msgObj = obj.fromJson(res, Message.class);
        token = msgObj.message;
        SpUtils.putString(c, "token", token);
        return msgObj.code;
    }


}
