package com.tevreden.dbc.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tevreden.dbc.C;
import com.tevreden.dbc.G;
import com.tevreden.dbc.R;

public class ContactUsFrag extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frg_contact_us, container, false);

        TextView txtContent = (TextView)rootView.findViewById(R.id.txtContent);
        if(G.lang == C.LangEng) {
            txtContent.setText(R.string.contact_us_uk);
        } else if( G.lang == C.LangNed ) {
            txtContent.setText(R.string.contact_us_nd);
        } else {
            txtContent.setText(R.string.contact_us_pa);
        }

        return rootView;
    }
}
