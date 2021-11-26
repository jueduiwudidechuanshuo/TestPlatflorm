package com.bignerdranch.android.testplatflorm.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.bignerdranch.android.testplatflorm.database.PostDbSchema.PostTable;

public class PostBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "postBase.db";

    public PostBaseHelper (Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + PostTable.NAME + "(" +
                "_id integer primary key autoincrement, " +
                PostTable.Cols.UUID + ", " +
                PostTable.Cols.TITLE + ", " +
                PostTable.Cols.LABEL + ", " +
                PostTable.Cols.URI +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
