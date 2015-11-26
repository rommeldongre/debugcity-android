package com.greylabs.sumod.dbct10.Model;

/**
 * Created by Sumod on 12-Oct-15.
 */
public class User {

    private String full_name;
    private String email_ID;
    private String password;
    private String mobile;
    private String location;
    private int credits;

    //constructors:

    public User() {
    }

    public User(String email_ID, String full_name) {
        this.email_ID = email_ID;
        this.full_name = full_name;
    }

    public User(String email_ID, String full_name, String password) {
        this.email_ID = email_ID;
        this.full_name = full_name;
        this.password = password;
    }

    //setters

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public void setEmail_ID(String email_ID) {
        this.email_ID = email_ID;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    //getters

    public int getCredits() {
        return credits;
    }

    public String getEmail_ID() {
        return email_ID;
    }

    public String getFull_name() {
        return full_name;
    }

    public String getLocation() {
        return location;
    }

    public String getMobile() {
        return mobile;
    }

    public String getPassword() {
        return password;
    }
}
