package com.tevreden.dbc.model;


import com.google.gson.annotations.SerializedName;

public class User {

    public @SerializedName("apikey") String apikey;
    public @SerializedName("first_name") String first_name;
    public @SerializedName("last_name") String last_name;
    public @SerializedName("email") String email;
    public @SerializedName("phone") String phone;
    public @SerializedName("avatar") String avatar;

    public String username;

}
