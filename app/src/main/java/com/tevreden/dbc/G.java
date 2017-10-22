package com.tevreden.dbc;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tevreden.dbc.model.Organization;
import com.tevreden.dbc.model.Pet;
import com.tevreden.dbc.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class G {

    static public int w;
    static public int h;

    public static int lang;  //0: eng, 1: ned; 2: pap
    public static int like;  //0: dog. 1: cat

    public static User curUser = null;


    public static Pet pet;
    public static ArrayList<Pet> pets = new ArrayList<Pet>();

    static SharedPreferences pref;


    public static Organization org;
    public static ArrayList<Organization> orgs = new ArrayList<Organization>();

    public static void alertDisplayer(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    public static void showToast( Context context, String message ) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        View bg = toast.getView();
        TextView tv = (TextView)bg.findViewById(android.R.id.message);
        bg.setBackgroundResource(R.drawable.corner_white_fill);
        tv.setTextColor(Color.BLUE);
        toast.show();
    }



    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }

        return phrase.toString();
    }

    public static void setFlag( final int petId, final int like ) { // like:1 unlike: 0, -1: delete from like

        String url = C.baseUrl + "apis/animals/flag";
        StringRequest loginRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        G.alertDisplayer(act, G.str(act, R.string.app_name), G.str(act, R.string.db_error));
//                        requestFocus(editUserName);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("apikey", G.curUser.apikey);
                params.put("animal_id", String.valueOf(petId));
                params.put("flag", String.valueOf(like));
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(loginRequest);
    }

//    static public FragmentManager gFrgMgr;
}
