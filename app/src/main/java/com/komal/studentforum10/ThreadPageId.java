package com.komal.studentforum10;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class ThreadPageId {

    @Exclude
    public String threadPageId;

    public <T extends ThreadPageId> T withId(@NonNull final String id) {
        this.threadPageId = id;
        return (T) this;
    }

}
