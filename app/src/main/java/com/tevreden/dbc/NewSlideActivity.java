package com.tevreden.dbc;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class NewSlideActivity extends AppCompatActivity implements View.OnClickListener {

    TextView txtContinue;
    TextView txtContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_slide);
        initView();
    }

    private void initView() {
        initStatusBar();

        txtContent = (TextView)findViewById(R.id.txtContent);
        txtContinue = (TextView)findViewById(R.id.txtContinue);
        txtContinue.setOnClickListener(this);

        if( G.lang == C.LangEng ) {
            txtContinue.setText(R.string.continue_uk);
            txtContent.setText( R.string.slider_uk);
        }
        else if( G.lang == C.LangNed ) {
            txtContinue.setText(R.string.continue_nd);
            txtContent.setText(R.string.slider_nd);
        }
        else {
            txtContinue.setText(R.string.continue_pa);
            txtContent.setText(R.string.slider_pa);
        }
    }

    private void initStatusBar() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return;
        int flag_translucent_status = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        getWindow().setFlags(flag_translucent_status, flag_translucent_status);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtContinue:
                Intent i = new Intent(NewSlideActivity.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                break;
        }
    }
}
