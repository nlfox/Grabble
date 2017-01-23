package com.example.nlfox.grabble;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class ScoreboardContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<ScoreItem> ITEMS = new ArrayList<ScoreItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, ScoreItem> ITEM_MAP = new HashMap<String, ScoreItem>();

    private static final int COUNT = 0;


    private static void addItem(ScoreItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

//    private static ScoreItem createDummyItem(int position) {
//        return new ScoreItem();
//    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class ScoreItem {
        public final String id;
        public final String name;
        public final String score;

        public ScoreItem(String id, String name, String score) {
            this.id = id;
            this.name = name;
            this.score = score;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
