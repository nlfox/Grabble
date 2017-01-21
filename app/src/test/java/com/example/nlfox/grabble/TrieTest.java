package com.example.nlfox.grabble;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by nlfox on 1/14/17.
 */
public class TrieTest {
    @Test
    public void addString() throws Exception {
        assertEquals(1, 1);
    }

    @Test
    public void validWord() throws Exception {

    }

    @Test
    public void serialize() throws Exception {
        assertEquals(getFile("serialized.txt").trim(), t.serialize());
    }


    @Test
    public void suggest() {
        assertArrayEquals(t.getSuggestion("holi").toArray(),new String[]{"holiday"});
        //System.out.println(t.getSuggestion("holi"));
    }

    @Test
    public void deserialize() throws Exception {

        Trie t1 = new Trie(getFile("serialized.txt"));
        assertEquals(t1.validWord("holiday"), true);
    }

    String dict;
    Trie t;

    public String getFile(String file) {
        InputStream is = getClass().getResourceAsStream(file);
        String s1;
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        s1 = s.hasNext() ? s.next() : "";
        return s1.trim();
    }

    @Before
    public void setUp() throws Exception {
        String s = getFile("grabble.txt");
        t = new Trie();
        for (String line : s.split("\n")) {
            t.addString(line.trim().toLowerCase());
        }


    }


}