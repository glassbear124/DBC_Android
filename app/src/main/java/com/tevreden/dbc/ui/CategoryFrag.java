package com.tevreden.dbc.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tevreden.dbc.C;
import com.tevreden.dbc.G;
import com.tevreden.dbc.MainActivity;
import com.tevreden.dbc.R;

public class CategoryFrag extends Fragment implements View.OnClickListener {

    public static CategoryFrag newInstance() {
        Bundle args = new Bundle();
        CategoryFrag fragment = new CategoryFrag();
        fragment.setArguments(args);
        return fragment;
    }

    TextView txtWantSee;
    ImageView imgDog, imgCat;
    View lnBg;

    void loadLang() {

        if( G.lang == C.LangEng ) {
            txtWantSee.setText(R.string.want_to_see);
        }
        else if( G.lang == C.LangNed ) {
            txtWantSee.setText(R.string.want_to_see_nd);
        }
        else {
            txtWantSee.setText(R.string.want_to_see_pa);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frg_category, container, false);

        lnBg = rootView.findViewById(R.id.lnBg);
        txtWantSee = (TextView)rootView.findViewById(R.id.txtWantSee);
        imgDog = (ImageView)rootView.findViewById(R.id.imgDog); imgDog.setOnClickListener(this);
        imgCat = (ImageView)rootView.findViewById(R.id.imgCat); imgCat.setOnClickListener(this);

        loadLang();
//        loadBg();

        return rootView;
    }

    void loadBg() {

        if( G.like == C.LikeDog ) {
  //          imgDog.setImageResource(R.drawable.icn_dog_1);
            imgCat.setImageResource(R.drawable.icn_cat_0);
        }
        else {
            imgDog.setImageResource(R.drawable.icn_dog_0);
//            imgCat.setImageResource(R.drawable.icn_cat_1);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgDog:
                G.like = C.LikeDog;
//                loadBg();
                break;
            case R.id.imgCat:
                G.like = C.LikeCat;
 //               loadBg();
                break;
        }

        MainActivity act = (MainActivity) getActivity();
        act.changeStart();
    }
}
