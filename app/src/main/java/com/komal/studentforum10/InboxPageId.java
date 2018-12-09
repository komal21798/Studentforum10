package com.komal.studentforum10;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class InboxPageId {

    @Exclude
    public String inboxPageId;

    public <T extends InboxPageId> T withId(@NonNull final String id) {
        this.inboxPageId = id;
        return (T) this;
    }

}
