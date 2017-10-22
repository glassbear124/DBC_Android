package com.tevreden.dbc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;


public class LangActivity extends AppCompatActivity implements View.OnClickListener {

    TextView txtChooseLang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lang);
        initView();
    }

    private void initView() {

        txtChooseLang = (TextView)findViewById(R.id.txtChooseLang);

        findViewById(R.id.imgUsa).setOnClickListener(this);
        findViewById(R.id.imgNed).setOnClickListener(this);
        findViewById(R.id.imgPap).setOnClickListener(this);

        initStatusBar();
    }

    void loadLang() {
        if( G.lang == C.LangEng ) {
            txtChooseLang.setText(R.string.choose_lang);
        }
        else if( G.lang == C.LangNed ) {
            txtChooseLang.setText(R.string.choose_lang_nd);
        }
        else {
            txtChooseLang.setText(R.string.choose_lang_pa);
        }

        Intent i;
        boolean isSecond = G.pref.getBoolean("isSecond", false);

        if( isSecond ) {
            i = new Intent(LangActivity.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        } else {
            SharedPreferences.Editor editor = G.pref.edit();
            editor.putBoolean("isSecond", true);
            editor.commit();
            i = new Intent(LangActivity.this, NewSlideActivity.class);
        }
        startActivity(i);
    }

    private void initStatusBar() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return;
        int flag_translucent_status = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        getWindow().setFlags(flag_translucent_status, flag_translucent_status);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgUsa:
                G.lang = C.LangEng; loadLang();
                break;
            case R.id.imgNed:
                G.lang = C.LangNed; loadLang();
                break;
            case R.id.imgPap:
                G.lang = C.LangPap; loadLang();
                break;
        }
    }
}
