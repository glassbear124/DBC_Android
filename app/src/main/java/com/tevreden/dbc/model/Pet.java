package com.tevreden.dbc.model;

import com.google.gson.annotations.SerializedName;
import com.tevreden.dbc.C;
import com.tevreden.dbc.G;
import com.tevreden.dbc.R;


public class Pet {

    public @SerializedName("animal_id") int animal_id;
    public @SerializedName("category") String category;
    public @SerializedName("sex") String sex;
    public @SerializedName("name") String name;
    public @SerializedName("age_unit") String age_unit;
    public @SerializedName("age") String age;
    public @SerializedName("color") String color;
    public @SerializedName("size") String size;
    public @SerializedName("status") String status;
    public @SerializedName("picture") String picture;


    public int getSex() {

        if( sex.compareTo("male") == 0)
        {
            if( G.lang == C.LangEng )
                return R.string.male;
            else if( G.lang == C.LangNed )
                return R.string.male_nd;
            else
                return R.string.male_pa;
        } else {
            if( G.lang == C.LangEng )
                return R.string.female;
            else if( G.lang == C.LangNed )
                return R.string.female_nd;
            else
                return R.string.female_pa;
        }
    }

    public int getAgeUnit() {
        if( age_unit.compareTo("day") == 0)
        {
            if( G.lang == C.LangEng )
                return R.string.day;
            else if( G.lang == C.LangNed )
                return R.string.day_nd;
            else
                return R.string.day_pa;
        }
        else if( age_unit.compareTo("week") == 0)
        {
            if( G.lang == C.LangEng )
                return R.string.week;
            else if( G.lang == C.LangNed )
                return R.string.week_nd;
            else
                return R.string.week_pa;
        }
        else if( age_unit.compareTo("month") == 0)
        {
            if( G.lang == C.LangEng )
                return R.string.month;
            else if( G.lang == C.LangNed )
                return R.string.month_nd;
            else
                return R.string.month_pa;
        }
        else {
            if( G.lang == C.LangEng )
                return R.string.year;
            else if( G.lang == C.LangNed )
                return R.string.year_nd;
            else
                return R.string.year_pa;
        }
    }

}
