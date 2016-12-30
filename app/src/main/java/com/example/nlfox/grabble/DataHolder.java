package com.example.nlfox.grabble;

import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;

/**
 * Created by nlfox on 12/21/16.
 */

public class DataHolder {

    private HashMap<Character, Integer> letters;
    private HashMap<String, Boolean> collected;

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


    public void collectPoint(String letter,String point) {
        addLetter(letter.charAt(0));
        collected.put(point, true);
    }

    public boolean addLetter(Character c) {
        if (letters.containsKey(c)) {
            letters.put(c, letters.get(c) + 1);
        } else {
            letters.put(c, 1);
        }
        return true;
    }


    public HashMap<Character, Integer> getLetters() {
        return letters;
    }

    private static final DataHolder holder = new DataHolder();

    public static DataHolder getInstance() {
        return holder;
    }
}
