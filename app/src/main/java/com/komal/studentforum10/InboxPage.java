package com.komal.studentforum10;

public class InboxPage extends InboxPageId{

    public InboxPage(String username, String thread_name, String thread_desc) {
        this.username = username;
        this.thread_name = thread_name;
        this.thread_desc = thread_desc;
    }

    private String username;
    private String thread_name;

    public String getUsername() {
        return username;
    }

    public void setUsername(String user_id) {
        this.username = user_id;
    }

    public String getThread_name() {
        return thread_name;
    }

    public void setThread_name(String thread_name) {
        this.thread_name = thread_name;
    }

    public String getThread_desc() {
        return thread_desc;
    }

    public void setThread_desc(String thread_desc) {
        this.thread_desc = thread_desc;
    }

    private String thread_desc;

    public InboxPage() {}


}
