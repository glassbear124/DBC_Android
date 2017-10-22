package com.tevreden.dbc.tinder.ui;

import android.animation.Animator;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.tevreden.dbc.C;
import com.tevreden.dbc.G;
import com.tevreden.dbc.R;
import com.tevreden.dbc.tinder.bus.RxBus;
import com.tevreden.dbc.tinder.bus.events.TopCardMovedEvent;
import com.tevreden.dbc.model.Pet;
import com.tevreden.dbc.tinder.utilities.DisplayUtility;
import com.squareup.picasso.Picasso;

public class TinderCardView extends FrameLayout implements View.OnTouchListener {

    // region Constants
    private static final float CARD_ROTATION_DEGREES = 40.0f;
    private static final float BADGE_ROTATION_DEGREES = 15.0f;
    private static final int DURATION = 300;
    // endregion

    public boolean isSpecial = false;

    // region Views
    private ImageView imageView;
    private TextView txtName;
    private TextView txtSex;
    private TextView txtAge;
    private TextView likeTextView;
    private TextView nopeTextView;
    // endregion

    // region Member Variables
    private float oldX;
    private float oldY;
    private float newX;
    private float newY;
    public float dX;
    public float dY;
    private float rightBoundary;
    private float leftBoundary;
    private int screenWidth;
    private int padding;
    // endregion

    // region Constructors
    public TinderCardView(Context context) {
        super(context);
        init(context, null);
    }

    public TinderCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public TinderCardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }
    // endregion

    // region View.OnTouchListener Methods
    @Override
    public boolean onTouch(final View view, MotionEvent motionEvent) {
        TinderStackLayout tinderStackLayout = ((TinderStackLayout)view.getParent());
        TinderCardView topCard = (TinderCardView) tinderStackLayout.getChildAt(tinderStackLayout.getChildCount()-1);
        if(topCard.equals(view)){
            switch(motionEvent.getAction()){
                case MotionEvent.ACTION_DOWN:
                    oldX = motionEvent.getX();
                    oldY = motionEvent.getY();

                    // cancel any current animations
                    view.clearAnimation();
                    return true;
                case MotionEvent.ACTION_UP:
                    if(isCardBeyondLeftBoundary(view)){
                        RxBus.getInstance().send(new TopCardMovedEvent(-(screenWidth)));
                        dismissCard(view, -(screenWidth * 2));
                    } else if(isCardBeyondRightBoundary(view)){
                        RxBus.getInstance().send(new TopCardMovedEvent(screenWidth));
                        dismissCard(view, (screenWidth * 2));
                    } else {
                        RxBus.getInstance().send(new TopCardMovedEvent(0));
                        resetCard(view);
                    }
                    return true;
                case MotionEvent.ACTION_MOVE:
                    newX = motionEvent.getX();
                    newY = motionEvent.getY();

                    dX = newX - oldX;
                    dY = newY - oldY;

                    float posX = view.getX() + dX;

                    RxBus.getInstance().send(new TopCardMovedEvent(posX));

                    // Set new position
                    view.setX(view.getX() + dX);
                    view.setY(view.getY() + dY);

                    setCardRotation(view, view.getX());

                    updateAlphaOfBadges(posX);
                    return true;
                default :
                    return super.onTouchEvent(motionEvent);
            }
        }
        return super.onTouchEvent(motionEvent);
    }
    // endregion

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        setOnTouchListener(null);
    }

    // region Helper Methods
    private void init(Context context, AttributeSet attrs) {
        if (!isInEditMode()) {
            inflate(context, R.layout.tinder_card, this);

            imageView = (ImageView) findViewById(R.id.iv);
            txtName = (TextView) findViewById(R.id.txtName);
            txtSex = (TextView) findViewById(R.id.txtSex);
            txtAge = (TextView) findViewById(R.id.txtAge);

            likeTextView = (TextView) findViewById(R.id.like_tv);
            nopeTextView = (TextView) findViewById(R.id.nope_tv);

            likeTextView.setRotation(-(BADGE_ROTATION_DEGREES));
            nopeTextView.setRotation(BADGE_ROTATION_DEGREES);

            screenWidth = DisplayUtility.getScreenWidth(context);
            leftBoundary =  screenWidth * (1.0f/6.0f); // Left 1/6 of screen
            rightBoundary = screenWidth * (5.0f/6.0f); // Right 1/6 of screen
            padding = DisplayUtility.dp2px(context, 16);

            setOnTouchListener(this);
        }
    }

    // Check if card's middle is beyond the left boundary
    private boolean isCardBeyondLeftBoundary(View view){
        return (view.getX() + (view.getWidth() / 2) < leftBoundary);
    }

    // Check if card's middle is beyond the right boundary
    private boolean isCardBeyondRightBoundary(View view){
        return (view.getX() + (view.getWidth() / 2) > rightBoundary);
    }

    public void dismissCard(final View view, int xPos){
        view.animate()
                .x(xPos)
                .y(0)
                .setInterpolator(new AccelerateInterpolator())
                .setDuration(DURATION)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        ViewGroup viewGroup = (ViewGroup) view.getParent();
                        if(viewGroup != null) {
                            viewGroup.removeView(view);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
    }

    public void resetCard(View view){
        view.animate()
                .x(0)
                .y(0)
                .rotation(0)
                .setInterpolator(new OvershootInterpolator())
                .setDuration(DURATION);

        likeTextView.setAlpha(0);
        nopeTextView.setAlpha(0);
    }

    private void setCardRotation(View view, float posX){
        float rotation = (CARD_ROTATION_DEGREES * (posX)) / screenWidth;
        int halfCardHeight = (view.getHeight() / 2);
        if(oldY < halfCardHeight - (2*padding)){
            view.setRotation(rotation);
        } else {
            view.setRotation(-rotation);
        }
    }

    // set alpha of like and nope badges
    private void updateAlphaOfBadges(float posX){
        float alpha = (posX - padding) / (screenWidth * 0.50f);
        likeTextView.setAlpha(alpha);
        nopeTextView.setAlpha(-alpha);
    }

    public void bind(Pet user){
        if(user == null)
            return;

        setUpImage(imageView, user);
        setUpName(txtName, user);
        setUpSex(txtSex, user);
        setUpAge(txtAge, user);
    }

    private void setUpImage(ImageView iv, Pet pet){

        if(!TextUtils.isEmpty(pet.picture)){
            Picasso.with(iv.getContext())
                    .load(pet.picture)
                    .into(iv);
        }
    }

    private void setUpName(TextView tv, Pet pet){

        if(!TextUtils.isEmpty(pet.name)){
            tv.setText(pet.name);
        }
    }

    private void setUpSex(TextView tv, Pet pet){
        if(!TextUtils.isEmpty(pet.sex)){
            tv.setText(pet.getSex());
        }
    }

    private void setUpAge(TextView tv, Pet pet){
        if(!TextUtils.isEmpty(pet.sex)){

            tv.setText(pet.age + " " +  getResources().getString(pet.getAgeUnit()) );
//            String age;
//            if(G.lang == C.LangEng )
//                age = getContext().getResources().getString(R.string.age1);
//            else if ( G.lang == C.LangNed )
//                age = getContext().getResources().getString(R.string.age1_nd);
//            else
//                age = getContext().getResources().getString(R.string.age1_pa);
//            tv.setText(pet.age + " " + age );
        }
    }
}
