package com.komal.studentforum10;

public class NotifsPage extends NotifsPageId {

    public  NotifsPage(){}

    public NotifsPage(String username, String profile_image) {
        this.username = username;
        this.profile_image = profile_image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private String username;

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    private String profile_image;
}
