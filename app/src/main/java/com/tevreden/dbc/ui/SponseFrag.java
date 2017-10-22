package com.tevreden.dbc.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.tevreden.dbc.R;
import com.tevreden.dbc.model.Pet;
import com.tevreden.dbc.model.Sponser;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SponseFrag extends Fragment {

    ProgressDialog progDlg;
    ArrayList<Sponser> sponsers = new ArrayList<Sponser>();

    LinearLayout lnGold, lnPlatinum;

    private void getSponsers() {

        progDlg.setMessage("");
        progDlg.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progDlg.show();

        sponsers.clear();
        String url = C.baseUrl + "apis/sponsers";
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
                            JsonArray jsonArray = result.getAsJsonArray();
                            for( JsonElement element : jsonArray ) {
                                final Sponser sponser = gSon.fromJson(element.toString(), Sponser.class);
                                sponsers.add(sponser);

                                ImageView img = new ImageView( getContext() );
                                TextView txt = new TextView( getContext() );
                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.MATCH_PARENT);
                                lp.setMargins(0,30,0,0);
                                img.setLayoutParams(lp);

                                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT);
                                lp.setMargins(0,30,0,0);
                                txt.setLayoutParams(lp1);
                                txt.setGravity(Gravity.CENTER_HORIZONTAL);
                                txt.setTextSize(20);
                                txt.setTextColor(R.color.colorPrimary);

                                img.setAdjustViewBounds(true);
                                img.setScaleType(ImageView.ScaleType.CENTER_CROP);

                                if( sponser.image.length() > 0 ) {
                                    Picasso.with(getContext()).load(sponser.image).into(img);
                                    img.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            if( Patterns.WEB_URL.matcher(sponser.link).matches() == false )
                                                return;

                                            Uri uri = Uri.parse(sponser.link);
                                            if( uri == null ) {
                                                return;
                                            }

                                            Intent iw = new Intent(Intent.ACTION_VIEW);
                                            iw.setData(uri);
                                            startActivity(iw);
                                        }
                                    });
                                    txt = null;
                                } else {
                                    img = null;
                                    txt.setText( sponser.text );
                                    txt.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            if( Patterns.WEB_URL.matcher(sponser.link).matches() == false )
                                                return;

                                            Uri uri = Uri.parse(sponser.link);
                                            if( uri == null ) {
                                                return;
                                            }

                                            Intent iw = new Intent(Intent.ACTION_VIEW);
                                            iw.setData(uri);
                                            startActivity(iw);
                                        }
                                    });
                                }

                                if( sponser.type.compareTo("gold") == 0 ) {
                                    if( txt == null )
                                        lnGold.addView(img);
                                    else
                                        lnGold.addView(txt);
                                } else {
                                    if( txt == null )
                                        lnPlatinum.addView(img);
                                    else
                                        lnPlatinum.addView(txt);
                                }
                            }
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
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(loginRequest);
    }

    public static SponseFrag newInstance() {
        Bundle args = new Bundle();
        SponseFrag fragment = new SponseFrag();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frg_sponse, container, false);

        progDlg = new ProgressDialog( getContext() );

        lnGold = (LinearLayout)rootView.findViewById(R.id.lnGold);
        lnPlatinum = (LinearLayout)rootView.findViewById(R.id.lnPlatinum);

        getSponsers();

        return rootView;
    }
}
