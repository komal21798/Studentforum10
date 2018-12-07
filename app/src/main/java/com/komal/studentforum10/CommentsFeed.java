package com.komal.studentforum10;

import java.util.Date;

public class CommentsFeed extends CommentsFeedId {

    public String comment;
    public String user_id;
    public Date timestamp;

    public String getPost_name() {
        return post_name;
    }

    public void setPost_name(String post_name) {
        this.post_name = post_name;
    }

    public String getThread_name() {
        return thread_name;
    }

    public void setThread_name(String thread_name) {
        this.thread_name = thread_name;
    }

    public String post_name;
    public String thread_name;

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



    public CommentsFeed() {}

    public CommentsFeed(String comment, String user_id, Date timestamp, String post_name, String thread_name) {
        this.comment = comment;
        this.user_id = user_id;
        this.timestamp = timestamp;
        this.post_name = post_name;
        this.thread_name = thread_name;
    }




}
