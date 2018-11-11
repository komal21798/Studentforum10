package com.komal.studentforum10;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class ExploreFeedId {

    @Exclude
    public String exploreFeedId;

    public <T extends ExploreFeedId> T withId(@NonNull final String id) {
        this.exploreFeedId = id;
        return (T) this;
    }

}
