package com.example.nlfox.grabble;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Pair;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by nlfox on 16-10-22.
 */

public class WebModel {
    private static OkHttpClient client = null;
    private static String site = "http://138.68.1.13:5000/";
    private static String token;
    private static String letterMap;
    private Context c;


    class Message {
        String message;
        int code;
    }


    public WebModel(Context context) {
        c = context;
        token = SpUtils.getString(c, "token");
        letterMap = SpUtils.getString(c, "letterMap_"+getWeekDay());

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

    List<Pair<String, Integer>> getScoreboard() throws IOException {
        List<Pair<String, Integer>> l = new ArrayList<>();
        Gson obj = new Gson();
        Type listType = new TypeToken<ArrayList<ArrayList<String>>>() {
        }.getType();
        ArrayList<ArrayList<String>> a = obj.fromJson(get("scoreboard"), listType);
        ArrayList<Pair<String, Integer>> res = new ArrayList<>();
        for (ArrayList<String> item : a) {
            res.add(new Pair<>(item.get(0), Integer.parseInt(item.get(1))));
        }
        return res;
    }

    List<ScoreboardContent.ScoreItem> getScoreboardItem() throws IOException {
        List<Pair<String, Integer>> a;
        List<ScoreboardContent.ScoreItem> s = new ArrayList<>();
        a = getScoreboard();
        for (Integer i = 0; i < a.size(); i++) {
            s.add(new ScoreboardContent.ScoreItem(i.toString(), a.get(i).first, a.get(i).second.toString()));
        }
        return s;
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

    String get(String site, String url) throws IOException {
        Request request = new Request.Builder()
                .url(site + url + "?token=" + token)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public String getWeekDay(){
        // http://stackoverflow.com/questions/18256521/android-calendar-get-current-day-of-week-as-string


        String weekDay = "";

        Calendar c = Calendar.getInstance();
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

        if (Calendar.MONDAY == dayOfWeek) {
            weekDay = "monday";
        } else if (Calendar.TUESDAY == dayOfWeek) {
            weekDay = "tuesday";
        } else if (Calendar.WEDNESDAY == dayOfWeek) {
            weekDay = "wednesday";
        } else if (Calendar.THURSDAY == dayOfWeek) {
            weekDay = "thursday";
        } else if (Calendar.FRIDAY == dayOfWeek) {
            weekDay = "friday";
        } else if (Calendar.SATURDAY == dayOfWeek) {
            weekDay = "saturday";
        } else if (Calendar.SUNDAY == dayOfWeek) {
            weekDay = "sunday";
        }
        return weekDay;
    }
    void downloadLetterMap() throws IOException {

        letterMap = get("http://www.inf.ed.ac.uk/", "teaching/courses/selp/coursework/" + getWeekDay() + ".kml");
        SpUtils.putString(c,"letterMap_"+getWeekDay(),letterMap);

    }

    Boolean has_token() {
        return token.equals("");
    }

    String getLetterMap(){

        return letterMap;
    }

    Boolean has_letter_map() {
        return !letterMap.equals("");
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
        if (msgObj.code == 1) {
            token = msgObj.message;
            SpUtils.putString(c, "token", token);
        }
        return msgObj.code;
    }


}
