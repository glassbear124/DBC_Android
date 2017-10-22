package com.tevreden.dbc.tinder.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.FrameLayout;

import com.tevreden.dbc.tinder.bus.RxBus;
import com.tevreden.dbc.tinder.bus.events.TopCardMovedEvent;
import com.tevreden.dbc.tinder.utilities.DisplayUtility;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

public class TinderStackLayout extends FrameLayout {

    // region Constants
    public static final int DURATION = 300;
    // endregion

    // region Member Variables
    private PublishSubject<Integer> publishSubject = PublishSubject.create();
    private PublishSubject<View> publishTopView = PublishSubject.create();
    private CompositeSubscription compositeSubscription;
    public int screenWidth;
    public int yMultiplier;
    // endregion

    // region Constructors
    public TinderStackLayout(Context context) {
        super(context);
        init();
    }

    public TinderStackLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TinderStackLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    // endregion

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        if(publishSubject != null)
            publishSubject.onNext(getChildCount());
    }

    @Override
    public void removeView(View view) {
        super.removeView(view);
        if(publishSubject != null)
            publishSubject.onNext(getChildCount());
        if(publishTopView != null)
            publishTopView.onNext(view);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        compositeSubscription.unsubscribe();
    }

    // region Helper Methods
    private void init(){
        setClipChildren(false);

        screenWidth = DisplayUtility.getScreenWidth(getContext());
        yMultiplier = DisplayUtility.dp2px(getContext(), 8);

        compositeSubscription = new CompositeSubscription();

        setUpRxBusSubscription();
    }

    private void setUpRxBusSubscription(){
        Subscription rxBusSubscription = RxBus.getInstance().toObserverable()
                .observeOn(AndroidSchedulers.mainThread()) // UI Thread
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object event) {
                        if (event == null) {
                            return;
                        }

                        if(event instanceof TopCardMovedEvent){
                            float posX = ((TopCardMovedEvent) event).getPosX();

                            int childCount = getChildCount();
                            for(int i=childCount-2; i>=0; i--){
                                TinderCardView tinderCardView = (TinderCardView) getChildAt(i);

                                if(tinderCardView != null){
                                    if(Math.abs(posX) == (float)screenWidth){
                                        float scaleValue = 1 - ((childCount-2-i)/50.0f);

                                        tinderCardView.animate()
                                            .x(0)
                                            .y((childCount-2-i) * yMultiplier)
                                            .scaleX(scaleValue)
                                            .rotation(0)
                                            .setInterpolator(new AnticipateOvershootInterpolator())
                                            .setDuration(DURATION);
                                    } else {
//                                        float multiplier =  (DisplayUtility.dp2px(getContext(), 8)) / (float)screenWidth;
//                                        float dy = -(Math.abs(posX * multiplier));
//                                        tinderCard.setTranslationY(dy);
                                    }
                                }
                            }
                        }
                    }
                });
        compositeSubscription.add(rxBusSubscription);
    }

    public PublishSubject<Integer> getPublishSubject() {
        return publishSubject;
    }

    public PublishSubject<View> getPublishViewSubject() {
        return publishTopView;
    }


    public void addCard(TinderCardView tc){
        ViewGroup.LayoutParams layoutParams; layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        int childCount = getChildCount();
        addView(tc, 0, layoutParams);

        float scaleValue = 1 - (childCount/50.0f);

        tc.animate()
            .x(0)
            .y(childCount * yMultiplier)
            .scaleX(scaleValue)
            .setInterpolator(new AnticipateOvershootInterpolator())
            .setDuration(DURATION);
    }
    // endregion
}
