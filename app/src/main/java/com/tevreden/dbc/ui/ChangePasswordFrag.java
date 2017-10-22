package com.tevreden.dbc.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tevreden.dbc.AppController;
import com.tevreden.dbc.C;
import com.tevreden.dbc.G;
import com.tevreden.dbc.MainActivity;
import com.tevreden.dbc.R;
import com.tevreden.dbc.SignupActivity;

import java.util.HashMap;
import java.util.Map;

public class ChangePasswordFrag extends Fragment implements View.OnClickListener {

    public static ChangePasswordFrag newInstance() {
        Bundle args = new Bundle();
        ChangePasswordFrag fragment = new ChangePasswordFrag();
        fragment.setArguments(args);
        return fragment;
    }

    EditText editPassword1, editPassword2;
    TextView txtReset;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frg_change_pw, container, false);

        rootView.findViewById(R.id.vBack).setOnClickListener(this);

        editPassword1 = (EditText)rootView.findViewById(R.id.editPassword1);
        editPassword2 = (EditText)rootView.findViewById(R.id.editPassword2);

        txtReset = (TextView)rootView.findViewById(R.id.txtReset);
        txtReset.setOnClickListener(this);

        progDlg = new ProgressDialog(getContext());

        init();
        return rootView;
    }

    public void init() {

        if( G.lang == C.LangEng ) {
            txtReset.setText(R.string.reset);
            editPassword1.setHint(R.string.new_pw);
            editPassword2.setHint(R.string.confirm_pwd);
        } else if( G.lang == C.LangPap ) {
            txtReset.setText(R.string.reset_pa);
            editPassword1.setHint(R.string.new_pw_pa);
            editPassword2.setHint(R.string.confirm_pwd_pa);
        } else {
            txtReset.setText(R.string.reset_nd);
            editPassword1.setHint(R.string.new_pw_nd);
            editPassword2.setHint(R.string.confirm_pwd_nd);
        }
    }

    ProgressDialog progDlg;
    private void changePassword(final String password ) {

        progDlg.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progDlg.show();

        String url = C.baseUrl + "apis/profile/change_password";
        StringRequest loginRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        JsonElement jsonElement = AppController.getInstance().getGson().fromJson(response, JsonElement.class);
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        progDlg.dismiss();

                        int err = jsonObject.get("errorcode").getAsInt();
                        if( err > 0 ) {
                            G.showToast( getContext(), "The password change has failed.");
                        } else {
                            G.showToast( getContext(), "The password change has been completed." );
//                            MainActivity act = (MainActivity)getContext();
//                            act.removeFragment();
                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progDlg.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("apikey", G.curUser.apikey);
                params.put("new_pwd", password);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(loginRequest);
    }

    @Override
    public void onClick(View v) {
        MainActivity act = (MainActivity)getActivity();
        switch (v.getId()) {
            case R.id.vBack:
                act.removeFragment();
                break;

            case R.id.txtReset:

                if( editPassword1.length() == 0 ) {
                    G.showToast( getActivity(), "Please enter your password.");
                    return;
                }

                if( editPassword2.length() == 0 ) {
                    G.showToast( getActivity(), "Please confirm the password.");
                    return;
                }

                String pass1 = editPassword1.getText().toString();
                String pass2 = editPassword2.getText().toString();
                if( pass1.compareTo(pass2) != 0 ) {
                    G.showToast( getActivity(), "The password is incorrect.");
                    return;
                }
                changePassword(pass1);
                break;
        }
    }
}
