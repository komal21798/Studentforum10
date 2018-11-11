package com.komal.studentforum10;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class HomeFeedId {

    @Exclude
    public String homeFeedId;

    public <T extends HomeFeedId> T withId(@NonNull final String id) {
        this.homeFeedId = id;
        return (T) this;
    }

}
