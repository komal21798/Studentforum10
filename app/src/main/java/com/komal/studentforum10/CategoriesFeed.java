package com.komal.studentforum10;

public class CategoriesFeed extends CategoryId {

    private String thread_desc;
    private String thread_name;
    private String user_id;

    public CategoriesFeed() {}

    public CategoriesFeed(String thread_desc, String thread_name, String user_id) {
        this.thread_desc = thread_desc;
        this.thread_name = thread_name;
        this.user_id = user_id;
    }

    public String getThread_desc() {
        return thread_desc;
    }

    public void setThread_desc(String thread_desc) {
        this.thread_desc = thread_desc;
    }

    public String getThread_name() {
        return thread_name;
    }

    public void setThread_name(String thread_name) {
        this.thread_name = thread_name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
