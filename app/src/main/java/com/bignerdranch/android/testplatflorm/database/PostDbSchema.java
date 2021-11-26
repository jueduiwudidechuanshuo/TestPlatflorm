package com.bignerdranch.android.testplatflorm.database;

public class PostDbSchema {
    public static final class PostTable {
        public static final String NAME = "Posts";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String LABEL = "label";
            public static final String URI = "uri";
        }
    }

}
