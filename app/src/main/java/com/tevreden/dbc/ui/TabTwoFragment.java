package com.tevreden.dbc.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.tevreden.dbc.model.Organization;
import com.tevreden.dbc.model.Pet;

import java.util.HashMap;
import java.util.Map;

public class TabTwoFragment extends Fragment {

    public static TabTwoFragment newInstance() {
        Bundle args = new Bundle();
        TabTwoFragment fragment = new TabTwoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void getOrganization() {

        progDlg.setMessage("");
        progDlg.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progDlg.show();

        G.orgs.clear();
        String url = C.baseUrl + "apis/orgs";
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
                                Organization org = gSon.fromJson(element.toString(), Organization.class);
                                if( org.name.contains("Dierenbescherming") == true )
                                    G.orgs.add(0, org);
                                else
                                    G.orgs.add(org);
                            }
                            tabTwoAdapter.notifyDataSetChanged();
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

    //ui
    private Context mContext;
    ProgressDialog progDlg;
    TabTwoAdapter tabTwoAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frg_tab_two, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.ff_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        tabTwoAdapter = new TabTwoAdapter(mContext);
        recyclerView.setAdapter(tabTwoAdapter);

        progDlg = new ProgressDialog(getContext());
        getOrganization();
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public class TabTwoAdapter extends RecyclerView.Adapter<TabTwoAdapter.ViewHolder> {

        private Context context;

        public TabTwoAdapter(Context context) {
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_tab_two, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Organization org = G.orgs.get(position);
            if( org.logo != null && org.logo.length() > 0 ) {
                Picasso.with(context).load(org.logo).into(holder.imgAvatar);
            }
            holder.tvFriend.setText(org.name);
            holder.tvFriend.setId(position);
            holder.txtComment.setText(org.contact_pers);
        }

        @Override
        public int getItemCount() {
            return G.orgs.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView tvFriend;
            ImageView imgAvatar;
            TextView txtComment;

            public ViewHolder(View itemView) {
                super(itemView);
                tvFriend = (TextView) itemView.findViewById(R.id.txtName);
                imgAvatar = (ImageView)itemView.findViewById(R.id.imgAvatar);
                txtComment = (TextView)itemView.findViewById(R.id.txtComment);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        G.org = G.orgs.get( tvFriend.getId() );
                        MainActivity act = (MainActivity) getActivity();
                        OrgDetailFrag frg = (OrgDetailFrag) getFragmentManager().findFragmentByTag( OrgDetailFrag.class.getSimpleName() );
                        if( frg == null ) {
                            frg = new OrgDetailFrag();
                        }
                        else {
                            frg.init();
                        }
                        act.addFragment( frg );
                    }
                });
            }
        }
    }
}
