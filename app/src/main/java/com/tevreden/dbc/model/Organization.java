package com.tevreden.dbc.model;

import com.google.gson.annotations.SerializedName;

public class Organization {
    public @SerializedName("org_id") String org_id;
    public @SerializedName("logo") String logo;
    public @SerializedName("name") String name;
    public @SerializedName("contact_pers") String contact_pers;
    public @SerializedName("email") String email;
    public @SerializedName("tel1") String tel1;
    public @SerializedName("tel2") String tel2;
    public @SerializedName("address") String address;
    public @SerializedName("animal_count") int animal_count;
}
