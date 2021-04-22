package com.tst.flutodo.model;

import com.google.gson.annotations.SerializedName;

public class TodoItem {

    @SerializedName("Key")
    private String key;
    @SerializedName("Name")
    private String name;
    @SerializedName("IsComplete")
    private Boolean isCompleted;


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getCompleted() {
        return isCompleted;
    }

    public void setCompleted(Boolean completed) {
        isCompleted = completed;
    }


    public TodoItem(String key, String name, Boolean isCompleted) {
        this.key = key;
        this.name = name;
        this.isCompleted = isCompleted;
    }
}
