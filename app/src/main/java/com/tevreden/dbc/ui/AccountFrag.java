package com.tevreden.dbc.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;
import com.tevreden.dbc.AppController;
import com.tevreden.dbc.AppHelper;
import com.tevreden.dbc.C;
import com.tevreden.dbc.G;
import com.tevreden.dbc.MainActivity;
import com.tevreden.dbc.R;
import com.tevreden.dbc.VolleyMultipartRequest;
import com.tevreden.dbc.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountFrag extends Fragment implements View.OnClickListener{

    CircleImageView imgAvatar;
    EditText editEmail, editPhone, editFirstName, editLastName;
    View lnEditName;
    TextView txtName, txtEdit, txtCancel;
    TextView txtUserName;
    View cameraRoll, vEditPhoto;

    boolean isEnableEdit = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frg_my_account, container, false);

        MainActivity act = (MainActivity)getActivity();
        act.changeRightButtinIcon(R.drawable.change_password);

        imgAvatar = (CircleImageView)rootView.findViewById(R.id.imgAvatar);
        imgAvatar.setOnClickListener(this);

        vEditPhoto = rootView.findViewById(R.id.vEditPhoto);

        txtName = (TextView)rootView.findViewById(R.id.txtName);
        lnEditName = rootView.findViewById(R.id.lnEditName);

        txtUserName = (TextView)rootView.findViewById(R.id.txtUserName);

        editFirstName = (EditText)rootView.findViewById(R.id.editFirstName);
        editLastName = (EditText)rootView.findViewById(R.id.editLastName);

        editEmail = (EditText)rootView.findViewById(R.id.editEmail);
        editPhone = (EditText)rootView.findViewById(R.id.editPhone);

        txtEdit = (TextView)rootView.findViewById(R.id.txtEdit);
        txtEdit.setOnClickListener(this);

        txtCancel = (TextView)rootView.findViewById(R.id.txtCancel);
        txtCancel.setOnClickListener(this);

        if(G.curUser != null) {

            if( G.curUser.avatar != null && G.curUser.avatar.length() > 0 )
                Picasso.with( getContext() ).load(G.curUser.avatar).into(imgAvatar);

            txtUserName.setText( G.curUser.username );

            txtName.setText( G.curUser.first_name + " " + G.curUser.last_name);
            editFirstName.setText(G.curUser.first_name);
            editLastName.setText(G.curUser.last_name);

            editEmail.setText(G.curUser.email);
            editPhone.setText(G.curUser.phone);
        }

        rootView.findViewById(R.id.imgGallery).setOnClickListener(this);
        rootView.findViewById(R.id.imgCamera).setOnClickListener(this);
        rootView.findViewById(R.id.imgClose).setOnClickListener(this);

        cameraRoll = rootView.findViewById(R.id.cameraRoll);

        progDlg = new ProgressDialog( getActivity() );

        if( G.lang == C.LangEng ) {
            txtEdit.setText(R.string.edit);
            txtCancel.setText(R.string.cancel);

            editFirstName.setHint(R.string.first_name);
            editLastName.setHint(R.string.last_name);
            editPhone.setHint(R.string.phone_number);
            editEmail.setHint(R.string.email);

        } else if( G.lang == C.LangNed ) {
            txtEdit.setText(R.string.edit_nd);
            txtCancel.setText(R.string.cancel_nd);
            editFirstName.setHint(R.string.first_name_nd);
            editLastName.setHint(R.string.last_name_nd);
            editPhone.setHint(R.string.phone_number_nd);
            editEmail.setHint(R.string.email_nd);

        }
        else {
            txtEdit.setText(R.string.edit_pa);
            txtCancel.setText(R.string.cancel_pa);
            editFirstName.setHint(R.string.first_name_pa);
            editLastName.setHint(R.string.last_name_pa);
            editPhone.setHint(R.string.phone_number_pa);
            editEmail.setHint(R.string.email_pa);
        }
        return rootView;
    }

    private void change_avatar() {

        String url = C.baseUrl + "apis/profile/change_avatar";
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                try {
                    JSONObject result = new JSONObject(resultResponse);

                    int errorcode = result.getInt("errorcode");
                    String message = result.getString("messages");

                    JSONObject objResult = result.getJSONObject("result");
                    String url = objResult.getString("avatar");
                    if( url != null && url.length() > 0 )
                        G.curUser.avatar = url;

                    if( errorcode == 1 ) {
                        G.showToast( getActivity(), message);
                    }

                    if( G.curUser.avatar != null && G.curUser.avatar.length() > 0 )
                        Picasso.with( getContext() ).load(G.curUser.avatar).into(imgAvatar);
                    else
                        imgAvatar.setImageResource(R.drawable.icn_user_placeholder);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        String status = response.getString("status");
                        String message = response.getString("message");

                        Log.e("Error Status", status);
                        Log.e("Error Message", message);

                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message+" Please login again";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message+ " Check your inputs";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message+" Something is getting wrong";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("apikey", G.curUser.apikey);
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView
                imgAvatar.getDrawable();

                Drawable drawable =  resize(imgAvatar.getDrawable());
                params.put("avatar", new DataPart("file_avatar.jpg", AppHelper.getFileDataFromDrawable(getContext(), drawable), "image/jpeg"));
                return params;
            }
        };
        VolleySingleton.getInstance(getContext()).addToRequestQueue(multipartRequest);
    };


    private Drawable resize(Drawable image) {
        Bitmap b = ((BitmapDrawable)image).getBitmap();

        int w = b.getWidth();
        int h = b.getHeight();

        int newH = (int)(h * (300.0f/w));
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, 300, newH, false);
        return new BitmapDrawable(getResources(), bitmapResized);
    }

    ProgressDialog progDlg;
    private void save_profile(final String firstName, final String lastName, final String email, final String phone) {

        progDlg.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progDlg.show();

        String url = C.baseUrl + "apis/profile/edit";
        StringRequest loginRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        JsonElement jsonElement = AppController.getInstance().getGson().fromJson(response, JsonElement.class);
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        progDlg.dismiss();

//                        int err = jsonObject.get("errorcode").getAsInt();
//                        if( err > 0 ) {
//                            int errId = G.getErrorMessage(errorcode);
//                            G.alertDisplayer(act, G.str( act, R.string.app_name), G.str(act, errId));
//                            requestFocus(editUserName);
//                        } else {

                            G.curUser.first_name = firstName;
                            G.curUser.last_name = lastName;
                            G.curUser.email = email;
                            G.curUser.phone = phone;

                            if( G.lang == C.LangEng ) {
                                txtEdit.setText(R.string.edit);
                            } else if( G.lang == C.LangNed )
                                txtEdit.setText(R.string.edit_nd);
                            else
                                txtEdit.setText(R.string.edit_pa);

                            txtName.setVisibility(View.VISIBLE);
                            txtName.setText( firstName + " " + lastName );
                            lnEditName.setVisibility(View.GONE);

                            txtCancel.setVisibility(View.GONE);
                            vEditPhoto.setVisibility(View.GONE);

                            MainActivity act = (MainActivity)getActivity();
                            act.showRightButton(View.GONE);
//                        }
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
                params.put("first_name", firstName);
                params.put("last_name", lastName);
                params.put("email", email);
                params.put("phone", phone);

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(loginRequest);
    }

    public Uri imageUri;

    @Override
    public void onClick(View v) {

        MainActivity act = (MainActivity)getActivity();

        switch (v.getId()) {
            case R.id.imgGallery:
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                getActivity().startActivityForResult(pickPhoto , 1);//one can be replaced with any action code

//                Intent intent = new Intent();
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                pickPhoto.setType("image/*");
//                getActivity().startActivityForResult(intent, 1);
                break;
            case R.id.imgCamera:

                if( act.permissionCheckForCamera() == true ) {
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);//zero can be replaced with any action code
                }

//                String manufacturer = Build.MANUFACTURER;
//                String s = manufacturer.toLowerCase();
//                if( manufacturer.toLowerCase().compareTo("samsung") == 0 ) {
//                    if( act.permissionCheckForCamera() == true ) {
//                        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                        startActivityForResult(takePicture, 0);//zero can be replaced with any action code
//                    }
//                } else {
//                    if (act.permissionCheckForCamera() && act.permissionCheckForStorage() ) {
////                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
////                        File photo = new File(Environment.getExternalStorageDirectory(), "Pic.jpg");
////                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
////                        imageUri = Uri.fromFile(photo);
////                        startActivityForResult(intent, 0);
//
//                        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                        startActivityForResult(takePicture, 0);//zero can be replaced with any action code
//
//                    }
//                }

//                if (permissionCheckForCamera()) {
//                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    startActivityForResult(takePicture, 0);//zero can be replaced with any action code
//                }



//                if ( act.permissionCheckForStorage()) {
//                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    File photo = new File(Environment.getExternalStorageDirectory(), "Pic.jpg");
//                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
//                    imageUri = Uri.fromFile(photo);
//                    startActivityForResult(intent, 0);
//                }
                break;
            case R.id.imgClose:
                cameraRoll.setVisibility(View.GONE);
                break;
            case R.id.imgAvatar:

                if( isEnableEdit == false )
                    return;

                cameraRoll.setVisibility(View.VISIBLE);
                break;
            case R.id.txtCancel:
                isEnableEdit = false;

                editEmail.setEnabled( isEnableEdit );
                editPhone.setEnabled( isEnableEdit );
                vEditPhoto.setVisibility(View.GONE);

                if( G.lang == C.LangEng ) {
                    txtEdit.setText(R.string.edit);
                } else if( G.lang == C.LangNed )
                    txtEdit.setText(R.string.edit_nd);
                else
                    txtEdit.setText(R.string.edit_pa);

                txtName.setVisibility(View.VISIBLE);
                lnEditName.setVisibility(View.GONE);

                editFirstName.setText(G.curUser.first_name);
                editLastName.setText(G.curUser.last_name);
                txtName.setText( G.curUser.first_name + " " + G.curUser.last_name );
                editEmail.setText(G.curUser.email);
                editPhone.setText(G.curUser.phone);

                txtCancel.setVisibility(View.GONE);
                act.showRightButton(View.GONE);
                break;

            case R.id.txtEdit:
                isEnableEdit = !isEnableEdit;
                if( isEnableEdit == true ) {
                    vEditPhoto.setVisibility(View.VISIBLE);
                    txtCancel.setVisibility(View.VISIBLE);
                    if( G.lang == C.LangEng ) {
                        txtEdit.setText(R.string.save);
                    } else if( G.lang == C.LangNed )
                        txtEdit.setText(R.string.save_nd);
                    else
                        txtEdit.setText(R.string.save_pa);

                    txtName.setVisibility(View.GONE);
                    lnEditName.setVisibility(View.VISIBLE);

                    act.showRightButton(View.VISIBLE);

                } else {
                    save_profile( editFirstName.getText().toString(), editLastName.getText().toString(),
                            editEmail.getText().toString(), editPhone.getText().toString() );
                }

                editEmail.setEnabled( isEnableEdit );
                editPhone.setEnabled( isEnableEdit );
                break;
        }
    }

    public void setImage(Uri selectedImage ) {
        imgAvatar.setImageURI(selectedImage);
        change_avatar();
        cameraRoll.setVisibility(View.GONE);
    }

    public void setImage(Bitmap bmp) {
        if( bmp == null )
            return;
        imgAvatar.setImageBitmap(bmp);
        change_avatar();
        cameraRoll.setVisibility(View.GONE);
    }
}
