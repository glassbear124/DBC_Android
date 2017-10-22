package com.tevreden.dbc.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.tevreden.dbc.model.Pet;
import com.tevreden.dbc.tinder.ui.TinderCardView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TabOneFragment extends Fragment {

    public static TabOneFragment newInstance() {
        Bundle args = new Bundle();
        TabOneFragment fragment = new TabOneFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private Context mContext;
    ProgressDialog progDlg;
    public ArrayList<Pet> fevorites = new ArrayList<Pet>();

    private void getFevorite() {

        progDlg.setMessage("");
        progDlg.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progDlg.show();

        fevorites.clear();
        String url = C.baseUrl + "apis/animals/favoritos";
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
                                fevorites.add(pet);
                            }

                            if( fevorites.size() > 0) {
                                recyclerView.setVisibility(View.VISIBLE);
                                txtHavenot.setVisibility(View.GONE);
                                tabOneAdapter.notifyDataSetChanged();
                            } else {
                                txtHavenot.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
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

    TabOneAdapter tabOneAdapter;
    RecyclerView recyclerView;
    TextView txtHavenot;
    private Paint p = new Paint();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frg_tab_one, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.ff_rv_one);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        tabOneAdapter = new TabOneAdapter(mContext);
        recyclerView.setAdapter(tabOneAdapter);
        txtHavenot = (TextView)rootView.findViewById(R.id.txtHavenot);
        txtHavenot.setVisibility(View.GONE);
        if( G.lang == C.LangEng ) {
            txtHavenot.setText( R.string.havenot_fevorite );
        } else if( G.lang == C.LangNed ) {
            txtHavenot.setText( R.string.havenot_fevorite_nd );
        } else
            txtHavenot.setText( R.string.havenot_fevorite_pa );
        progDlg = new ProgressDialog(getContext());
        initSwipe();
        getFevorite();
        return rootView;
    }

    void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT ) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                tabOneAdapter.removeItem(position);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    p.setColor(Color.parseColor("#D32F2F"));
                    RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(), (float) itemView.getBottom());
                    c.drawRect(background,p);
                    icon = BitmapFactory.decodeResource(getResources(), R.drawable.btn_x);
                    RectF icon_dest = new RectF((float) itemView.getRight() - 2*width ,(float) itemView.getTop() + width,(float) itemView.getRight() - width,(float)itemView.getBottom() - width);
                    c.drawBitmap(icon,null,icon_dest,p);
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public class TabOneAdapter extends RecyclerView.Adapter<TabOneAdapter.ViewHolder> {

        private Context context;

        public TabOneAdapter(Context context) {
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_tab_one, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            Pet pet = fevorites.get(position);
            if( pet.picture != null && pet.picture.length() > 0 ) {
                Picasso.with(context).load(pet.picture).into(holder.imgAvatar);
            }
            holder.tvFriend.setText(pet.name);
            holder.tvFriend.setId(position);
            holder.txtComment.setText(pet.getSex());
        }

        public void removeItem(int position) {

            Pet pet = fevorites.get(position);
            G.setFlag(pet.animal_id, -1); // delete

            fevorites.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, fevorites.size());

            if( fevorites.size() == 0 ) {
                recyclerView.setVisibility(View.GONE);
                txtHavenot.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            return fevorites.size();
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
                        G.pet = fevorites.get(tvFriend.getId());
                        MainActivity act = (MainActivity) getActivity();
                        InfoFrag frg = (InfoFrag) getFragmentManager().findFragmentByTag( InfoFrag.class.getSimpleName() );
                        if( frg == null ) {
                            frg = new InfoFrag();
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
