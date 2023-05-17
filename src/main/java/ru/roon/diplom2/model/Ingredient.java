package ru.roon.diplom2.model;

import com.google.gson.annotations.SerializedName;

public class Ingredient {
    @SerializedName("_id")
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
