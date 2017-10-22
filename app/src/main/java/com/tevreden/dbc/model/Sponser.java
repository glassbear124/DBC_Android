package com.tevreden.dbc.model;


import com.google.gson.annotations.SerializedName;

public class Sponser {

    public @SerializedName("sponser_id") int sponser_id;
    public @SerializedName("type") String type;
    public @SerializedName("image") String image;
    public @SerializedName("link") String link;
    public @SerializedName("text") String text;
}
