package com.bignerdranch.android.testplatflorm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bignerdranch.android.testplatflorm.database.PostBaseHelper;
import com.bignerdranch.android.testplatflorm.database.PostCursorWrapper;
import com.bignerdranch.android.testplatflorm.database.PostDbSchema.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PostLab {
    private static PostLab sPostLab;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static PostLab get(Context context) {
        if (sPostLab == null) {
            sPostLab = new PostLab(context);
        }
        return sPostLab;
    }

    private PostLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new PostBaseHelper(mContext)
                .getWritableDatabase();
    }

    public void addPost(Post post) {
        ContentValues values = getContentValues(post);

        mDatabase.insert(PostTable.NAME, null, values);
    }

    public List<Post> getPosts() {
        List<Post> posts = new ArrayList<>();

        PostCursorWrapper cursor = queryPosts(null, null);

        try{
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                posts.add(cursor.getPost());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return posts;
    }

    public Post getPost(UUID id) {
        PostCursorWrapper cursor = queryPosts(
                PostTable.Cols.UUID + " = ?",
                new String[] { id.toString() }
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getPost();
        } finally {
            cursor.close();
        }
    }

    public File getPhotoFile(Post post) {
        File filesDir = mContext.getFilesDir();
        return new File(filesDir, post.getPhotoFilename());
    }

    public void updatePost(Post post) {
        String uuidString = post.getId().toString();
        ContentValues values = getContentValues(post);
        mDatabase.update(PostTable.NAME, values,
                PostTable.Cols.UUID + " = ?",
                new String[] { uuidString });
    }

    private PostCursorWrapper queryPosts(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                PostTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new PostCursorWrapper(cursor);
    }

    private static ContentValues getContentValues(Post post) {
        ContentValues values = new ContentValues();
        values.put(PostTable.Cols.UUID, post.getId().toString());
        values.put(PostTable.Cols.TITLE, post.getTitle());
        values.put(PostTable.Cols.LABEL, post.getLabel());

        return values;
    }
}
