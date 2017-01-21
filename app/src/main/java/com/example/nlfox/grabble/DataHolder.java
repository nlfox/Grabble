package com.example.nlfox.grabble;

import com.google.android.gms.maps.model.Marker;
import com.google.gson.internal.LinkedTreeMap;

import java.util.Map;
import java.util.HashMap;

/**
 * Created by nlfox on 12/21/16.
 */

public class DataHolder {

    private Map<Character, Integer> letters;
    private Map<String, Boolean> collected;
    private Integer count;
    private Integer total_distance;

    public String toString() {
        return letters.toString();
    }

    private DataHolder() {
        letters = new HashMap<>();
        collected = new HashMap<>();
        for (int i = 0; i < 26; i++) {
            letters.put((char) (i + 'a'), 0);
        }
    }

    public void removeWord(String word) {
        for (int i = 0; i < 7; i++) {
            letters.put(word.charAt(i), letters.get(word.charAt(i)) - 1);
        }
    }


    public void collectPoint(String letter, String point) {
        addLetter(letter.charAt(0));
        collected.put(point, true);
    }

    public Map<String, Boolean> getCollected() {
        return collected;
    }

    public boolean addLetter(Character c) {
        if (letters.containsKey(c)) {
            letters.put(c, letters.get(c) + 1);
        } else {
            letters.put(c, 1);
        }
        return true;
    }


    public Map<Character, Integer> getLetters() {
        return letters;
    }

    private static final DataHolder holder = new DataHolder();


    public Integer getCount() {
        return count;
    }

    public Integer getTotalDistance() {
        return total_distance;
    }



    public void initialize(LinkedTreeMap m) {
        collected = (Map<String, Boolean>) m.get("collected");
        Map<String, Double> letters_before = (Map<String, Double>) m.get("letter");
        count = ((Double) m.get("count")).intValue();
        total_distance = ((Double) m.get("distance")).intValue();
        letters = new HashMap<>();
        for (String i : letters_before.keySet()) {
            letters.put(i.charAt(0), letters_before.get(i).intValue());
        }
    }

    public static DataHolder getInstance() {
        return holder;
    }
}
