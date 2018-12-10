package com.komal.studentforum10;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class NotifsPageId {

    @Exclude
    public String notifsPageId;

    public <T extends NotifsPageId> T withId(@NonNull final String id) {
        this.notifsPageId = id;
        return (T) this;
    }

}
