package com.greylabs.sumod.dbct10.Model;

/**
 * Created by Sumod on 6/3/2015.
 */
public class Category {

    //Private variables:
    private String name;
    private String description;

    //Constructors:

    public Category(String name, String description) {
        this.description = description;
        this.name = name;
    }

    public Category(){

    }

    //Getters:

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    //Setters:

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }
}
