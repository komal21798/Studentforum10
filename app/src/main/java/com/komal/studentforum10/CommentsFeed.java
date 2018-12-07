package com.komal.studentforum10;

import java.util.Date;

public class CommentsFeed extends CommentsFeedId {

    public String comment;
    public String user_id;
    public Date timestamp;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }




    public CommentsFeed(String comment, String user_id, Date timestamp) {
        this.comment = comment;
        this.user_id = user_id;
        this.timestamp = timestamp;
    }




}
