package com.tevreden.dbc;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tevreden.dbc.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MenuActivity extends Activity implements View.OnClickListener {

    Timer T;
    public int mCount;
    View splash;
    View txtLogin, txtSignup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        G.pref = getSharedPreferences( getPackageName(), Context.MODE_PRIVATE);

        if( G.w == 0 ) {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            G.w = metrics.widthPixels;
            G.h = metrics.heightPixels;
        }

        initStatusBar();
        initView();

        mCount = 0;

        Bundle b = getIntent().getExtras();
        boolean isLoaded = false;
        if( b != null )
            isLoaded = b.getBoolean("isloaded");
        if( isLoaded == false ) {
            T = new Timer();
            T.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mCount++;
                            if( mCount == 2000 ) {
                                T.cancel();
                                T = null;
                                splash.setVisibility(View.GONE);

//                            boolean isLogin = prefs.getBoolean("login", false);
//                            String uname = prefs.getString("signed_name", "");
//                            String signed_pw = prefs.getString("signed_pw", "");
//
//                            if( isLogin == false ) {
//                                Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
//                                startActivity(intent);
//                            }
                            }
                        }
                    });
                }
            }, 1, 1);
        } else {
            splash.setVisibility(View.GONE);
        }
    }

    private void initView() {
        splash = findViewById(R.id.imgSplash); splash.setVisibility(View.VISIBLE);
        txtLogin = findViewById(R.id.txtLogin); txtLogin.setOnClickListener(this);
        txtSignup = findViewById(R.id.txtSignup); txtSignup.setOnClickListener(this);
    }

    private void initStatusBar() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return;
        int flag_translucent_status = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        getWindow().setFlags(flag_translucent_status, flag_translucent_status);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtLogin:
                Intent intent;
                intent = new Intent(MenuActivity.this, LoginActivity.class);
//                intent = new Intent(MenuActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.txtSignup:
                Intent intent1 = new Intent(MenuActivity.this, SignupActivity.class);
                startActivity(intent1);
                break;
        }
    }
}
