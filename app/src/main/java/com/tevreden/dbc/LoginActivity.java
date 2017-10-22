package com.tevreden.dbc;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tevreden.dbc.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText editUserName, editPassword;
    View txtLogin, txtForgotPassword, txtFB;

    LoginButton loginButton;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");

        loginButton.registerCallback(callbackManager,
                new FacebookCallback< LoginResult >() {@Override
                public void onSuccess(final LoginResult loginResult) {

                    GraphRequest request = GraphRequest.newMeRequest(
                            loginResult.getAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {@Override
                            public void onCompleted(JSONObject object, GraphResponse response) {

                                try {
                                    String id = object.getString("id");
                                    String first_name = object.getString("first_name");
                                    String last_name = object.getString("last_name");
                                    String email = object.getString("email");
                                    login( id, first_name, last_name, "user" + id, email );

                                } catch (JSONException e) {
                                    G.showToast( LoginActivity.this, "Fail facebook login");
                                    e.printStackTrace();
                                }
                            }
                            });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id, first_name, last_name, email");
                    request.setParameters(parameters);
                    request.executeAsync();
                }

                    @Override
                    public void onCancel() {
                        System.out.println("onCancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        System.out.println("onError");
                        Log.v("LoginActivity", exception.getCause().toString());
                    }
                });

        initView();
    }

    private void login(final String fb_id, final String first_name, final String last_name,
                       final String username, final String email ) {

        final Activity act = LoginActivity.this;
        progDlg.setMessage("Login...");
        progDlg.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progDlg.show();

        String url = C.baseUrl + "apis/auth/fb_signin";
        StringRequest loginRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        JsonElement jsonElement = AppController.getInstance().getGson().fromJson(response, JsonElement.class);
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        progDlg.dismiss();

                        int err = jsonObject.get("errorcode").getAsInt();
                        if( err > 0 ) {
                            G.showToast( act, "The Facebook login has failed.");
                        } else {
                            JsonElement result = jsonObject.get("result");
                            G.curUser = AppController.getInstance().getGson().fromJson(result.toString(), User.class);
                            G.showToast(act, "Welcome to the DBC app!");

                            G.curUser.username = username;

                            Intent intent = new Intent(act, LangActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progDlg.dismiss();
                        G.showToast( LoginActivity.this, "The login has failed.");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("fb_id", fb_id);
                params.put("first_name", first_name);
                params.put("last_name", last_name);
                params.put("username", username);
                params.put("email", email);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(loginRequest);
    }

    ProgressDialog progDlg;
    private void login(final String uname, final String password) {

        final Activity act = LoginActivity.this;
        progDlg.setMessage("Login...");
        progDlg.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progDlg.show();

        String url = C.baseUrl + "apis/auth/signin";
        StringRequest loginRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        JsonElement jsonElement = AppController.getInstance().getGson().fromJson(response, JsonElement.class);
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        progDlg.dismiss();

                        int err = jsonObject.get("errorcode").getAsInt();
                        if( err > 0 ) {
                            G.showToast( LoginActivity.this, "Wrong username and/or password.");
                        } else {
                            JsonElement result = jsonObject.get("result");
                            G.curUser = AppController.getInstance().getGson().fromJson(result.toString(), User.class);

                            G.curUser.username = uname;
                            G.showToast(LoginActivity.this, "Welcome to the Pet Cupid app!");

                            SharedPreferences.Editor editor = G.pref.edit();
                            editor.putBoolean("login", true);
                            editor.putString("signed_name", uname);
                            editor.putString("signed_pw", password);
                            editor.commit();

                            Intent intent = new Intent(act, LangActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progDlg.dismiss();
                        G.showToast( LoginActivity.this, "The login has failed.");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("username", uname);
                params.put("password", password);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(loginRequest);
    }

    private void initView() {
        editUserName = (EditText)findViewById(R.id.editUserName);
        editPassword = (EditText)findViewById(R.id.editPassword);
        txtForgotPassword = findViewById(R.id.txtForgotPassword);
        txtForgotPassword.setOnClickListener(this);

        txtLogin = findViewById(R.id.txtLogin); txtLogin.setOnClickListener(this);
        txtFB = findViewById(R.id.txtFB); txtFB.setOnClickListener(this);
        progDlg = new ProgressDialog( this );

        initStatusBar();
    }

    private void initStatusBar() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return;
        int flag_translucent_status = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        getWindow().setFlags(flag_translucent_status, flag_translucent_status);
    }

    void login() {
        if( editUserName.getText().toString().length() == 0 ) {
            G.showToast(this, "Please enter your username.");
            return;
        }

        if( editPassword.getText().toString().length() == 0 ) {
            G.showToast(this, "Please enter your password.");
            return;
        }
        login( editUserName.getText().toString(), editPassword.getText().toString() );
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtForgotPassword:
                Intent i = new Intent(LoginActivity.this, ForgotActivity.class);
                startActivity(i);
                break;
            case R.id.txtLogin:
               login();
//.              login( "glassbear", "123456" );
                break;
            case R.id.txtFB:
                loginButton.performClick();
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent data) {
        super.onActivityResult(requestCode, responseCode, data);
        callbackManager.onActivityResult(requestCode, responseCode, data);
    }
}
