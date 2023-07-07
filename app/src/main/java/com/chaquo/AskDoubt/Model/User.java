package com.chaquo.AskDoubt.Model;

public class User {
String email,fullname,imageUrl,username;
    public User() {
    }

    public User(String email, String fullname, String imageUrl, String username) {
        this.email = email;
        this.fullname = fullname;
        this.imageUrl = imageUrl;
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
