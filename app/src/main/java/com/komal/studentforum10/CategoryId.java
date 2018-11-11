package com.komal.studentforum10;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class CategoryId {

    @Exclude
    public String CategoryId;

    public <T extends CategoryId> T withId(@NonNull final String id) {
        this.CategoryId = id;
        return (T) this;
    }

}
