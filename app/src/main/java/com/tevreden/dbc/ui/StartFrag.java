package com.tevreden.dbc.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateOvershootInterpolator;
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
import com.tevreden.dbc.AppController;
import com.tevreden.dbc.C;
import com.tevreden.dbc.G;
import com.tevreden.dbc.MainActivity;
import com.tevreden.dbc.R;
import com.tevreden.dbc.model.User;
import com.tevreden.dbc.model.Pet;
import com.tevreden.dbc.tinder.ui.TinderCardView;
import com.tevreden.dbc.tinder.ui.TinderStackLayout;

import java.util.HashMap;
import java.util.Map;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

public class StartFrag extends Fragment implements View.OnClickListener {

    // region Constants
    private static final int STACK_SIZE = 4;

    private TinderStackLayout tinderStackLayout;

    private int index = 0;

    private int counter = 0;
    ProgressDialog progDlg;

    public void reset() {
        progDlg.setMessage("");
        progDlg.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progDlg.show();

        String url = C.baseUrl + "apis/animals/reset";
        StringRequest loginRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        Gson gSon = AppController.getInstance().getGson();
                        JsonElement jsonElement = gSon.fromJson(response, JsonElement.class);
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        int err = jsonObject.get("errorcode").getAsInt();
                        if( err > 0 ) {
//                            int errId = G.getErrorMessage(errorcode);
//                            G.alertDisplayer(act, G.str( act, R.string.app_name), G.str(act, errId));
//                            requestFocus(editUserName);
                        } else {
                            tinderStackLayout.removeAllViews();
                            index = 0;
                            getFeed();
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

    void getFeed() {

        G.pets.clear();
        String url = C.baseUrl + "apis/animals/feed";
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
                                Pet pet = gSon.fromJson(element.toString(), Pet.class);
                                G.pets.add(pet);
                            }

                            if( G.pets.size() > 0 )
                                rnMask.setVisibility(View.GONE);

                            TinderCardView tc;
                            for(int i=index; index<i+STACK_SIZE; index++){
                                if( index > G.pets.size() - 1 )
                                    continue;
                                tc = new TinderCardView( getActivity() );
                                tc.bind(G.pets.get(index));
                                tinderStackLayout.addCard(tc);
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
                if( G.like == C.LikeCat )
                    params.put("category", "cat");
                else
                    params.put("category", "dog");
                params.put("org_id", "");
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(loginRequest);
    }

    View rnMask;
    TextView txtHavenot;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frg_start, container, false);

        MainActivity act = (MainActivity)getActivity();
        act.showRightButton(View.VISIBLE);
        act.changeRightButtinIcon(R.drawable.reset);


        rootView.findViewById(R.id.vLike).setOnClickListener(this);
        rootView.findViewById(R.id.vUnlike).setOnClickListener(this);
        rootView.findViewById(R.id.vInfo).setOnClickListener(this);


        tinderStackLayout = (TinderStackLayout) rootView.findViewById(R.id.tsl);
        progDlg = new ProgressDialog( getActivity());

        rnMask = rootView.findViewById(R.id.rnMask);
        txtHavenot = (TextView)rootView.findViewById(R.id.txtHavenot);
        if( G.lang == C.LangEng ) {
            txtHavenot.setText( R.string.havenot_fevorite );
        } else if( G.lang == C.LangNed ) {
            txtHavenot.setText( R.string.havenot_fevorite_nd );
        } else
            txtHavenot.setText( R.string.havenot_fevorite_pa );

        progDlg.setMessage("");
        progDlg.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progDlg.show();
        getFeed();

        tinderStackLayout.getPublishViewSubject()
                .observeOn(AndroidSchedulers.mainThread()) // UI Thread
                .subscribe(new Subscriber<View>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {}

                    @Override
                    public void onNext(View view) {

                        Pet pet = G.pets.get(counter);
                        TinderCardView v = (TinderCardView)view;

                        if( v.isSpecial == true ) {
                            return;
                        }
                        else if( v.dX > 0 ) {
                            G.setFlag( pet.animal_id, 1 );
                            counter++;
                        } else {
                            G.setFlag( pet.animal_id, 0 );
                            counter++;
                        }
                        Log.e("err", "dx" + v.dX );
                    }
                });

        tinderStackLayout.getPublishSubject()
                .observeOn(AndroidSchedulers.mainThread()) // UI Thread
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Integer integer) {

                        if(integer == 1){
                            TinderCardView tc;
                            for(int i=index; index<i+(STACK_SIZE-1); index++){
                                if( index > G.pets.size()-1 ) {
                                    continue;
                                }

                                tc = new TinderCardView( getActivity() );
                                tc.bind(G.pets.get(index));
                                tinderStackLayout.addCard(tc);
                            }
                        }
                        else if( integer == 0 ) {
                            rnMask.setVisibility(View.VISIBLE);
                        }
                    }
                });
        return rootView;
    }

    void isLikeAction( boolean liked ) {

        if( G.pets.size() == 0 || counter > G.pets.size() - 1 )
            return;

        int childCount = tinderStackLayout.getChildCount();
        TinderCardView tinderCardView = (TinderCardView) tinderStackLayout.getChildAt(childCount-1);
        tinderCardView.isSpecial = true;

        int x = -G.w * 2;
        int like = 0;
        if( liked == true ) {
            x = G.w * 2;
            like = 1;
        }
        tinderCardView.dismissCard(tinderCardView, x);
        Pet pet1 = G.pets.get(counter);
        G.setFlag( pet1.animal_id, like );
        counter++;

        for(int i=childCount-2; i>=0; i--){
            TinderCardView tc = (TinderCardView) tinderStackLayout.getChildAt(i);
            if(tc != null){
                float scaleValue = 1 - ((childCount-2-i)/50.0f);
                tc.animate()
                        .x(0)
                        .y((childCount-2-i) * tinderStackLayout.yMultiplier)
                        .scaleX(scaleValue)
                        .rotation(0)
                        .setInterpolator(new AnticipateOvershootInterpolator())
                        .setDuration(TinderStackLayout.DURATION);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.vLike:    isLikeAction(true);  break;
            case R.id.vUnlike:  isLikeAction(false); break;
            case R.id.vInfo:
                if( G.pets.size() == 0 || counter > G.pets.size() - 1)
                    return;

                G.pet = G.pets.get(counter);
                MainActivity act = (MainActivity) getActivity();
                InfoFrag frg = (InfoFrag) getFragmentManager().findFragmentByTag( InfoFrag.class.getSimpleName() );
                if( frg == null ) {
                    frg = new InfoFrag();
                }
                else {
                    frg.init();
                }
                act.addFragment( frg );
                break;
        }
    }
}
