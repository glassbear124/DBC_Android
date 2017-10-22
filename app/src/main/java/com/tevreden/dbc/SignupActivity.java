package com.tevreden.dbc;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tevreden.dbc.model.User;

import java.util.HashMap;
import java.util.Map;


public class SignupActivity extends Activity implements View.OnClickListener {

    EditText editUserName, editPassword, editEmail, editConfirmPassword;
    View txtSignup;

    ProgressDialog progDlg;

    private void signup(final String uname, final String email, final String password) {

        progDlg.setMessage("Sign Up...");
        progDlg.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progDlg.show();

        String url = C.baseUrl + "apis/auth/signup";
        StringRequest loginRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        JsonElement jsonElement = AppController.getInstance().getGson().fromJson(response, JsonElement.class);
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        progDlg.dismiss();

                        int err = jsonObject.get("errorcode").getAsInt();
                        if( err > 0 ) {
                            G.showToast( SignupActivity.this, "The Sign Up process has failed.");
                        } else {
                            JsonElement result = jsonObject.get("result");
                            G.showToast(SignupActivity.this, "Thank you for Signing up, you can login now.");
                            onBackPressed();
                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progDlg.dismiss();
                        G.showToast( SignupActivity.this, "Sign Up fail");
//                        G.alertDisplayer(act, G.str(act, R.string.app_name), G.str(act, R.string.db_error));
//                        requestFocus(editUserName);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("username", uname);
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(loginRequest);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initView();
    }

    private void initView() {
        editUserName = (EditText)findViewById(R.id.editUserName);
        editEmail = (EditText)findViewById(R.id.editEmail);
        editPassword = (EditText)findViewById(R.id.editPassword);
        editConfirmPassword = (EditText)findViewById(R.id.editConfirmPassword);
        txtSignup = findViewById(R.id.txtSignup); txtSignup.setOnClickListener(this);

        progDlg = new ProgressDialog(this);
        initStatusBar();
    }

    private void initStatusBar() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return;
        int flag_translucent_status = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        getWindow().setFlags(flag_translucent_status, flag_translucent_status);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtSignup:
                if( editUserName.length() == 0 ) {
                    G.showToast(SignupActivity.this, "Please enter your username");
                    return;
                }

                if( editEmail.length() == 0 ) {
                    G.showToast(SignupActivity.this, "Please enter your email.");
                    return;
                }

                if( editPassword.length() == 0 ) {
                    G.showToast(SignupActivity.this, "Please enter your password.");
                    return;
                }

                if( editConfirmPassword.length() == 0 ) {
                    G.showToast(SignupActivity.this, "Please confirm the password.");
                    return;
                }

                String pw1 = editPassword.getText().toString();
                String pw2 = editConfirmPassword.getText().toString();

                if( pw1.compareTo(pw2) != 0 ) {
                    G.showToast(SignupActivity.this, "The password cannot be confirmed.");
                    return;
                }

                signup( editUserName.getText().toString(), editEmail.getText().toString(), pw1 );
                break;
        }
    }
}
