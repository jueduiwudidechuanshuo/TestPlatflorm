package com.bignerdranch.android.testplatflorm.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.bignerdranch.android.testplatflorm.Post;
import com.bignerdranch.android.testplatflorm.database.PostDbSchema.*;

import java.util.UUID;

public class PostCursorWrapper extends CursorWrapper {
    public PostCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Post getPost() {
        String uuidString = getString(getColumnIndex(PostTable.Cols.UUID));
        String title = getString(getColumnIndex(PostTable.Cols.TITLE));
        String label = getString(getColumnIndex(PostTable.Cols.LABEL));

        Post post = new Post(UUID.fromString(uuidString));
        post.setTitle(title);
        post.setLabel(label);

        return post;
    }
}
