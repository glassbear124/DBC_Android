package com.tevreden.dbc.ui;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.tevreden.dbc.MainActivity;
import com.tevreden.dbc.R;

public class HealthFrag extends Fragment {

    public static AboutFrag newInstance() {
        Bundle args = new Bundle();
        AboutFrag fragment = new AboutFrag();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frg_health, container, false);
        rootView.findViewById(R.id.vBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity act = (MainActivity)getActivity();
                act.removeFragment();
                return;
            }
        });
        init();
        initStatusBar();
        return rootView;
    }

    private void initStatusBar() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return;
        int flag_translucent_status = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        getActivity().getWindow().setFlags(flag_translucent_status, flag_translucent_status);
    }

    public void init() {

    }
}
