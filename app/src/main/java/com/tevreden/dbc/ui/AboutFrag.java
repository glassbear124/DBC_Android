package com.tevreden.dbc.ui;

import android.content.Intent;
import android.net.Uri;
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

public class AboutFrag extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frg_about, container, false);

        TextView txtContent = (TextView)rootView.findViewById(R.id.txtContent);
        if(G.lang == C.LangEng) {
            txtContent.setText(R.string.about_uk);
        } else if( G.lang == C.LangNed ) {
            txtContent.setText(R.string.about_nd);
        } else {
            txtContent.setText(R.string.about_pa);
        }

        TextView txtUrl= (TextView)rootView.findViewById(R.id.txtUrl);
        txtUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("http://www.sportential.com");
                if( uri == null ) {
                    return;
                }

                Intent iw = new Intent(Intent.ACTION_VIEW);
                iw.setData(uri);
                startActivity(iw);

            }
        });
        return rootView;
    }
}
