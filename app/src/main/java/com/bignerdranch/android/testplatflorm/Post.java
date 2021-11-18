package com.bignerdranch.android.testplatflorm;

import java.util.UUID;

public class Post {
    private UUID mId;
    private String mCode;
    private String mTitle;
    private String mLabel;

    public Post() {
        this(UUID.randomUUID());
    }

    public Post(UUID id) {
        mId = id;
    }

    public UUID getId() {
        return mId;
    }

    public void setId(UUID id) {
        mId = id;
    }

    public String getCode() {
        return mCode;
    }

    public void setCode(String code) {
        mCode = code;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getLabel() {
        return mLabel;
    }

    public void setLabel(String label) {
        mLabel = label;
    }

    public String getPhotoFilename() {
        return "IMG_ " + getId().toString() + ".jpg";
    }
}
