package com.tevreden.dbc.ui;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.tevreden.dbc.MainActivity;
import com.tevreden.dbc.R;

public class ContactFrag extends Fragment implements View.OnClickListener{

    ImageView imgAvatar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frg_contact, container, false);

        rootView.findViewById(R.id.vBack).setOnClickListener(this);
        initStatusBar();
        init();
        return rootView;
    }

    public void init() {
    }

    private void initStatusBar() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return;
        int flag_translucent_status = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        getActivity().getWindow().setFlags(flag_translucent_status, flag_translucent_status);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.vBack:
                MainActivity act = (MainActivity)getActivity();
                act.removeFragment();
                break;
        }
    }
}