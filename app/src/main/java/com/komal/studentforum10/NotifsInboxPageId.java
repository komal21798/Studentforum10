package com.komal.studentforum10;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class NotifsInboxPageId {

    @Exclude
    public String notifsInboxPageId;

    public <T extends NotifsInboxPageId> T withId(@NonNull final String id) {
        this.notifsInboxPageId = id;
        return (T) this;
    }
}
