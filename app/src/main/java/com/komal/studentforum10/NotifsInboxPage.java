package com.komal.studentforum10;

public class NotifsInboxPage extends NotifsPageId {

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NotifsInboxPage() {}

    public NotifsInboxPage(String message) {
        this.message = message;
    }

    private String message;


}
