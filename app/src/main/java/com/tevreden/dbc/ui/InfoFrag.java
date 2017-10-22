package com.tevreden.dbc.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;
import com.tevreden.dbc.AppController;
import com.tevreden.dbc.C;
import com.tevreden.dbc.G;
import com.tevreden.dbc.MainActivity;
import com.tevreden.dbc.R;
import com.tevreden.dbc.model.Info;
import com.tevreden.dbc.model.Organization;
import com.tevreden.dbc.model.Pet;
import com.tevreden.dbc.tinder.ui.TinderCardView;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.microedition.khronos.opengles.GL;

public class InfoFrag extends Fragment implements View.OnClickListener {

    TextView txtName, txtLikes;
    View vFirstIcon;
    TextView txtFirstTab, txtSecondTab;

    TextView txtColor, txtColorVal;
    TextView txtGender, txtGenderVal;
    TextView txtAge, txtAgeVal;
    TextView txtSize, txtSizeVal;
    TextView txtStatus, txtStatusVal;
    TextView txtInfo;
    ImageView imgAvatar;

    Toolbar toolbar;
    Organization org;
    TextView vContact;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frg_info, container, false);

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolBar);

        txtName = (TextView)rootView.findViewById(R.id.txtName);
        txtLikes = (TextView)rootView.findViewById(R.id.txtLikes);

        rootView.findViewById(R.id.rnFirst).setOnClickListener(this);
        rootView.findViewById(R.id.rnSecond).setOnClickListener(this);
        rootView.findViewById(R.id.vBack).setOnClickListener(this);
        vContact  = (TextView)rootView.findViewById(R.id.vContact);
        vContact.setOnClickListener(this);

        vFirstIcon = rootView.findViewById(R.id.vFirstIcon);

        txtFirstTab = (TextView)rootView.findViewById(R.id.txtFirstTab);
        txtSecondTab = (TextView)rootView.findViewById(R.id.txtSecondTab);

        txtColor = (TextView)rootView.findViewById(R.id.txtColor);
        txtColorVal = (TextView)rootView.findViewById(R.id.txtColorVal);
        txtGender = (TextView)rootView.findViewById(R.id.txtGender);
        txtGenderVal = (TextView)rootView.findViewById(R.id.txtGenderVal);
        txtAge = (TextView)rootView.findViewById(R.id.txtAge);
        txtAgeVal = (TextView)rootView.findViewById(R.id.txtAgeVal);
        txtSize = (TextView)rootView.findViewById(R.id.txtSize);
        txtSizeVal = (TextView)rootView.findViewById(R.id.txtSizeVal);
        txtStatus = (TextView)rootView.findViewById(R.id.txtStatus);
        txtStatusVal = (TextView)rootView.findViewById(R.id.txtStatusVal);

        txtInfo = (TextView)rootView.findViewById(R.id.txtInfo);

        imgAvatar = (ImageView)rootView.findViewById(R.id.imgAvatar);
        progDlg = new ProgressDialog(getContext());

        init();
        return rootView;
    }

    ProgressDialog progDlg;
    private void getDetail( final int animal_id ) {

        progDlg.setMessage("");
        progDlg.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progDlg.show();

        String url = C.baseUrl + "apis/animals/detail";
        StringRequest loginRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        Gson gSon = AppController.getInstance().getGson();
                        JsonElement jsonElement = gSon.fromJson(response, JsonElement.class);
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        progDlg.dismiss();

                        int err = jsonObject.get("errorcode").getAsInt();
                        if( err > 0 ) {
//                            int errId = G.getErrorMessage(errorcode);
//                            G.alertDisplayer(act, G.str( act, R.string.app_name), G.str(act, errId));
//                            requestFocus(editUserName);
                        } else {
                            JsonElement result = jsonObject.get("result");
                            jsonObject = result.getAsJsonObject();
                            JsonElement jsonOrg = jsonObject.get("org");
                            JsonElement jsonInfo = jsonObject.get("info");

                            org = gSon.fromJson(jsonOrg.toString(), Organization.class);
                            Info info = gSon.fromJson(jsonInfo.toString(), Info.class);
                            if( G.lang == C.LangEng )
                                txtInfo.setText( info.en );
                            else if( G.lang == C.LangNed )
                                txtInfo.setText( info.nl );
                            else
                                txtInfo.setText( info.pap );
                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progDlg.dismiss();
//                        G.alertDisplayer(act, G.str(act, R.string.app_name), G.str(act, R.string.db_error));
//                        requestFocus(editUserName);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("apikey", G.curUser.apikey);
                params.put("animal_id", String.valueOf(animal_id));
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(loginRequest);
    }

    public void init() {

        if( G.lang == C.LangEng ) {
            txtGender.setText( R.string.gender );
            txtAge.setText(R.string.age);
            txtColor.setText( R.string.color );
            txtSize.setText( R.string.size );
            txtStatus.setText(R.string.status );

            vContact.setText(R.string.adopt_me);
        } else if( G.lang == C.LangPap ) {
            txtGender.setText( R.string.gender_pa );
            txtAge.setText(R.string.age_pa);
            txtColor.setText( R.string.color_pa );
            txtSize.setText( R.string.size_pa );
            txtStatus.setText(R.string.status_pa );

            vContact.setText(R.string.adopt_me_pa);
        } else {
            txtGender.setText( R.string.gender_nd );
            txtAge.setText(R.string.age_nd);
            txtColor.setText( R.string.color_nd );
            txtSize.setText( R.string.size_nd );
            txtStatus.setText(R.string.status_nd );

            vContact.setText(R.string.adopt_me_nd);

        }

        if( G.pet != null ) {
            Pet pet = G.pet;
            Picasso.with( getContext() ).load(G.pet.picture).into(imgAvatar);

            txtGenderVal.setText(G.pet.getSex());
/*
            if( G.pet.sex.compareTo("male") == 0 ) {
                if( G.lang == C.LangEng )
                    txtGenderVal.setText(R.string.male);
                else if( G.lang == C.LangNed )
                    txtGenderVal.setText(R.string.male_nd);
                else
                    txtGenderVal.setText(R.string.male_pa);
            } else {
                if( G.lang == C.LangEng )
                    txtGenderVal.setText(R.string.female);
                else if( G.lang == C.LangNed )
                    txtGenderVal.setText(R.string.female_nd);
                else
                    txtGenderVal.setText(R.string.female_pa);
            }

            txtGenderVal.setText( pet.getSex() );
*/

            txtAgeVal.setText(G.pet.age + " " + getResources().getString(G.pet.getAgeUnit()));
            txtColorVal.setText(G.pet.color);

            if( G.pet.size.compareTo("small") == 0 ) {

                if( G.lang == C.LangEng )
                    txtSizeVal.setText(R.string.small);
                else if( G.lang == C.LangNed )
                    txtSizeVal.setText(R.string.small_nd);
                else
                    txtSizeVal.setText(R.string.small_pa);

            } else if( G.pet.size.compareTo("medium") == 0 ) {

                if( G.lang == C.LangEng )
                    txtSizeVal.setText(R.string.medium);
                else if( G.lang == C.LangNed )
                    txtSizeVal.setText(R.string.medium_nd);
                else
                    txtSizeVal.setText(R.string.medium_pa);

            } else {
                if( G.lang == C.LangEng )
                    txtSizeVal.setText(R.string.large);
                else if( G.lang == C.LangNed )
                    txtSizeVal.setText(R.string.large_nd);
                else
                    txtSizeVal.setText(R.string.large_pa);
            }

            if( G.lang == C.LangEng)
                txtStatusVal.setText(R.string.adoptable);
            else if( G.lang == C.LangNed )
                txtStatusVal.setText(R.string.adoptable_nd);
            else
                txtStatusVal.setText(R.string.adoptable_pa);

            txtName.setText(G.pet.name);
            getDetail( G.pet.animal_id );
        }
        toolbar.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {

        MainActivity act = (MainActivity)getActivity();

        switch (v.getId()) {
            case R.id.vContact:
                G.org = org;
                OrgDetailFrag frgOrgDetail = (OrgDetailFrag) getFragmentManager().findFragmentByTag( OrgDetailFrag.class.getSimpleName() );
                if( frgOrgDetail == null ) {
                    frgOrgDetail = new OrgDetailFrag();
                    frgOrgDetail.petName = G.pet.name;
                }
                else {
                    frgOrgDetail.petName = G.pet.name;
                    frgOrgDetail.init();
                }
                act.addFragment( frgOrgDetail );
                break;
            case R.id.vBack:
                act.removeFragment();
                break;
            case R.id.rnFirst:
                ContactFrag frg = (ContactFrag) getFragmentManager().findFragmentByTag("ContactFrag");
                if( frg == null )
                    frg = new ContactFrag();
                else {
                    frg.init();
                }
                act.addFragment( new ContactFrag() );
                break;
            case R.id.rnSecond:
                HealthFrag frgHealth = (HealthFrag) getFragmentManager().findFragmentByTag("HealthFrag");
                if( frgHealth == null )
                    frgHealth = new HealthFrag();
                else {
                    frgHealth.init();
                }
                act.addFragment(frgHealth);
                break;
        }
    }
}
