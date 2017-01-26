package com.example.nlfox.grabble;

public class ScoreboardContent {

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
