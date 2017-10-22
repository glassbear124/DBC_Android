package com.tevreden.dbc.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.tevreden.dbc.G;
import com.tevreden.dbc.MainActivity;
import com.tevreden.dbc.R;
import com.tevreden.dbc.model.Organization;

import org.w3c.dom.Text;

public class OrgDetailFrag extends Fragment implements View.OnClickListener{

    ImageView imgAvatar;
    Toolbar toolbar;
    TextView txtOrg, txtContactor, txtAddress, txtPhone1, txtPhone2, txtEmail;

    public String petName;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frg_org_detail, container, false);

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolBar);

        imgAvatar = (ImageView)rootView.findViewById(R.id.imgAvatar);

        txtOrg = (TextView) rootView.findViewById(R.id.txtOrg);
        txtContactor = (TextView)rootView.findViewById(R.id.txtContactor);
        txtAddress = (TextView)rootView.findViewById(R.id.txtAddress);
        txtPhone1 = (TextView)rootView.findViewById(R.id.txtPhone1);
        txtPhone1.setOnClickListener(this);
        txtPhone2 = (TextView)rootView.findViewById(R.id.txtPhone2);
        txtPhone2.setOnClickListener(this);
        txtEmail = (TextView)rootView.findViewById(R.id.txtEmail);
        txtEmail.setOnClickListener(this);

        rootView.findViewById(R.id.vBack).setOnClickListener(this);
        initStatusBar();
        init();
        return rootView;
    }

    public void init() {
        toolbar.setVisibility(View.GONE);

        if( G.org.logo != null && G.org.logo.length() > 0 ) {
            Picasso.with( getContext() ).load(G.org.logo).into(imgAvatar);
        }
        txtOrg.setText( G.org.name );
        txtContactor.setText(G.org.contact_pers);
        txtAddress.setText(G.org.address);
        txtPhone1.setText(G.org.tel1);
        txtPhone2.setText(G.org.tel2);
        txtEmail.setText(G.org.email);

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

            case R.id.txtPhone1:
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+G.org.tel1));
                callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(callIntent);
                break;

            case R.id.txtPhone2:
                Intent i = new Intent(Intent.ACTION_CALL);
                i.setData(Uri.parse("tel:"+G.org.tel2));
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(i);
                break;

            case R.id.txtEmail:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{G.org.email} );
                if( petName != null )
                    intent.putExtra(Intent.EXTRA_SUBJECT, petName);
                try {
                    startActivity(Intent.createChooser(intent, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}