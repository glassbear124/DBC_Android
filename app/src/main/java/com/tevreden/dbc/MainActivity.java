package com.tevreden.dbc;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.jkb.slidemenu.SlideMenuLayout;
import com.tevreden.dbc.ui.AboutFrag;
import com.tevreden.dbc.ui.AccountFrag;
import com.tevreden.dbc.ui.CategoryFrag;
import com.tevreden.dbc.ui.ChangePasswordFrag;
import com.tevreden.dbc.ui.ContactUsFrag;
import com.tevreden.dbc.ui.SponseFrag;
import com.tevreden.dbc.ui.StartFrag;
import com.tevreden.dbc.ui.TabOneFragment;
import com.tevreden.dbc.ui.TabTwoFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


//import android.support.v4.app.FragmentTransaction;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //ui
    private SlideMenuLayout slideMenuLayout;
    TextView titleName;
    TabLayout contentTabLayout;
    FrameLayout frameLayout;
    ViewPager contentViewPager;

    View rightMenu;

    ArrayList<String> frgStacks = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initStatusBar();
        initView();
        changeFragment( new CategoryFrag() );
    }

    public void removeFragment() {
        final int size = frgStacks.size();
        if( size < 2 )
            return;

        final String frgLast = frgStacks.get(size-2);

        List<Fragment> list = getSupportFragmentManager().getFragments();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        for( Fragment frg : list ) {
            if (frg == null)
                continue;
            ft.hide(frg);
        }

        ft.commitAllowingStateLoss();

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Fragment frg = getSupportFragmentManager().findFragmentByTag(frgLast);
                frgStacks.remove(size-1);
                if( size == 2 ) {
                    toolbar.setVisibility(View.VISIBLE);
                }
                ft.show(frg);
                ft.commitAllowingStateLoss();
            }
        });
    }

    public void addFragment( final Fragment tarFrg ) {

        List<Fragment> list = getSupportFragmentManager().getFragments();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        for( Fragment frg : list ) {
            if (frg == null)
                continue;
            ft.hide(frg);
        }
        ft.commitAllowingStateLoss();

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                String strId = tarFrg.getClass().getSimpleName();
                Fragment frg1 = getSupportFragmentManager().findFragmentByTag(strId);
                if (frg1 == null) {
                    ft.add( R.id.main_fragment, tarFrg, strId)
                            .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                } else
                    ft.show(frg1);
                frgStacks.add(strId);
                ft.commitAllowingStateLoss();
            }
        });
    }

    Fragment curFrg;

    private void changeFragment(Fragment targetFragment){
        frgStacks.clear();
        frgStacks.add("fragment");
        slideMenuLayout.closeLeftSlide();
        curFrg = targetFragment;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment, targetFragment, "fragment")
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commitAllowingStateLoss();
    }

    Toolbar toolbar;

    private void initView() {
        slideMenuLayout = (SlideMenuLayout) findViewById(R.id.mainSlideMenu);
        titleName = (TextView)findViewById(R.id.title_name);
        frameLayout = (FrameLayout)findViewById(R.id.main_fragment);

        findViewById(R.id.txtClose).setOnClickListener(this);
        RecyclerView leftRecyclerView = (RecyclerView) findViewById(R.id.cml_rv);
        leftRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        SlideLeftAdapter slideLeftAdapter = new SlideLeftAdapter(this);
        leftRecyclerView.setAdapter(slideLeftAdapter);

        contentTabLayout = (TabLayout) findViewById(R.id.am_tab);
        contentViewPager = (ViewPager) findViewById(R.id.am_vp);
        contentTabLayout.addTab(contentTabLayout.newTab());
        contentTabLayout.addTab(contentTabLayout.newTab());
        contentViewPager.setAdapter(new ContentAdapter(getSupportFragmentManager()));
        contentTabLayout.setupWithViewPager(contentViewPager);

        findViewById(R.id.fm_leftMenu).setOnClickListener(this);
        rightMenu = findViewById(R.id.fm_rightMenu);
        rightMenu.setOnClickListener(this);
        contentTabLayout.setVisibility(View.GONE);
        contentViewPager.setVisibility(View.GONE);

        toolbar = (Toolbar) findViewById(R.id.toolBar);
    }

    private void initStatusBar() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return;
        int flag_translucent_status = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        getWindow().setFlags(flag_translucent_status, flag_translucent_status);
    }

    String message, yes, no;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fm_leftMenu:
                slideMenuLayout.toggleLeftSlide();
                break;
            case R.id.fm_rightMenu:

                if( curFrg == null )
                    return;

                String strCurCls = curFrg.getClass().getSimpleName();
                if( strCurCls.compareTo( StartFrag.class.getSimpleName()) == 0 ) {


                    if( G.lang == C.LangEng ) {
                        message = "Do you want to see all animals again?";
                        yes = "Yes"; no = "No";
                    } else if( G.lang == C.LangNed ) {
                        message = "Wil je alle dieren opnieuw zien?";
                        yes = "Ja"; no = "Nee";
                    } else {
                        message = "Bo ke wak tur animal di nobo?";
                        yes = "Si"; no = "No";
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(this)
                            .setMessage(message)
                            .setNegativeButton(no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .setPositiveButton(yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    ((StartFrag)curFrg).reset();
                                }
                            });
                    AlertDialog ok = builder.create();
                    ok.show();


                } else if( strCurCls.compareTo( AccountFrag.class.getSimpleName()) == 0 ) {
                    FragmentManager frgMgr = getSupportFragmentManager();
                    ChangePasswordFrag frg = (ChangePasswordFrag) frgMgr.findFragmentByTag( ChangePasswordFrag.class.getSimpleName() );
                    if( frg == null ) {
                        frg = new ChangePasswordFrag();
                    }
                    else {
                        frg.init();
                    }
                    toolbar.setVisibility(View.GONE);
                    addFragment( frg );
                }
                break;
            case R.id.txtClose:
                slideMenuLayout.closeLeftSlide();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (slideMenuLayout.isLeftSlideOpen() || slideMenuLayout.isRightSlideOpen()) {
            slideMenuLayout.closeLeftSlide();
            slideMenuLayout.closeRightSlide();
        } else {
//            super.onBackPressed();
            if( frgStacks.size() > 1 ) {
                removeFragment();
            } else {
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
            }
        }
    }

    void back() {
        super.onBackPressed();
    }

    public void changeStart() {
        titleName.setText(R.string.app_name);
        showRightButton(View.GONE);
        changeFragment( new StartFrag() );
    }


    public void showRightButton( int visible ) {
        rightMenu.setVisibility(visible);
    }

    public void changeRightButtinIcon( int rId ) {
        rightMenu.setBackgroundResource(rId);
    }

    public class SlideLeftAdapter extends RecyclerView.Adapter<SlideLeftAdapter.ViewHolder> {

        final Context context;
        String [] fiilliste;

        public SlideLeftAdapter(Context context) {
            this.context = context;
            if( G.lang == C.LangEng)
                fiilliste = context.getResources().getStringArray(R.array.left_menu_en);
            else if( G.lang == C.LangNed )
                fiilliste = context.getResources().getStringArray(R.array.left_menu_nd);
            else
                fiilliste = context.getResources().getStringArray(R.array.left_menu_pa);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_friend, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.tvFriend.setText( fiilliste[position] );
            holder.tvFriend.setId(100+position);
        }

        @Override
        public int getItemCount() {
            return fiilliste.length;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvFriend;

            public ViewHolder(View itemView) {
                super(itemView);
                tvFriend = (TextView) itemView.findViewById(R.id.if_tv);
                tvFriend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showRightButton(View.GONE);
                        int id = v.getId();
                        switch (id) {
                            case 100: // start
                                titleName.setText(R.string.app_name);
                                //changeFragment( new StartFrag() );
                                changeFragment( new CategoryFrag() );
                                break;
                            case 101: // favorite
                                titleName.setText( tvFriend.getText() );
                                changeFragment( new TabOneFragment() );
                                break;
                            case 102:
                                titleName.setText( tvFriend.getText() );
                                slideMenuLayout.closeLeftSlide();
                                changeFragment( new TabTwoFragment() );
                                break;
                            case 103: // account
                                titleName.setText( tvFriend.getText() );
                                changeFragment( new AccountFrag() );
                                break;
                            case 104: // about
                                titleName.setText( tvFriend.getText() );
                                changeFragment( new AboutFrag() );
                                break;
                            case 105:  // sponse
                                titleName.setText( tvFriend.getText() );
                                changeFragment( new SponseFrag() );
                                break;
                            case 106:  // contact us
                                titleName.setText("");
                                changeFragment( new ContactUsFrag() );
                                break;
                            case 107:  // logout
                                LoginManager.getInstance().logOut();

                                Intent i = new Intent( MainActivity.this, MenuActivity.class);
                                Bundle b = new Bundle();
                                b.putBoolean("isloaded", true);
                                i.putExtras(b);

                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                                break;
                        }
                    }
                });
            }
        }
    }

    public class ContentAdapter extends FragmentPagerAdapter {

        private String[] menuNames = new String[]{
                "Favorites", "Organization"
        };

        public ContentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return TabOneFragment.newInstance();
            } else {
                return TabTwoFragment.newInstance();
            }
        }

        @Override
        public int getCount() {
            return menuNames.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return menuNames[position];
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        if(resultCode != RESULT_OK)
            return;

        AccountFrag frgAcctoun = (AccountFrag)curFrg;
        if( requestCode == 1 || requestCode == 65537 ) {

            Uri selectedImage = imageReturnedIntent.getData();
            if( selectedImage != null )
                frgAcctoun.setImage(selectedImage);
        } else  {// if( requestCode == 0 ) {

//            Bitmap bitmap = (Bitmap) imageReturnedIntent.getExtras().get("data");
//
//
//            Uri selectedImage = G.imageUri;
//            if( selectedImage == null )
//                selectedImage = imageReturnedIntent.getData();
//
//            if( selectedImage != null )
//                getContentResolver().notifyChange(selectedImage, null);
//
//            ContentResolver cr = getContentResolver();
//            Bitmap bitmap;
            try {
//                bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, selectedImage);
                Bitmap bitmap = (Bitmap) imageReturnedIntent.getExtras().get("data");
                if( bitmap.getWidth() > bitmap.getHeight() ) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(90);
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
                }
                frgAcctoun.setImage(bitmap);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean permissionCheckForStorage() {
        boolean success = true;
        MarshMallowPermission marshMallowPermission = new MarshMallowPermission(this);
        if (Build.VERSION.SDK_INT > 22) {
            if (!marshMallowPermission.checkPermissionForExternalStorage()) {
                if (!marshMallowPermission.checkPermissionForReadExternalStorage()) {
                    success = false;
                    marshMallowPermission.requestPermissionForExternalStorage();
                    marshMallowPermission.requestPermissionForReadExternalStorage();
                }
            }
        }
        return success;
    }

    public boolean permissionCheckForCamera() {
        boolean success = true;
        MarshMallowPermission marshMallowPermission = new MarshMallowPermission(this);
        if (Build.VERSION.SDK_INT > 22) {
            if (!marshMallowPermission.checkPermissionForCamera()) {
                success = false;
                marshMallowPermission.requestPermissionForCamera();
            }
        }
        return success;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case C.REQ_READ_EXTERNAL_STORAGE:
            case C.REQ_WRITE_EXTERNAL_STORAGE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the task you need to do.
//                    new FetchPhotoDetail().execute();
                    permissionCheckForCamera();
                } else {
                    //Toast.makeText(this, "Location permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
                    // permission denied, boo! Disable the functionality that depends on this permission.
                }
                break;

            case C.REQ_CAMERA_CLICK:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the task you need to do.
//                    if (rlImagedialog.getVisibility() == View.VISIBLE) {
//                        closeImagePopupWithAnimation();
//                    } /*else {
//                        openImagePopupWithAnimation();
//                    }*/

                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);//zero can be replaced with any action code

                } else {
                    //Toast.makeText(this, "Location permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
                    // permission denied, boo! Disable the functionality that depends on this permission.
                }
                break;
            case C.REQ_CALL_PHONE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the task you need to do.
//                    call();
                } else {
                    //Toast.makeText(this, "Location permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
                    // permission denied, boo! Disable the functionality that depends on this permission.
                }
                break;
            default:
                break;
        }
    }

}
