package com.komal.studentforum10;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class CommentsFeedId {

        @Exclude
        public String commentsFeedId;

        public <T extends com.komal.studentforum10.CommentsFeedId> T withId(@NonNull final String id) {
            this.commentsFeedId = id;
            return (T) this;

        }
}
