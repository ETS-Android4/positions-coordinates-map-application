package com.example.mapapplication.data;

public class User {

    public String email;
    public String username;
    public String password;
    public boolean flag;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String email, String username, String password, boolean flag) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.flag = flag;
    }
}
